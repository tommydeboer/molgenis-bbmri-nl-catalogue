package org.molgenis.app;

import com.google.api.client.util.Lists;
import org.molgenis.app.controller.BackgroundController;
import org.molgenis.app.controller.HomeController;
import org.molgenis.auth.*;
import org.molgenis.bootstrap.populate.SystemEntityRegistry;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.i18n.model.LanguageMetadata;
import org.molgenis.data.importer.wizard.ImportWizardController;
import org.molgenis.data.settings.SettingsPackage;
import org.molgenis.data.support.QueryImpl;
import org.molgenis.data.system.core.FreemarkerTemplateMetaData;
import org.molgenis.dataexplorer.controller.DataExplorerController;
import org.molgenis.security.account.AccountService;
import org.molgenis.security.core.utils.SecurityUtils;
import org.molgenis.ui.admin.user.UserAccountController;
import org.molgenis.ui.controller.FeedbackController;
import org.molgenis.ui.controller.RedirectController;
import org.molgenis.ui.jobs.ScheduledJobsController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.google.api.client.util.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.data.meta.model.AttributeMetadata.ATTRIBUTE_META_DATA;
import static org.molgenis.data.meta.model.EntityTypeMetadata.ENTITY_TYPE_META_DATA;
import static org.molgenis.data.meta.model.EntityTypeMetadata.PACKAGE;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;
import static org.molgenis.promise.model.BbmriNlCheatSheet.*;
import static org.molgenis.security.core.utils.SecurityUtils.*;

/**
 * Registry of application system entities to be added to an empty database.
 */
@Component
public class WebAppSystemEntityRegistry implements SystemEntityRegistry
{
	private static final String DATA_EXPLORER_SETTINGS =
			SettingsPackage.PACKAGE_SETTINGS + PACKAGE_SEPARATOR + DataExplorerController.ID;
	private static final List<String> DATA_MANAGER_ENTITY_READ_AUTHORITIES = Arrays.asList(
			FreemarkerTemplateMetaData.FREEMARKER_TEMPLATE, REF_DATA_CATEGORY_TYPES, REF_AGE_TYPES,
			REF_COLLECTION_TYPES, REF_COUNTRIES, REF_DISEASE_TYPES, REF_EXP_DATA_TYPES, REF_MATERIAL_TYPES,
			REF_SAMPLE_SIZE_TYPES, REF_SEX_TYPES, REF_STAFF_SIZE_TYPES, DATA_EXPLORER_SETTINGS);
	private static final List<String> DATA_MANAGER_ENTITY_WRITE_AUTHORITIES = Arrays.asList(REF_BIOBANKS,
			REF_JURISTIC_PERSONS, REF_PERSONS, REF_PUBLICATIONS, SAMPLE_COLLECTIONS_ENTITY);
	private static final List<String> DATA_MANAGER_PLUGIN_READ_AUTHORITIES = Arrays.asList(BackgroundController.ID,
			RedirectController.ID, ImportWizardController.ID);
	private static final List<String> DATA_MANAGER_PLUGIN_WRITE_AUTHORITIES = Arrays.asList(DataExplorerController.ID,
			ScheduledJobsController.ID);
	private static final List<String> ALL_USERS_ENTITY_READ_AUTHORITIES = Arrays.asList(
			FreemarkerTemplateMetaData.FREEMARKER_TEMPLATE, LanguageMetadata.LANGUAGE, DATA_EXPLORER_SETTINGS);
	private static final List<String> ALL_USERS_PLUGIN_READ_AUTHORITIES = Arrays.asList(HomeController.ID,
			RedirectController.ID);
	private static final List<String> ALL_USERS_PLUGIN_WRITE_AUTHORITIES = singletonList(UserAccountController.ID);
	private static final List<String> ANONYMOUS_USER_PLUGIN_READ_AUTHORITIES = Arrays.asList(HomeController.ID,
			RedirectController.ID, FeedbackController.ID, DataExplorerController.ID);
	private static final List<String> ANONYMOUS_USER_ENTITY_READ_AUTHORITIES = Arrays.asList(
			FreemarkerTemplateMetaData.FREEMARKER_TEMPLATE, REF_AGE_TYPES, ENTITY_TYPE_META_DATA, ATTRIBUTE_META_DATA,
			PACKAGE, REF_BIOBANKS, REF_COLLECTION_TYPES, REF_COUNTRIES, REF_DATA_CATEGORY_TYPES, REF_DISEASE_TYPES,
			REF_EXP_DATA_TYPES, REF_JURISTIC_PERSONS, REF_MATERIAL_TYPES, LanguageMetadata.LANGUAGE, REF_PERSONS,
			REF_PUBLICATIONS, SAMPLE_COLLECTIONS_ENTITY, REF_SAMPLE_SIZE_TYPES, REF_SEX_TYPES, REF_STAFF_SIZE_TYPES,
			DATA_EXPLORER_SETTINGS);

	private final DataService dataService;
	private final UserAuthorityFactory userAuthorityFactory;
	private final GroupAuthorityFactory groupAuthorityFactory;
	private final GroupFactory groupFactory;

