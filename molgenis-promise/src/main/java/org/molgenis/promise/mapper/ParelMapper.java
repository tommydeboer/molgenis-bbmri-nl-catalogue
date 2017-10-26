package org.molgenis.promise.mapper;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.data.jobs.Progress;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.DynamicEntity;
import org.molgenis.promise.PromiseMapperType;
import org.molgenis.promise.client.PromiseDataParser;
import org.molgenis.promise.model.BbmriNlCheatSheet;
import org.molgenis.promise.model.PromiseCredentials;
import org.molgenis.promise.model.PromiseMaterialType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.hibernate.validator.util.CollectionHelper.asSet;
import static org.molgenis.promise.model.BbmriNlCheatSheet.*;
import static org.molgenis.promise.model.PromiseMaterialTypeMetadata.PROMISE_MATERIAL_TYPE;

@Component
public class ParelMapper implements PromiseMapper
{
	private static final String UNKNOWN = "Unknown";
	private final PromiseDataParser promiseDataParser;
	private final DataService dataService;

	@Autowired
	public ParelMapper(PromiseDataParser promiseDataParser, DataService dataService)
	{
		this.promiseDataParser = requireNonNull(promiseDataParser);
		this.dataService = requireNonNull(dataService);
	}

	@Override
	public PromiseMapperType getType()
	{
		return PromiseMapperType.PAREL;
	}

	@Override
	@Transactional
	public int map(Progress progress, PromiseCredentials promiseCredentials, String biobankId) throws IOException
	{
		progress.status("Mapping biobank from ProMISe with id " + biobankId);

		EntityType targetEntityMetaData = requireNonNull(dataService.getEntityType(SAMPLE_COLLECTIONS_ENTITY));

		// Parse biobanks
		promiseDataParser.parse(promiseCredentials, 0, promiseBiobankEntity ->
		{

			// find out if a sample collection with this id already exists
			Entity targetEntity = dataService.findOneById(SAMPLE_COLLECTIONS_ENTITY, biobankId);

			boolean biobankExists = true;
			if (targetEntity == null)
			{
				targetEntity = new DynamicEntity(targetEntityMetaData);

				// fill hand coded fields with dummy data the first time this biobank is added
				targetEntity.set(CONTACT_PERSON, singletonList(getTempPerson())); // mref
				targetEntity.set(PRINCIPAL_INVESTIGATORS, singletonList(getTempPerson())); // mref
				targetEntity.set(INSTITUTES, singletonList(getTempJuristicPerson())); // mref
				targetEntity.set(DISEASE, singletonList(getTempDisease())); // mref
				targetEntity.set(OMICS, singletonList(getTempOmics())); // mref
				targetEntity.set(DATA_CATEGORIES, singletonList(getTempDataCategories())); // mref

				targetEntity.set(NAME, null); // nillable
				targetEntity.set(ACRONYM, null); // nillable
				targetEntity.set(DESCRIPTION, null); // nillable
				targetEntity.set(PUBLICATIONS, null); // nillable
				targetEntity.set(BIOBANKS, null); // nillable
				targetEntity.set(WEBSITE, "http://www.parelsnoer.org/"); // nillable
				targetEntity.set(BIOBANK_SAMPLE_ACCESS_FEE, null); // nillable
				targetEntity.set(BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS, null); // nillable
				targetEntity.set(BIOBANK_SAMPLE_ACCESS_DESCRIPTION, null); // nillable
				targetEntity.set(BIOBANK_SAMPLE_ACCESS_URI, "http://www.parelsnoer.org/page/Onderzoeker"); // nillable
				targetEntity.set(BIOBANK_DATA_ACCESS_FEE, null); // nillable
				targetEntity.set(BIOBANK_DATA_ACCESS_JOINT_PROJECTS, null); // nillable
				targetEntity.set(BIOBANK_DATA_ACCESS_DESCRIPTION, null); // nillable
				targetEntity.set(BIOBANK_DATA_ACCESS_URI, "http://www.parelsnoer.org/page/Onderzoeker"); // nillable

				biobankExists = false;
			}

			// map data from ProMISe
			targetEntity.set(BbmriNlCheatSheet.ID, biobankId);
			targetEntity.set(TYPE, toTypes(promiseBiobankEntity.get("COLLECTION_TYPE"))); // mref
			targetEntity.set(MATERIALS, toMaterialTypes(promiseBiobankEntity.get("MATERIAL_TYPES"))); // mref
			targetEntity.set(SEX, toGenders(promiseBiobankEntity.get("SEX"))); // mref
			targetEntity.set(AGE_LOW, Integer.valueOf(promiseBiobankEntity.get("AGE_LOW"))); // nillable
			targetEntity.set(AGE_HIGH, Integer.valueOf(promiseBiobankEntity.get("AGE_HIGH"))); // nillable
			targetEntity.set(AGE_UNIT, toAgeType(promiseBiobankEntity.get("AGE_UNIT")));
			targetEntity.set(NUMBER_OF_DONORS, Integer.valueOf(promiseBiobankEntity.get("NUMBER_DONORS"))); // nillable

			if (biobankExists)
			{
				progress.status("Updating Sample Collection with id " + targetEntity.getIdValue());
				dataService.update(SAMPLE_COLLECTIONS_ENTITY, targetEntity);
			}
			else
			{
				progress.status("Adding new Sample Collection with id " + targetEntity.getIdValue());
				dataService.add(SAMPLE_COLLECTIONS_ENTITY, targetEntity);
			}
		});
		return 1;
	}

