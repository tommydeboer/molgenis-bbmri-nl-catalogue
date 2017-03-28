package org.molgenis.promise.mapper;

import com.google.common.collect.Iterables;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.DynamicEntity;
import org.molgenis.promise.client.PromiseDataParser;
import org.molgenis.promise.mapper.MappingReport.Status;
import org.molgenis.promise.model.BbmriNlCheatSheet;
import org.molgenis.promise.model.PromiseCredentials;
import org.molgenis.promise.model.PromiseMappingProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.molgenis.promise.model.BbmriNlCheatSheet.*;

@Component
public class ParelMapper implements PromiseMapper, ApplicationListener<ContextRefreshedEvent>
{
	private final String MAPPER_ID = "PAREL";

	private PromiseMapperFactory promiseMapperFactory;
	private PromiseDataParser promiseDataParser;
	private DataService dataService;

	private static final Logger LOG = LoggerFactory.getLogger(ParelMapper.class);

	private static final HashMap<String, List<String>> materialTypesMap;
	private static final List<String> unknownMaterialTypes = newArrayList();
	private static final List<String> materialTypeIds = newArrayList();

	static
	{
		materialTypesMap = new HashMap<>();
		materialTypesMap.put("bloed", singletonList("WHOLE BLOOD"));
		materialTypesMap.put("bloedplasma", singletonList("PLASMA"));
		materialTypesMap.put("bloedplasma (EDTA)", singletonList("PLASMA"));
		materialTypesMap.put("bloedserum", singletonList("SERUM"));
		materialTypesMap.put("DNA uit beenmergcellen", singletonList("DNA"));
		materialTypesMap.put("DNA uit bloedcellen", singletonList("DNA"));
		materialTypesMap.put("feces", singletonList("FECES"));
		materialTypesMap.put("gastrointestinale mucosa", singletonList("TISSUE_FROZEN"));
		materialTypesMap.put("liquor (CSF)", singletonList("OTHER"));
		materialTypesMap.put("mononucleaire celfractie uit beenmerg", singletonList("OTHER"));
		materialTypesMap.put("mononucleaire celfractie uit bloed", singletonList("PERIPHERAL_BLOOD_CELLS"));
		materialTypesMap.put("RNA uit beenmergcellen", singletonList("RNA"));
		materialTypesMap.put("RNA uit bloedcellen", singletonList("RNA"));
		materialTypesMap.put("serum", singletonList("SERUM"));
		materialTypesMap.put("urine", singletonList("URINE"));
		materialTypesMap.put("weefsel", asList("TISSUE_FROZEN", "TISSUE_PARAFFIN_EMBEDDED"));
	}

	private static final HashMap<String, List<String>> tissueTypesMap;

	static
	{
		tissueTypesMap = new HashMap<>();
		tissueTypesMap.put("1", singletonList("TISSUE_PARAFFIN_EMBEDDED"));
		tissueTypesMap.put("2", singletonList("TISSUE_FROZEN"));
		tissueTypesMap.put("9", singletonList("OTHER"));
	}

	@Autowired
	public ParelMapper(PromiseMapperFactory promiseMapperFactory, PromiseDataParser promiseDataParser,
			DataService dataService)
	{
		this.promiseMapperFactory = requireNonNull(promiseMapperFactory);
		this.promiseDataParser = requireNonNull(promiseDataParser);
		this.dataService = requireNonNull(dataService);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent arg0)
	{
		promiseMapperFactory.registerMapper(MAPPER_ID, this);
	}

	@Override
	public String getId()
	{
		return MAPPER_ID;
	}

