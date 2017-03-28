package org.molgenis.promise.mapper;

import org.mockito.ArgumentCaptor;
import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.molgenis.promise.mapper.RadboudMapper.XML_ID;
import static org.molgenis.promise.mapper.RadboudMapper.XML_IDAA;
import static org.molgenis.promise.mapper.RadboudSampleMap.*;
import static org.molgenis.promise.model.BbmriNlCheatSheet.*;
import static org.testng.Assert.assertEquals;

public class RadboudSampleMapTest
{
	private RadboudSampleMap radboudSampleMap;
	private DataService dataService;
	private ArgumentCaptor<Stream> streamCaptor = ArgumentCaptor.forClass(Stream.class);

	@BeforeMethod
	@SuppressWarnings("unchecked")
	public void beforeMethod()
	{
		dataService = mock(DataService.class);
		radboudSampleMap = new RadboudSampleMap(dataService);

		Stream<Entity> resultStream = mock(Stream.class);
		List<Entity> resultEntities = mock(List.class);
		when(resultStream.collect(toList())).thenReturn(resultEntities);
		when(dataService.findAll(any(String.class), any(Stream.class))).thenReturn(resultStream);

		Map<String, String> sample1 = new HashMap<>();
		sample1.put(XML_ID, "9000");
		sample1.put(XML_IDAA, "100");
		sample1.put(XML_DEELBIOBANKS, "100");
		sample1.put(XML_GENDER, "2");
		sample1.put(XML_BIRTHDATE, "1960-01-01T00:00:00+02:00");
		sample1.put(XML_INCLUSIE, "2000-01-01T00:00:00+02:00");
		sample1.put(XML_BLOED, "2");
		sample1.put(XML_BLOEDPLASMA, "2");
		sample1.put(XML_BLOEDSERUM, "2");
		sample1.put(XML_DNA, "1");
		sample1.put(XML_RNA, "2");
		sample1.put(XML_GWASOMNI, "1");
		sample1.put(XML_GASTROINTMUC, "2");
		sample1.put(XML_URINE, "2");
		sample1.put(XML_LIQUOR, "2");
		sample1.put(XML_FECES, "2");
		sample1.put(XML_DNABEENMERG, "2");
		sample1.put(XML_RNABEENMERG, "2");
		sample1.put(XML_SPEEKSEL, "2");
		sample1.put(XML_MONONUCLBLOED, "2");
		sample1.put(XML_MONONUCMERG, "2");
		sample1.put(XML_GRANULOCYTMERG, "2");
		sample1.put(XML_MONOCYTMERG, "2");
		sample1.put(XML_MICROBIOOM, "2");

		Map<String, String> sample2 = new HashMap<>();
		sample2.put(XML_ID, "9000");
		sample2.put(XML_IDAA, "100");
		sample2.put(XML_DEELBIOBANKS, "100");
		sample2.put(XML_GENDER, "1");
		sample2.put(XML_BIRTHDATE, "2010-01-01T00:00:00+02:00");
		sample2.put(XML_INCLUSIE, "2011-01-01T00:00:00+02:00");
		sample2.put(XML_BLOED, "2");
		sample2.put(XML_BLOEDPLASMA, "2");
		sample2.put(XML_BLOEDSERUM, "2");
		sample2.put(XML_DNA, "1");
		sample2.put(XML_RNA, "2");
		sample2.put(XML_WEEFSELSOORT, "2");
		sample2.put(XML_GASTROINTMUC, "2");
		sample2.put(XML_URINE, "2");
		sample2.put(XML_LIQUOR, "2");
		sample2.put(XML_FECES, "2");
		sample2.put(XML_CELLBEENMERG, "2");
		sample2.put(XML_DNABEENMERG, "2");
		sample2.put(XML_RNABEENMERG, "1");
		sample2.put(XML_SPEEKSEL, "2");
		sample2.put(XML_MONONUCLBLOED, "2");
		sample2.put(XML_MONONUCMERG, "2");
		sample2.put(XML_GRANULOCYTMERG, "2");
		sample2.put(XML_MONOCYTMERG, "1");
		sample2.put(XML_MICROBIOOM, "2");

		Map<String, String> sample3 = new HashMap<>();
		sample3.put(XML_ID, "9000");
		sample3.put(XML_IDAA, "8");
		sample3.put(XML_DEELBIOBANKS, "0");
		sample3.put(XML_GENDER, "3");
		sample3.put(XML_BIRTHDATE, "2050-01-01T00:00:00+02:00");
		sample3.put(XML_INCLUSIE, "2268-01-01T00:00:00+02:00"); // will fail age sanity check
		sample3.put(XML_BLOED, "2");
		sample3.put(XML_BLOEDPLASMA, "2");
		sample3.put(XML_BLOEDSERUM, "2");
		sample3.put(XML_DNA, "2");
		sample3.put(XML_RNA, "2");
		sample3.put(XML_GASTROINTMUC, "2");
		sample3.put(XML_URINE, "1");
		sample3.put(XML_LIQUOR, "2");
		sample3.put(XML_FECES, "1");
		sample3.put(XML_CELLBEENMERG, "2");
		sample3.put(XML_DNABEENMERG, "2");
		sample3.put(XML_RNABEENMERG, "2");
		sample3.put(XML_SPEEKSEL, "1");
		sample3.put(XML_EXOOMCHIP, "1");
		sample3.put(XML_MONONUCLBLOED, "2");
		sample3.put(XML_MONONUCMERG, "2");
		sample3.put(XML_GRANULOCYTMERG, "2");
		sample3.put(XML_MONOCYTMERG, "2");
		sample3.put(XML_MICROBIOOM, "2");

		Map<String, String> sample4 = new HashMap<>();
		sample4.put(XML_ID, "1");
		sample4.put(XML_IDAA, "1");

		radboudSampleMap.addSample(sample1);
		radboudSampleMap.addSample(sample2);
		radboudSampleMap.addSample(sample3);
		radboudSampleMap.addSample(sample4);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetDataCategories() throws Exception
	{
		Map<String, String> biobank1 = new HashMap<>();
		//when(biobank.getString(any(String.class))).thenReturn("2");

		biobank1.put(XML_ID,"9000");
		biobank1.put(XML_IDAA,"100");
		biobank1.put(XML_BEELDEN,"1");
		radboudSampleMap.getDataCategories(biobank1);
		verify(dataService, atLeastOnce()).findAll(eq(REF_DATA_CATEGORY_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("BIOLOGICAL_SAMPLES", "IMAGING_DATA"));

		Map<String, String> biobank2 = new HashMap<>();
		biobank2.put(XML_ID, "9000");
		biobank2.put(XML_IDAA, "8");
		biobank2.put(XML_BEELDEN, "2");
		biobank2.put(XML_BEHANDEL, "1");
		radboudSampleMap.getDataCategories(biobank2);
		verify(dataService, atLeastOnce()).findAll(eq(REF_DATA_CATEGORY_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("MEDICAL_RECORDS"));

		Map<String, String> biobank3 = new HashMap<>();
		biobank2.put(XML_ID, "1");
		biobank2.put(XML_IDAA, "1");
		biobank2.put(XML_BEHANDEL, "2");
		radboudSampleMap.getDataCategories(biobank3);
		verify(dataService, atLeastOnce()).findAll(eq(REF_DATA_CATEGORY_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("NAV"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetMaterials() throws Exception
	{
		radboudSampleMap.getMaterials("9000_100");
		verify(dataService, atLeastOnce()).findAll(eq(REF_MATERIAL_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()),
				newHashSet("DNA", "TISSUE_FROZEN", "OTHER", "MICRO_RNA"));

		radboudSampleMap.getMaterials("9000_8");
		verify(dataService, atLeastOnce()).findAll(eq(REF_MATERIAL_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("SALIVA", "URINE", "FECES"));

		radboudSampleMap.getMaterials("1_1");
		verify(dataService, atLeastOnce()).findAll(eq(REF_MATERIAL_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("NAV"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetOmics() throws Exception
	{
		radboudSampleMap.getOmics("9000_100");
		verify(dataService, atLeastOnce()).findAll(eq(REF_EXP_DATA_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("GENOMICS"));

		radboudSampleMap.getOmics("9000_8");
		verify(dataService, atLeastOnce()).findAll(eq(REF_EXP_DATA_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("GENOMICS"));

		radboudSampleMap.getOmics("1_1");
		verify(dataService, atLeastOnce()).findAll(eq(REF_EXP_DATA_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("NAV"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetSex() throws Exception
	{
		radboudSampleMap.getSex("9000_100");
		verify(dataService, atLeastOnce()).findAll(eq(REF_SEX_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("MALE", "FEMALE"));

		radboudSampleMap.getSex("9000_8");
		verify(dataService, atLeastOnce()).findAll(eq(REF_SEX_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("UNKNOWN"));

		radboudSampleMap.getSex("1_1");
		verify(dataService, atLeastOnce()).findAll(eq(REF_SEX_TYPES), streamCaptor.capture());
		assertEquals(streamCaptor.getValue().collect(toSet()), newHashSet("NAV"));
	}

	@Test
	public void testGetAgeMin() throws Exception
	{
		assertEquals(radboudSampleMap.getAgeMin("9000_100"), Integer.valueOf(1));
		assertEquals(radboudSampleMap.getAgeMin("9000_8"), null);
		assertEquals(radboudSampleMap.getAgeMin("1_1"), null);
	}

	@Test
	public void testGetAgeMax() throws Exception
	{
		assertEquals(radboudSampleMap.getAgeMax("9000_100"), Integer.valueOf(40));
		assertEquals(radboudSampleMap.getAgeMax("9000_8"), null);
		assertEquals(radboudSampleMap.getAgeMax("1_1"), null);
	}

	@Test
	public void testGetSize() throws Exception
	{
		assertEquals(radboudSampleMap.getSize("9000_100"), 2);
		assertEquals(radboudSampleMap.getSize("9000_8"), 1);
		assertEquals(radboudSampleMap.getSize("1_1"), 1);
	}
}