	Iterable<Entity> toMaterialTypes(String promiseMaterialTypeString)
	{
		Set<Object> promiseIds = asSet(promiseMaterialTypeString.split(","));
		List<PromiseMaterialType> foundMaterialTypes = findPromiseMaterialTypes(promiseIds);

		Set<Object> miabisIds = foundMaterialTypes.stream()
												  .flatMap(PromiseMaterialType::getMiabisMaterialTypes)
												  .map(type -> (Object) type)
												  .collect(toSet());
		return findMiabisMaterialTypes(miabisIds);
	}

	private List<Entity> findMiabisMaterialTypes(Set<Object> miabisMaterialTypeIds)
	{
		List<Entity> foundMiabisMaterialTypes = dataService.findAll(REF_MATERIAL_TYPES, miabisMaterialTypeIds.stream())
														   .collect(toList());
		Set<Object> foundMiabisIds = foundMiabisMaterialTypes.stream().map(Entity::getIdValue).collect(toSet());

		miabisMaterialTypeIds.removeAll(foundMiabisIds);

		if (!miabisMaterialTypeIds.isEmpty())
		{
			throw new MolgenisDataException(
					format("MIABIS material type(s) %s not found in [%s]", miabisMaterialTypeIds, REF_MATERIAL_TYPES));
		}
		return foundMiabisMaterialTypes;
	}

	private List<PromiseMaterialType> findPromiseMaterialTypes(Set<Object> promiseMaterialTypeIds)
	{
		List<PromiseMaterialType> foundMaterialTypes = dataService.findAll(PROMISE_MATERIAL_TYPE,
				promiseMaterialTypeIds.stream(), PromiseMaterialType.class).collect(toList());

		Set<Object> foundIds = foundMaterialTypes.stream().map(PromiseMaterialType::getId).collect(toSet());
		promiseMaterialTypeIds.removeAll(foundIds);

		if (!promiseMaterialTypeIds.isEmpty())
		{
			throw new MolgenisDataException(
					format("ProMISe material type(s) %s not found in [%s]", promiseMaterialTypeIds,
							PROMISE_MATERIAL_TYPE));
		}
		return foundMaterialTypes;
	}

	private Object getTempDataCategories()
	{
		return dataService.findOneById(REF_DATA_CATEGORY_TYPES, "NAV");
	}

	private Entity getTempOmics()
	{
		return dataService.findOneById(REF_EXP_DATA_TYPES, "NAV");
	}

	private Entity getTempDisease()
	{
		return dataService.findOneById(REF_DISEASE_TYPES, "NI");
	}

	private Entity getTempPerson()
	{
		Entity person = dataService.findOneById(REF_PERSONS, UNKNOWN);
		if (person == null)
		{
			EntityType personsMetaData = requireNonNull(dataService.getEntityType(REF_PERSONS));
			person = new DynamicEntity(personsMetaData);

			person.set("id", UNKNOWN);
			person.set("last_name", UNKNOWN);
			person.set("country", dataService.findOneById(REF_COUNTRIES, "NL"));
			dataService.add(REF_PERSONS, person);
		}

		return person;
	}

	private Entity getTempJuristicPerson()
	{
		Entity person = dataService.findOneById(REF_JURISTIC_PERSONS, UNKNOWN);
		if (person == null)
		{
			EntityType personsMetaData = requireNonNull(dataService.getEntityType(REF_JURISTIC_PERSONS));
			person = new DynamicEntity(personsMetaData);

			person.set("id", UNKNOWN);
			person.set("name", UNKNOWN);
			person.set("country", dataService.findOneById(REF_COUNTRIES, "NL"));
			dataService.add(REF_JURISTIC_PERSONS, person);
		}

		return person;
	}

	private Entity toAgeType(String ageType)
	{
		return dataService.findOneById(REF_AGE_TYPES, ageType);
	}

	private Iterable<Entity> toGenders(String promiseSex)
	{
		Object[] sexes = promiseSex.split(",");
		Stream<Object> ids = Arrays.stream(sexes);

		Iterable<Entity> genderTypes = dataService.findAll(REF_SEX_TYPES, ids).collect(toList());
		if (!genderTypes.iterator().hasNext())
		{
			throw new MolgenisDataException("Unknown '" + REF_SEX_TYPES + "' [" + ids.toString() + "]");
		}
		return genderTypes;
	}

	private Iterable<Entity> toTypes(String promiseTypes)
	{
		Object[] types = promiseTypes.split(",");
		Stream<Object> ids = Arrays.stream(types);

		Iterable<Entity> collectionTypes = dataService.findAll(REF_COLLECTION_TYPES, ids).collect(toList());

		if (!collectionTypes.iterator().hasNext())
		{
			throw new MolgenisDataException("Unknown '" + REF_COLLECTION_TYPES + "' [" + promiseTypes + "]");
		}
		return collectionTypes;
	}
}