	@Override
	public MappingReport map(PromiseMappingProject promiseMappingProject)
	{
		requireNonNull(promiseMappingProject);
		MappingReport report = new MappingReport();

		try
		{
			LOG.info("Getting data from ProMISe for " + promiseMappingProject.getString("name"));
			PromiseCredentials promiseCredentials = promiseMappingProject.getPromiseCredentials();
			EntityType targetEntityMetaData = requireNonNull(dataService.getEntityType(SAMPLE_COLLECTIONS_ENTITY));

			// Parse biobanks
			promiseDataParser.parse(promiseCredentials, 0, promiseBiobankEntity ->
			{

				// find out if a sample collection with this id already exists
				Entity targetEntity = dataService
						.findOneById(SAMPLE_COLLECTIONS_ENTITY, promiseMappingProject.getString(BIOBANK_ID));

				boolean biobankExists = true;
				if (targetEntity == null)
				{
					targetEntity = new DynamicEntity(targetEntityMetaData);

					// fill hand coded fields with dummy data the first time this biobank is added
					targetEntity.set(CONTACT_PERSON, singletonList(getTempPerson(REF_PERSONS))); // mref
					targetEntity.set(PRINCIPAL_INVESTIGATORS, singletonList(getTempPerson(REF_PERSONS))); // mref
					targetEntity.set(INSTITUTES, singletonList(getTempPerson(REF_JURISTIC_PERSONS))); // mref
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
					targetEntity
							.set(BIOBANK_SAMPLE_ACCESS_URI, "http://www.parelsnoer.org/page/Onderzoeker"); // nillable
					targetEntity.set(BIOBANK_DATA_ACCESS_FEE, null); // nillable
					targetEntity.set(BIOBANK_DATA_ACCESS_JOINT_PROJECTS, null); // nillable
					targetEntity.set(BIOBANK_DATA_ACCESS_DESCRIPTION, null); // nillable
					targetEntity.set(BIOBANK_DATA_ACCESS_URI, "http://www.parelsnoer.org/page/Onderzoeker"); // nillable

					biobankExists = false;
				}

				// map data from ProMISe
				targetEntity.set(BbmriNlCheatSheet.ID, promiseMappingProject.getString(BIOBANK_ID));
				targetEntity.set(TYPE, toTypes(promiseBiobankEntity.get("COLLECTION_TYPE"))); // mref
				targetEntity.set(MATERIALS, getMaterialTypes(promiseCredentials)); // mref
				targetEntity.set(SEX, toGenders(promiseBiobankEntity.get("SEX"))); // mref
				targetEntity.set(AGE_LOW, promiseBiobankEntity.get("AGE_LOW")); // nillable
				targetEntity.set(AGE_HIGH, promiseBiobankEntity.get("AGE_HIGH")); // nillable
				targetEntity.set(AGE_UNIT, toAgeType(promiseBiobankEntity.get("AGE_UNIT")));
				targetEntity.set(NUMBER_OF_DONORS, promiseBiobankEntity.get("NUMBER_DONORS")); // nillable

				if (biobankExists)
				{
					LOG.info("Updating Sample Collection with id " + targetEntity.getIdValue());
					dataService.update(SAMPLE_COLLECTIONS_ENTITY, targetEntity);
				}
				else
				{
					LOG.info("Adding new Sample Collection with id " + targetEntity.getIdValue());
					dataService.add(SAMPLE_COLLECTIONS_ENTITY, targetEntity);
				}

				report.setStatus(Status.SUCCESS);
			});
		}
		catch (Exception e)
		{
			report.setStatus(Status.ERROR);
			report.setMessage(e.getMessage());

			LOG.error("Something went wrong: {}", e);
		}

		return report;
	}

	private Iterable<Entity> getMaterialTypes(PromiseCredentials credentials)
	{
		try
		{
			// Parse samples
			promiseDataParser.parse(credentials, 1,
					promiseSampleEntity -> toMaterialTypes(promiseSampleEntity.get("MATERIAL_TYPES"),
							promiseSampleEntity.get("MATERIAL_TYPES_SUB")));
		}
		catch (IOException e)
		{
			LOG.error("Something went wrong: {}", e);
		}

		if (!unknownMaterialTypes.isEmpty()) throw new RuntimeException(
				"Unknown ProMISe material types: [" + String.join(",", unknownMaterialTypes) + "]");

		Iterable<Entity> materialTypes = dataService
				.findAll(REF_MATERIAL_TYPES, transform(materialTypeIds, id -> (Object) id).stream())
				.collect(Collectors.toList());

		if (Iterables.isEmpty(materialTypes))
			throw new RuntimeException("Couldn't find mappings for some of the material types in:" + materialTypeIds);
		return materialTypes;
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

	private Entity getTempPerson(String targetRefEntity)
	{
		Entity person = dataService.findOneById(targetRefEntity, "Unknown");
		if (person == null)
		{
			EntityType personsMetaData = requireNonNull(dataService.getEntityType(targetRefEntity));
			person = new DynamicEntity(personsMetaData);

			person.set("id", "Unknown");
			person.set("name", "Unknown");
			person.set("country", dataService.findOneById(REF_COUNTRIES, "NL"));
			dataService.add(targetRefEntity, person);
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
			throw new RuntimeException("Unknown '" + REF_SEX_TYPES + "' [" + ids.toString() + "]");
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
			throw new RuntimeException("Unknown '" + REF_COLLECTION_TYPES + "' [" + promiseTypes + "]");
		}
		return collectionTypes;
	}

	private void toMaterialTypes(String type, String tissue)
	{
		if (type.equals("weefsel") && tissue != null)
		{
			if (tissueTypesMap.containsKey(tissue)) materialTypeIds.addAll(tissueTypesMap.get(tissue));
			else unknownMaterialTypes.add(tissue);
		}
		else
		{
			if (materialTypesMap.containsKey(type)) materialTypeIds.addAll(materialTypesMap.get(type));
			else unknownMaterialTypes.add(type);
		}
	}
}
