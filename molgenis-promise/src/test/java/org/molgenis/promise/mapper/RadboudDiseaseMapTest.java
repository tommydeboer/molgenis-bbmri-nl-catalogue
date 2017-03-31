package org.molgenis.promise.mapper;

import com.google.common.collect.Lists;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.promise.mapper.RadboudDiseaseMap.*;
import static org.molgenis.promise.mapper.RadboudMapper.XML_ID;
import static org.molgenis.promise.mapper.RadboudMapper.XML_IDAA;
import static org.molgenis.promise.model.BbmriNlCheatSheet.REF_DISEASE_TYPES;
import static org.testng.Assert.assertEquals;

public class RadboudDiseaseMapTest
{
	private RadboudDiseaseMap radboudDiseaseMap;

	private Entity diseaseType1 = mock(Entity.class);
	private Entity diseaseType2 = mock(Entity.class);
	private Entity diseaseType3 = mock(Entity.class);
	private Entity diseaseTypeNAV = mock(Entity.class);

	@BeforeMethod
	@SuppressWarnings("unchecked")
	public void beforeMethod()
	{
		DataService dataService = mock(DataService.class);
		radboudDiseaseMap = new RadboudDiseaseMap(dataService);

		when(dataService.findOneById(REF_DISEASE_TYPES, URN_MIRIAM_ICD_PREFIX + "C81-C96")).thenReturn(diseaseType1);
		when(dataService.findOneById(REF_DISEASE_TYPES, URN_MIRIAM_ICD_PREFIX + "E11")).thenReturn(diseaseType2);
		when(dataService.findOneById(REF_DISEASE_TYPES, URN_MIRIAM_ICD_PREFIX + "F06.7")).thenReturn(diseaseType3);
		when(dataService.findOneById(REF_DISEASE_TYPES, URN_MIRIAM_ICD_PREFIX + "XXX")).thenReturn(null);
		when(dataService.findOneById(REF_DISEASE_TYPES, "NAV")).thenReturn(diseaseTypeNAV);

		Map<String, String> disease1 = new HashMap<>();
		disease1.put(XML_IDAA, "4");
		disease1.put(XML_IDAABB, "1");
		disease1.put(XML_ID, "9000");
		disease1.put(XML_CODENAME, "ICD-10");
		disease1.put(XML_CODEVERSION, "2015");
		disease1.put(XML_CODEINDEX, "C81-C96");
		disease1.put(XML_CODEDESCEN,
				"Malignant neoplasms, stated or presumed to be primary, of lymphoid, haematopoietic and related tissue");

		Map<String, String> disease2 = new HashMap<>();
		disease2.put(XML_ID, "9000");
		disease2.put(XML_IDAA, "4");
		disease2.put(XML_IDAABB, "1");
		disease2.put(XML_CODENAME, "ICD-10");
		disease2.put(XML_CODEVERSION, "2015");
		disease2.put(XML_CODEINDEX, "E11");
		disease2.put(XML_CODEDESCEN, "Type 2 diabetes mellitus");

		Map<String, String> disease3 = new HashMap<>();
		disease3.put(XML_ID, "9000");
		disease3.put(XML_IDAA, "8");
		disease3.put(XML_IDAABB, "1");
		disease3.put(XML_CODENAME, "ICD-10");
		disease3.put(XML_CODEVERSION, "2015");
		disease3.put(XML_CODEINDEX, "F06.7");
		disease3.put(XML_CODEDESCEN, "Mild cognitive disorder");

		Map<String, String> disease4 = new HashMap<>();
		disease4.put(XML_IDAA, "1");
		disease4.put(XML_CODENAME, "XXX");

		radboudDiseaseMap.addDisease(disease1);
		radboudDiseaseMap.addDisease(disease2);
		radboudDiseaseMap.addDisease(disease3);
		radboudDiseaseMap.addDisease(disease4);
	}

	@Test
	public void getMultipleDiseaseTypes()
	{
		assertEquals(radboudDiseaseMap.getDiseaseTypes("4"), Lists.newArrayList(diseaseType1, diseaseType2));
	}

	@Test
	public void getDiseaseTypes()
	{
		assertEquals(radboudDiseaseMap.getDiseaseTypes("8"), Lists.newArrayList(diseaseType3));
	}

	@Test
	public void getDiseaseTypesNAV()
	{
		assertEquals(radboudDiseaseMap.getDiseaseTypes("3"), Lists.newArrayList(diseaseTypeNAV));
		assertEquals(radboudDiseaseMap.getDiseaseTypes("1"), Lists.newArrayList(diseaseTypeNAV));
	}
}