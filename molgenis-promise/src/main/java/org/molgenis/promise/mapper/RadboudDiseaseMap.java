package org.molgenis.promise.mapper;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Objects.requireNonNull;
import static org.molgenis.promise.mapper.RadboudMapper.*;
import static org.molgenis.promise.model.BbmriNlCheatSheet.REF_DISEASE_TYPES;

class RadboudDiseaseMap
{
	private static final Logger LOG = LoggerFactory.getLogger(RadboudDiseaseMap.class);

	static final String URN_MIRIAM_ICD_PREFIX = "urn:miriam:icd:";
	static final String XML_IDAABB = "IDAABB";
	static final String XML_CODENAME = "CODENAME";
	static final String XML_CODEVERSION = "CODEVERSION";
	static final String XML_CODEDESCEN = "CODEDESCEN";
	static final String XML_CODEINDEX = "CODEINDEX";

	private Map<String, List<Map<String, String>>> diseases = newHashMap();
	private DataService dataService;

	private int numberOfDiseaseTypes;

	RadboudDiseaseMap(DataService dataService)
	{
		this.dataService = requireNonNull(dataService);
	}

	void addDisease(Map<String, String> radboudDiseaseEntity)
	{
		String diseaseId = radboudDiseaseEntity.get(XML_IDAA);
		diseases.putIfAbsent(diseaseId, newArrayList());
		diseases.get(diseaseId).add(radboudDiseaseEntity);
		numberOfDiseaseTypes++;
	}

	int getNumberOfDiseaseTypes()
	{
		return numberOfDiseaseTypes;
	}

	Iterable<Entity> getDiseaseTypes(String biobankIdaa)
	{
		List<Entity> diseaseTypes = newArrayList();
		Iterable<Map<String, String>> diseaseEntities = diseases.get(biobankIdaa);

		if (diseaseEntities != null)
		{
			diseaseEntities.forEach(disease -> {
				String icd10urn = URN_MIRIAM_ICD_PREFIX + disease.get(XML_CODEINDEX);
				Entity diseaseType = dataService.findOneById(REF_DISEASE_TYPES, icd10urn);
				if (diseaseType != null)
				{
					diseaseTypes.add(diseaseType);
				}
				else
				{
					LOG.info("Disease type with id [" + icd10urn + "] not found");
				}
			});
		}

		if (diseaseTypes.isEmpty())
		{
			diseaseTypes.add(dataService.findOneById(REF_DISEASE_TYPES, "NAV"));
		}
		return diseaseTypes;
	}
}


