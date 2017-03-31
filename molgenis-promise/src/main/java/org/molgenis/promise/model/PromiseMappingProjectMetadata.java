package org.molgenis.promise.model;

import org.molgenis.data.meta.SystemEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.Objects.requireNonNull;
import static org.molgenis.data.meta.AttributeType.XREF;
import static org.molgenis.data.meta.model.EntityType.AttributeRole.ROLE_ID;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;
import static org.molgenis.promise.model.PromisePackage.PACKAGE_PROMISE;

@Component
public class PromiseMappingProjectMetadata extends SystemEntityType
{
	public static final String SIMPLE_NAME = "PromiseMappingProject";
	public static final String PROMISE_MAPPING_PROJECT = PACKAGE_PROMISE + PACKAGE_SEPARATOR + SIMPLE_NAME;

	public static final String NAME = "name";
	public static final String BIOBANK_ID = "biobankId";
	public static final String CREDENTIALS = "credentials";
	public static final String MAPPER = "mapper";

	private final PromisePackage promisePackage;
	private final PromiseCredentialsMetadata promiseCredentialsMetaData;

	@Autowired
	public PromiseMappingProjectMetadata(PromisePackage promisePackage,
			PromiseCredentialsMetadata promiseCredentialsMetaData)
	{
		super(SIMPLE_NAME, PACKAGE_PROMISE);
		this.promisePackage = requireNonNull(promisePackage);
		this.promiseCredentialsMetaData = requireNonNull(promiseCredentialsMetaData);
	}

	@Override
	protected void init()
	{
		setLabel("ProMISe mapping projects");
		setPackage(promisePackage);

		addAttribute(NAME, ROLE_ID).setLabel("Name").setDescription("The name of this mapping");
		addAttribute(BIOBANK_ID).setNillable(false).setLabel("Biobank ID")
				.setDescription("The ID of the biobank in the BBMRI-NL Sample Collections entity").setUnique(true);
		addAttribute(CREDENTIALS).setDataType(XREF).setRefEntity(promiseCredentialsMetaData).setNillable(false)
				.setLabel("Credentials").setDescription("The ProMISe credentials for this biobank");
		addAttribute(MAPPER).setNillable(false).setLabel("Mapper")
				.setDescription("The mapper to use for this biobank");
	}

	@Override
	public Set<SystemEntityType> getDependencies()
	{
		return singleton(promiseCredentialsMetaData);
	}
}