	@Autowired
	public WebAppSystemEntityRegistry(DataService dataService, UserAuthorityFactory userAuthorityFactory,
			GroupAuthorityFactory groupAuthorityFactory, GroupFactory groupFactory)
	{
		this.dataService = requireNonNull(dataService);
		this.userAuthorityFactory = requireNonNull(userAuthorityFactory);
		this.groupAuthorityFactory = requireNonNull(groupAuthorityFactory);
		this.groupFactory = requireNonNull(groupFactory);
	}

	@Override
	public Collection<Entity> getEntities()
	{
		Group dataManagerGroup = createDataManagerGroup();
		Group allUsersGroup = getAllUsersGroup();
		User anonymousUser = getAnonymousUser();

		List<GroupAuthority> groupAuthorities = createGroupAuthorities(dataManagerGroup, allUsersGroup);
		List<UserAuthority> userAuthorities = createUserAuthorities(anonymousUser);

		List<Entity> entities = Lists.newArrayList();
		entities.add(dataManagerGroup);
		entities.addAll(groupAuthorities);
		entities.addAll(userAuthorities);
		return entities;
	}

	private Group createDataManagerGroup()
	{
		Group dataManagerGroup = groupFactory.create();
		dataManagerGroup.setName("Data Managers");
		return dataManagerGroup;
	}

	private Group getAllUsersGroup()
	{
		return dataService.findOne(GroupMetaData.GROUP,
				new QueryImpl<Group>().eq(GroupMetaData.NAME, AccountService.ALL_USER_GROUP), Group.class);
	}

	private User getAnonymousUser()
	{
		return dataService.findOne(UserMetaData.USER,
				new QueryImpl<User>().eq(UserMetaData.USERNAME, SecurityUtils.ANONYMOUS_USERNAME), User.class);
	}

	private List<UserAuthority> createUserAuthorities(User anonymousUser)
	{
		List<UserAuthority> userAuthorities = newArrayList();

		ANONYMOUS_USER_ENTITY_READ_AUTHORITIES.stream()
											  .map(entity -> createUserAuthority(anonymousUser,
													  AUTHORITY_ENTITY_READ_PREFIX + entity))
											  .forEach(userAuthorities::add);

		ANONYMOUS_USER_PLUGIN_READ_AUTHORITIES.stream()
											  .map(plugin -> createUserAuthority(anonymousUser,
													  AUTHORITY_PLUGIN_READ_PREFIX + plugin))
											  .forEach(userAuthorities::add);
		return userAuthorities;
	}

	private List<GroupAuthority> createGroupAuthorities(Group dataManagerGroup, Group allUsersGroup)
	{
		List<GroupAuthority> groupAuthorities = newArrayList();

		DATA_MANAGER_ENTITY_READ_AUTHORITIES.stream()
											.map(entity -> createGroupAuthority(dataManagerGroup,
													AUTHORITY_ENTITY_READ_PREFIX + entity))
											.forEach(groupAuthorities::add);

		DATA_MANAGER_ENTITY_WRITE_AUTHORITIES.stream()
											 .map(entity -> createGroupAuthority(dataManagerGroup,
													 AUTHORITY_ENTITY_WRITE_PREFIX + entity))
											 .forEach(groupAuthorities::add);

		DATA_MANAGER_PLUGIN_READ_AUTHORITIES.stream()
											.map(plugin -> createGroupAuthority(dataManagerGroup,
													AUTHORITY_PLUGIN_READ_PREFIX + plugin))
											.forEach(groupAuthorities::add);

		DATA_MANAGER_PLUGIN_WRITE_AUTHORITIES.stream()
											 .map(plugin -> createGroupAuthority(dataManagerGroup,
													 AUTHORITY_PLUGIN_WRITE_PREFIX + plugin))
											 .forEach(groupAuthorities::add);

		ALL_USERS_ENTITY_READ_AUTHORITIES.stream()
										 .map(entity -> createGroupAuthority(allUsersGroup,
												 AUTHORITY_ENTITY_READ_PREFIX + entity))
										 .forEach(groupAuthorities::add);

		ALL_USERS_PLUGIN_READ_AUTHORITIES.stream()
										 .map(plugin -> createGroupAuthority(allUsersGroup,
												 AUTHORITY_PLUGIN_READ_PREFIX + plugin))
										 .forEach(groupAuthorities::add);

		ALL_USERS_PLUGIN_WRITE_AUTHORITIES.stream()
										  .map(plugin -> createGroupAuthority(allUsersGroup,
												  AUTHORITY_PLUGIN_WRITE_PREFIX + plugin))
										  .forEach(groupAuthorities::add);
		return groupAuthorities;
	}

	private GroupAuthority createGroupAuthority(Group group, String role)
	{
		GroupAuthority groupAuthority = groupAuthorityFactory.create();
		groupAuthority.setGroup(group);
		groupAuthority.setRole(role);
		return groupAuthority;
	}

	private UserAuthority createUserAuthority(User user, String role)
	{
		UserAuthority userAuthority = userAuthorityFactory.create();
		userAuthority.setUser(user);
		userAuthority.setRole(role);
		return userAuthority;
	}
}