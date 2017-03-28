package org.molgenis.promise.mapper;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityManager;
import org.molgenis.data.meta.model.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.hash.Hashing.md5;
import static java.nio.charset.Charset.forName;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.molgenis.data.EntityManager.CreationMode.NO_POPULATE;
import static org.molgenis.data.EntityManager.CreationMode.POPULATE;
import static org.molgenis.promise.mapper.RadboudMapper.XML_IDAA;
import static org.molgenis.promise.mapper.RadboudMapper.getBiobankId;
import static org.molgenis.promise.model.BbmriNlCheatSheet.*;

class RadboudBiobankMapper
{
	private static final Logger LOG = LoggerFactory.getLogger(RadboudBiobankMapper.class);

	static final String XML_TITLE = "TITEL";
	static final String XML_DESCRIPTION = "OMSCHRIJVING";
	static final String XML_CONTACT_PERSON = "CONTACTPERS";
	static final String XML_ADDRESS1 = "ADRES1";
	static final String XML_ADDRESS2 = "ADRES2";
	static final String XML_ZIP_CODE = "POSTCODE";
	static final String XML_LOCATION = "PLAATS";
	static final String XML_EMAIL = "EMAIL";
	static final String XML_PHONE = "TELEFOON";
	static final String XML_TYPEBIOBANK = "TYPEBIOBANK";

	static final String ACCESS_URI = "http://www.radboudbiobank.nl/nl/collecties/materiaal-opvragen/";

	private DataService dataService;

	private Entity countryNl;
	private Entity ageType;
	private Entity rbbBiobank;
	private Entity juristicPerson;
	private EntityType personMetaData;
	private EntityManager entityManager;

	RadboudBiobankMapper(DataService dataService, EntityManager entityManager)
	{
		this.dataService = requireNonNull(dataService);
		this.entityManager = requireNonNull(entityManager);

		// cache entities that will be used more than once
		countryNl = requireNonNull(dataService.findOneById(REF_COUNTRIES, "NL"));
		ageType = requireNonNull(dataService.findOneById(REF_AGE_TYPES, "YEAR"));
		rbbBiobank = requireNonNull(dataService.findOneById(REF_BIOBANKS, "RBB"));
		juristicPerson = requireNonNull(dataService.findOneById(REF_JURISTIC_PERSONS, "83"));
		personMetaData = requireNonNull(dataService.getEntityType(REF_PERSONS));
	}

	/**
	 * Creates a new BBMRI Sample Collection entity from a Radboud Biobank entity.
	 *
	 * @param radboudBiobankEntity the Radboud Biobank Entity to turn into a BBMRI NL Sample Collection
	 * @param samples              a map of Radboud Samples
	 * @param diseases             a map of Radboud Disease Types
	 * @return a mapped BBMRI Sample Collection entity
	 */
	Entity mapNewBiobank(Map<String, String> radboudBiobankEntity, RadboudSampleMap samples, RadboudDiseaseMap diseases)
	{
		EntityType targetEntityMetaData = requireNonNull(dataService.getEntityType(SAMPLE_COLLECTIONS_ENTITY));

		Entity newSampleCollection = entityManager.create(targetEntityMetaData, POPULATE);

		// these fields are only set on first create, users will update them manually
		newSampleCollection.set(ACRONYM, null);
		newSampleCollection.set(PUBLICATIONS, null);
		newSampleCollection.set(BIOBANK_SAMPLE_ACCESS_URI, ACCESS_URI);
		newSampleCollection.set(WEBSITE, "http://www.radboudbiobank.nl/");
		newSampleCollection.set(BIOBANK_DATA_ACCESS_URI, ACCESS_URI);
		newSampleCollection.set(PRINCIPAL_INVESTIGATORS, getPrincipalInvestigator(getBiobankId(radboudBiobankEntity)));
		newSampleCollection.set(INSTITUTES, newArrayList(juristicPerson));

		return mapExistingBiobank(radboudBiobankEntity, samples, diseases, newSampleCollection);
	}

	/**
	 * Updates an existing BBMRI Sample Collection entity with data from a Radboud Biobank Entity.
	 *
	 * @param radboudBiobankEntity     the entity from which to collect data
	 * @param samples                  a map of Radboud Samples
	 * @param diseases                 a map of Radboud Disease Types
	 * @param existingSampleCollection a BBMRI Sample Collection entity
	 * @return a mapped BBMRI Sample Collection entity
	 */
	Entity mapExistingBiobank(Map<String, String> radboudBiobankEntity, RadboudSampleMap samples,
			RadboudDiseaseMap diseases, Entity existingSampleCollection)
	{
		String biobankId = getBiobankId(radboudBiobankEntity);

		// these fields are fetched from Promise and will always be overwritten
		existingSampleCollection.set(ID, biobankId);
		existingSampleCollection.set(NAME, radboudBiobankEntity.get(XML_TITLE));
		existingSampleCollection.set(TYPE, getTypes(radboudBiobankEntity.get(XML_TYPEBIOBANK)));
		existingSampleCollection.set(DATA_CATEGORIES, samples.getDataCategories(radboudBiobankEntity));
		existingSampleCollection.set(MATERIALS, samples.getMaterials(biobankId));
		existingSampleCollection.set(OMICS, samples.getOmics(biobankId));
		existingSampleCollection.set(SEX, samples.getSex(biobankId));
		existingSampleCollection.set(AGE_LOW, samples.getAgeMin(biobankId));
		existingSampleCollection.set(AGE_HIGH, samples.getAgeMax(biobankId));
		existingSampleCollection.set(AGE_UNIT, ageType);
		existingSampleCollection.set(DISEASE, diseases.getDiseaseTypes(radboudBiobankEntity.get(XML_IDAA)));
		existingSampleCollection.set(NUMBER_OF_DONORS, samples.getSize(biobankId));
		existingSampleCollection.set(DESCRIPTION, radboudBiobankEntity.get(XML_DESCRIPTION));
		existingSampleCollection.set(CONTACT_PERSON, getContactPersons(radboudBiobankEntity));
		existingSampleCollection.set(BIOBANKS, newArrayList(rbbBiobank));
		existingSampleCollection.set(BIOBANK_SAMPLE_ACCESS_FEE, true);
		existingSampleCollection.set(BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS, true);
		existingSampleCollection.set(BIOBANK_SAMPLE_ACCESS_DESCRIPTION, null);  // Don't fill in
		existingSampleCollection.set(BIOBANK_DATA_ACCESS_FEE, true);
		existingSampleCollection.set(BIOBANK_DATA_ACCESS_JOINT_PROJECTS, true);
		existingSampleCollection.set(BIOBANK_DATA_ACCESS_DESCRIPTION, null);  // Don't fill in

		return existingSampleCollection;
	}

