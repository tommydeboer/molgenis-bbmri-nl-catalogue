package org.molgenis.promise.mapper;

import com.google.common.collect.Maps;
import org.mockito.ArgumentCaptor;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityManager;
import org.molgenis.data.meta.model.Attribute;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.DynamicEntity;
import org.molgenis.promise.model.BbmriNlCheatSheet;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.molgenis.data.EntityManager.CreationMode.NO_POPULATE;
import static org.molgenis.data.EntityManager.CreationMode.POPULATE;
import static org.molgenis.data.meta.AttributeType.*;
import static org.molgenis.promise.mapper.RadboudBiobankMapper.*;
import static org.molgenis.promise.mapper.RadboudMapper.XML_ID;
import static org.molgenis.promise.mapper.RadboudMapper.XML_IDAA;
import static org.molgenis.promise.mapper.RadboudSampleMap.*;
import static org.molgenis.promise.model.BbmriNlCheatSheet.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class RadboudBiobankMapperTest
{
	private RadboudBiobankMapper radboudBiobankMapper;
	private RadboudSampleMap radboudSampleMap;
	private RadboudDiseaseMap radboudDiseaseMap;
	private Map<String, String> biobank;
	private DataService dataService;

	private final Entity NL_COUNTRY_ENTITY = mock(Entity.class);
	private final Entity JURISTIC_PERSON_83 = mock(Entity.class);
	private final Entity COLLECTION_OTHER = mock(Entity.class);
	private final Entity AGE_TYPE = mock(Entity.class);
	private final Entity RBB_BIOBANK = mock(Entity.class);
	private final Entity DISEASE_SPECIFIC = mock(Entity.class);

	private EntityType sampleCollectionsEntityType;

	private ArgumentCaptor<Entity> entityCaptor = ArgumentCaptor.forClass(Entity.class);

	@BeforeMethod
	@SuppressWarnings("unchecked")
	public void beforeMethod()
	{
		dataService = mock(DataService.class);
		radboudSampleMap = mock(RadboudSampleMap.class);
		radboudDiseaseMap = mock(RadboudDiseaseMap.class);

		sampleCollectionsEntityType = mock(EntityType.class);
		EntityType personEntityType = mock(EntityType.class);
		EntityType principalInvestigatorsEntityType = mock(EntityType.class);
		EntityType juristicPersonEntityType = mock(EntityType.class);

		Stream<Entity> resultStream = mock(Stream.class);
		List<Entity> resultEntities = mock(List.class);
		when(resultStream.collect(toList())).thenReturn(resultEntities);
		when(dataService.findAll(any(String.class), any(Stream.class))).thenReturn(resultStream);

		when(dataService.findOneById(REF_COUNTRIES, "NL")).thenReturn(NL_COUNTRY_ENTITY);
		when(dataService.getEntityType(REF_PERSONS)).thenReturn(personEntityType);
		when(dataService.getEntityType(SAMPLE_COLLECTIONS_ENTITY)).thenReturn(sampleCollectionsEntityType);
		when(dataService.findOneById(REF_JURISTIC_PERSONS, "83")).thenReturn(JURISTIC_PERSON_83);
		when(dataService.findOneById(REF_COLLECTION_TYPES, "OTHER")).thenReturn(COLLECTION_OTHER);
		when(dataService.findOneById(REF_AGE_TYPES, "YEAR")).thenReturn(AGE_TYPE);
		when(dataService.findOneById(REF_BIOBANKS, "RBB")).thenReturn(RBB_BIOBANK);
		when(dataService.findOneById(REF_COLLECTION_TYPES, "DISEASE_SPECIFIC")).thenReturn(DISEASE_SPECIFIC);

		Attribute stringAttr = mock(Attribute.class);
		when(stringAttr.getDataType()).thenReturn(STRING);
		Attribute intAttr = mock(Attribute.class);
		when(intAttr.getDataType()).thenReturn(INT);
		Attribute mrefAttr = mock(Attribute.class);
		when(mrefAttr.getDataType()).thenReturn(MREF);
		Attribute xrefAttr = mock(Attribute.class);
		when(xrefAttr.getDataType()).thenReturn(XREF);
		Attribute boolAttr = mock(Attribute.class);
		when(boolAttr.getDataType()).thenReturn(BOOL);

		Attribute principalInvestigatorAttr = mock(Attribute.class);
		when(principalInvestigatorAttr.getDataType()).thenReturn(MREF);
		when(principalInvestigatorAttr.getRefEntity()).thenReturn(principalInvestigatorsEntityType);

		Attribute institutesAttr = mock(Attribute.class);
		when(institutesAttr.getDataType()).thenReturn(MREF);
		when(institutesAttr.getRefEntity()).thenReturn(juristicPersonEntityType);

		when(sampleCollectionsEntityType.getAttribute(ID)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(ACRONYM)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(PUBLICATIONS)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_SAMPLE_ACCESS_URI)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(WEBSITE)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_DATA_ACCESS_URI)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(BbmriNlCheatSheet.PRINCIPAL_INVESTIGATORS)).thenReturn(
				principalInvestigatorAttr);
		when(sampleCollectionsEntityType.getAttribute(INSTITUTES)).thenReturn(institutesAttr);
		when(sampleCollectionsEntityType.getAttribute(NAME)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(TYPE)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(DATA_CATEGORIES)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(MATERIALS)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(OMICS)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(SEX)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(AGE_UNIT)).thenReturn(xrefAttr);
		when(sampleCollectionsEntityType.getAttribute(AGE_LOW)).thenReturn(intAttr);
		when(sampleCollectionsEntityType.getAttribute(AGE_HIGH)).thenReturn(intAttr);
		when(sampleCollectionsEntityType.getAttribute(DISEASE)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(NUMBER_OF_DONORS)).thenReturn(intAttr);
		when(sampleCollectionsEntityType.getAttribute(DESCRIPTION)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(CONTACT_PERSON)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANKS)).thenReturn(mrefAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_SAMPLE_ACCESS_FEE)).thenReturn(boolAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS)).thenReturn(boolAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_SAMPLE_ACCESS_DESCRIPTION)).thenReturn(stringAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_DATA_ACCESS_FEE)).thenReturn(boolAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_DATA_ACCESS_JOINT_PROJECTS)).thenReturn(boolAttr);
		when(sampleCollectionsEntityType.getAttribute(BIOBANK_DATA_ACCESS_DESCRIPTION)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(ID)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(FIRST_NAME)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(LAST_NAME)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(COUNTRY)).thenReturn(xrefAttr);
		when(personEntityType.getAttribute(PHONE)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(BbmriNlCheatSheet.EMAIL)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(BbmriNlCheatSheet.ADDRESS)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(BbmriNlCheatSheet.ZIP)).thenReturn(stringAttr);
		when(personEntityType.getAttribute(BbmriNlCheatSheet.CITY)).thenReturn(stringAttr);

		EntityManager entityManager = mock(EntityManager.class);
		radboudBiobankMapper = new RadboudBiobankMapper(dataService, entityManager);
		when(entityManager.create(sampleCollectionsEntityType, POPULATE)).thenReturn(
				new DynamicEntity(sampleCollectionsEntityType));
		when(entityManager.create(personEntityType, NO_POPULATE)).thenReturn(new DynamicEntity(personEntityType));
		when(entityManager.create(personEntityType, POPULATE)).thenReturn(new DynamicEntity(personEntityType));

		biobank = Maps.newHashMap();
		biobank.put(XML_ID, "9000");
		biobank.put(XML_IDAA, "1");
		biobank.put(XML_TITLE, "Inflammatoire Darmziekten");
		biobank.put(XML_DESCRIPTION, "Long description");
		biobank.put(XML_CONTACT_PERSON, "Dr. Abc de Fgh");
		biobank.put(XML_ADDRESS1, "afdeling Maag-Darm-Leverziekten");
		biobank.put(XML_ADDRESS2, "Post 123");
		biobank.put(XML_ZIP_CODE, "1234 AB");
		biobank.put(XML_LOCATION, "Poppenwier");
		biobank.put(XML_EMAIL, "abc@def.gh");
		biobank.put(XML_PHONE, "123-4567890");
		biobank.put(XML_TYPEBIOBANK, "1");
		biobank.put(XML_VOORGESCH, "1");
		biobank.put(XML_FAMANAM, "1");
		biobank.put(XML_BEHANDEL, "1");
		biobank.put(XML_FOLLOWUP, "2");
		biobank.put(XML_BEELDEN, "1");
		biobank.put(XML_VRAGENLIJST, "2");
		biobank.put(XML_OMICS, "2");
		biobank.put(XML_ROUTINEBEP, "2");
		biobank.put(XML_GWAS, "2");
		biobank.put(XML_HISTOPATH, "2");
		biobank.put(XML_OUTCOME, "2");
		biobank.put(XML_ANDERS, "2");
	}

	@Test
	public void mapNewBiobank()
	{
		Entity mappedEntity = radboudBiobankMapper.mapNewBiobank(biobank, radboudSampleMap, radboudDiseaseMap);

		assertEquals(mappedEntity.get(ACRONYM), null);
		assertEquals(mappedEntity.get(PUBLICATIONS), null);
		assertEquals(mappedEntity.get(BIOBANK_SAMPLE_ACCESS_URI), ACCESS_URI);
		assertEquals(mappedEntity.get(WEBSITE), "http://www.radboudbiobank.nl/");
		assertEquals(mappedEntity.get(BIOBANK_DATA_ACCESS_URI), ACCESS_URI);

		Iterator<Entity> investigatorIterator = mappedEntity.getEntities(BbmriNlCheatSheet.PRINCIPAL_INVESTIGATORS)
															.iterator();
		Entity investigator = investigatorIterator.next();
		assertEquals(investigator.get(ID), "9000_1");
		assertEquals(investigator.get(COUNTRY), NL_COUNTRY_ENTITY);
		assertFalse(investigatorIterator.hasNext());

		Iterator<Entity> instituteIterator = mappedEntity.getEntities(INSTITUTES).iterator();
		assertEquals(instituteIterator.next(), JURISTIC_PERSON_83);
		assertFalse(instituteIterator.hasNext());

		testDynamicMapping(mappedEntity);
		testFixedMapping(mappedEntity);
	}

	@Test
	public void mapExistingBiobank()
	{
		Entity existingCollection = new DynamicEntity(sampleCollectionsEntityType);

		String url = "http://abc.de/";

		existingCollection.set(ACRONYM, "ABC");
		existingCollection.set(PUBLICATIONS, "TEST");
		existingCollection.set(BIOBANK_SAMPLE_ACCESS_URI, url);
		existingCollection.set(WEBSITE, url);
		existingCollection.set(BIOBANK_DATA_ACCESS_URI, url);
		existingCollection.set(BbmriNlCheatSheet.PRINCIPAL_INVESTIGATORS, emptyList());
		existingCollection.set(INSTITUTES, emptyList());

		Entity mappedEntity = radboudBiobankMapper.mapExistingBiobank(biobank, radboudSampleMap, radboudDiseaseMap,
				existingCollection);

		// check that these fields aren't overwritten
		assertEquals(mappedEntity.get(ACRONYM), "ABC");
		assertEquals(mappedEntity.get(PUBLICATIONS), "TEST");
		assertEquals(mappedEntity.get(BIOBANK_SAMPLE_ACCESS_URI), url);
		assertEquals(mappedEntity.get(WEBSITE), url);
		assertEquals(mappedEntity.get(BIOBANK_DATA_ACCESS_URI), url);
		assertEquals(mappedEntity.getEntities(BbmriNlCheatSheet.PRINCIPAL_INVESTIGATORS), emptyList());
		assertEquals(mappedEntity.get(INSTITUTES), emptyList());

		testDynamicMapping(mappedEntity);
		testFixedMapping(mappedEntity);
	}

	/**
	 * Tests the portion of the mapping that is based on the input Radboud biobanks.
	 */
	private void testDynamicMapping(Entity mappedEntity)
	{
		assertEquals(mappedEntity.get(ID), "9000_1");
		assertEquals(mappedEntity.get(NAME), "Inflammatoire Darmziekten");

		Iterator<Entity> typeIterator = mappedEntity.getEntities(TYPE).iterator();
		assertEquals(typeIterator.next(), DISEASE_SPECIFIC);
		assertFalse(typeIterator.hasNext());

		verify(radboudSampleMap).getDataCategories(biobank);
		verify(radboudSampleMap).getMaterials("9000_1");
		verify(radboudSampleMap).getOmics("9000_1");
		verify(radboudSampleMap).getSex("9000_1");
		verify(radboudSampleMap).getAgeMin("9000_1");
		verify(radboudSampleMap).getAgeMax("9000_1");
		verify(radboudSampleMap).getSize("9000_1");

		verify(radboudDiseaseMap).getDiseaseTypes("1");
		assertEquals(mappedEntity.get(DESCRIPTION), "Long description");

		// verify new contact person entity is added
		verify(dataService, atLeastOnce()).findOneById(eq(REF_PERSONS), any(String.class));
		verify(dataService, atLeastOnce()).add(eq(REF_PERSONS), entityCaptor.capture());
		Iterator<Entity> contactIterator = mappedEntity.getEntities(CONTACT_PERSON).iterator();
		Entity contactPerson = contactIterator.next();
		assertFalse(contactIterator.hasNext());
		assertEquals(contactPerson, entityCaptor.getValue());
		assertEquals(contactPerson.get(FIRST_NAME), "Dr. Abc de Fgh");
	}

	/**
	 * Tests the fixed portion of the mapping (hardcoded default values).
	 */
	private void testFixedMapping(Entity mappedEntity)
	{
		Iterator<Entity> biobankIterator = mappedEntity.getEntities(BIOBANKS).iterator();
		assertEquals(biobankIterator.next(), RBB_BIOBANK);
		assertFalse(biobankIterator.hasNext());

		assertEquals(mappedEntity.get(AGE_UNIT), AGE_TYPE);
		assertEquals(mappedEntity.get(BIOBANK_SAMPLE_ACCESS_FEE), true);
		assertEquals(mappedEntity.get(BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS), true);
		assertEquals(mappedEntity.get(BIOBANK_SAMPLE_ACCESS_DESCRIPTION), null);
		assertEquals(mappedEntity.get(BIOBANK_DATA_ACCESS_FEE), true);
		assertEquals(mappedEntity.get(BIOBANK_DATA_ACCESS_JOINT_PROJECTS), true);
		assertEquals(mappedEntity.get(BIOBANK_DATA_ACCESS_DESCRIPTION), null);
	}
}