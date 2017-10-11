package org.molgenis.promise.model;

/**
 * Convenience class containing the identifiers of the BBMRI-NL MIABIS model
 */
public class BbmriNlCheatSheet
{
	private BbmriNlCheatSheet()
	{
	}

	// Entities
	public static final String SAMPLE_COLLECTIONS_ENTITY = "bbmri_nl_sample_collections";
	public static final String REF_COLLECTION_TYPES = "bbmri_nl_collection_types";
	public static final String REF_DATA_CATEGORY_TYPES = "bbmri_nl_data_category_types";
	public static final String REF_EXP_DATA_TYPES = "bbmri_nl_exp_data_types";
	public static final String REF_SEX_TYPES = "bbmri_nl_sex_types";
	public static final String REF_PERSONS = "bbmri_nl_persons";
	public static final String REF_JURISTIC_PERSONS = "bbmri_nl_juristic_persons";
	public static final String REF_DISEASE_TYPES = "bbmri_nl_disease_types";
	public static final String REF_MATERIAL_TYPES = "bbmri_nl_material_types";
	public static final String REF_AGE_TYPES = "bbmri_nl_age_types";
	public static final String REF_COUNTRIES = "bbmri_nl_countries";
	public static final String REF_BIOBANKS = "bbmri_nl_biobanks";
	public static final String REF_SAMPLE_SIZE_TYPES = "bbmri_nl_sample_size_types";
	public static final String REF_STAFF_SIZE_TYPES = "bbmri_nl_staff_size_types";
	public static final String REF_PUBLICATIONS = "bbmri_nl_publications";

	// Sample Collections Attributes
	public static final String ID = "id";
	public static final String NAME = "name";
	public static final String ACRONYM = "acronym";
	public static final String TYPE = "type";
	public static final String DISEASE = "disease";
	public static final String DATA_CATEGORIES = "data_categories";
	public static final String MATERIALS = "materials";
	public static final String OMICS = "omics";
	public static final String SEX = "sex";
	public static final String AGE_LOW = "age_low";
	public static final String AGE_HIGH = "age_high";
	public static final String AGE_UNIT = "age_unit";
	public static final String NUMBER_OF_DONORS = "numberOfDonors";
	public static final String DESCRIPTION = "description";
	public static final String PUBLICATIONS = "publications";
	public static final String CONTACT_PERSON = "contact_person";
	public static final String PRINCIPAL_INVESTIGATORS = "principal_investigators";
	public static final String INSTITUTES = "institutes";
	public static final String BIOBANKS = "biobanks";
	public static final String WEBSITE = "website";
	public static final String BIOBANK_SAMPLE_ACCESS_FEE = "sampleAccessFee";
	public static final String BIOBANK_SAMPLE_ACCESS_JOINT_PROJECTS = "sampleAccessJointProjects";
	public static final String BIOBANK_SAMPLE_ACCESS_DESCRIPTION = "sampleAccessDescription";
	public static final String BIOBANK_SAMPLE_ACCESS_URI = "sampleAccessURI";
	public static final String BIOBANK_DATA_ACCESS_FEE = "dataAccessFee";
	public static final String BIOBANK_DATA_ACCESS_JOINT_PROJECTS = "dataAccessJointProjects";
	public static final String BIOBANK_DATA_ACCESS_DESCRIPTION = "dataAccessDescription";
	public static final String BIOBANK_DATA_ACCESS_URI = "dataAccessURI";

	// Persons attributes
	public static final String COUNTRY = "country";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String PHONE = "phone";
	public static final String EMAIL = "email";
	public static final String ADDRESS = "address";
	public static final String ZIP = "zip";
	public static final String CITY = "city";
}