	private Iterable<Entity> getPrincipalInvestigator(String biobankId)
	{
		Entity principalInvestigatorEntity = dataService.findOneById(REF_PERSONS, biobankId);
		if (principalInvestigatorEntity == null)
		{
			principalInvestigatorEntity = entityManager.create(personMetaData, NO_POPULATE);
			principalInvestigatorEntity.set(ID, biobankId);
			principalInvestigatorEntity.set(COUNTRY, countryNl);
			dataService.add(REF_PERSONS, principalInvestigatorEntity);
			LOG.info("Added new principal investigator (person) with id {}", biobankId);
		}
		return singletonList(principalInvestigatorEntity);
	}

	private Iterable<Entity> getContactPersons(Map<String, String> biobankEntity)
	{
		String[] contactPerson = biobankEntity.get(XML_CONTACT_PERSON).split(",");
		String address1 = biobankEntity.get(XML_ADDRESS1);
		String address2 = biobankEntity.get(XML_ADDRESS2);
		String postalCode = biobankEntity.get(XML_ZIP_CODE);
		String city = biobankEntity.get(XML_LOCATION);
		String[] email = biobankEntity.get(XML_EMAIL).split(" ");
		String phoneNumber = biobankEntity.get(XML_PHONE);

		List<Entity> persons = newArrayList();
		for (int i = 0; i < contactPerson.length; i++)
		{
			StringBuilder contentBuilder = new StringBuilder();
			if (contactPerson[i] != null && !contactPerson[i].isEmpty()) contentBuilder.append(contactPerson[i]);
			if (address1 != null && !address1.isEmpty()) contentBuilder.append(address1);
			if (address2 != null && !address2.isEmpty()) contentBuilder.append(address2);
			if (postalCode != null && !postalCode.isEmpty()) contentBuilder.append(postalCode);
			if (city != null && !city.isEmpty()) contentBuilder.append(city);
			if (email[i] != null && !email[i].isEmpty()) contentBuilder.append(email[i]);
			if (phoneNumber != null && !phoneNumber.isEmpty()) contentBuilder.append(phoneNumber);

			String personId = md5().newHasher().putString(contentBuilder, forName("UTF-8")).hash().toString();
			Entity person = dataService.findOneById(REF_PERSONS, personId);

			if (person != null)
			{
				persons.add(person);
			}
			else
			{
				Entity newPerson = entityManager.create(personMetaData, POPULATE);
				newPerson.set(ID, personId);
				newPerson.set(FIRST_NAME, contactPerson[i]);
				newPerson.set(LAST_NAME, contactPerson[i]);
				newPerson.set(PHONE, phoneNumber);
				newPerson.set(EMAIL, email[i]);

				StringBuilder addressBuilder = new StringBuilder();
				if (address1 != null && !address1.isEmpty()) addressBuilder.append(address1);
				if (address2 != null && !address2.isEmpty())
				{
					if (address1 != null && !address1.isEmpty()) addressBuilder.append(' ');
					addressBuilder.append(address2);
				}
				if (addressBuilder.length() > 0)
				{
					newPerson.set(ADDRESS, addressBuilder.toString());
				}
				newPerson.set(ZIP, postalCode);
				newPerson.set(CITY, city);
				newPerson.set(COUNTRY, countryNl);
				dataService.add(REF_PERSONS, newPerson);
				LOG.info("Added new contact person: {}", contactPerson[i]);
				persons.add(newPerson);
			}

		}
		return persons;
	}

	private Iterable<Entity> getTypes(String radboudTypeBiobank)
	{
		String collectionTypeId;
		if (radboudTypeBiobank == null || radboudTypeBiobank.isEmpty())
		{
			collectionTypeId = "OTHER";
		}
		else
		{
			switch (radboudTypeBiobank)
			{
				case "0":
					collectionTypeId = "OTHER";
					break;
				case "1":
					collectionTypeId = "DISEASE_SPECIFIC";
					break;
				case "2":
					collectionTypeId = "POPULATION_BASED";
					break;
				default:
					throw new RuntimeException("Unknown biobank type [" + radboudTypeBiobank + "]");
			}
		}
		Entity collectionType = dataService.findOneById(REF_COLLECTION_TYPES, collectionTypeId);
		if (collectionType == null)
		{
			throw new RuntimeException("Unknown '" + REF_COLLECTION_TYPES + "' [" + collectionTypeId + "]");
		}
		return singletonList(collectionType);
	}
}
