package org.molgenis.promise.mapper;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.promise.client.PromiseDataParser;
import org.molgenis.promise.model.PromiseMaterialType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.stream.Stream;

import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.molgenis.promise.model.BbmriNlCheatSheet.REF_MATERIAL_TYPES;
import static org.molgenis.promise.model.PromiseMaterialTypeMetadata.PROMISE_MATERIAL_TYPE;
import static org.testng.Assert.assertEquals;

public class ParelMapperTest
{
	private ParelMapper parelMapper;
	private DataService dataService;

	private PromiseMaterialType promiseDna;
	private PromiseMaterialType promisePlasma;
	private PromiseMaterialType promiseWeefsel;

	private Entity miabisDna;
	private Entity miabisPlasma;
	private Entity miabisTissueFrozen;
	private Entity miabisTissueEmbedded;

	@BeforeClass
	public void setupBeforeClass()
	{
		dataService = mock(DataService.class);
		parelMapper = new ParelMapper(mock(PromiseMapperFactory.class), mock(PromiseDataParser.class), dataService);
	}

	@BeforeMethod
	public void setupBeforeMethod()
	{
		promiseDna = mock(PromiseMaterialType.class);
		when(promiseDna.getId()).thenReturn("DNA_UIT_BLOEDCELLEN");
		when(promiseDna.getMiabisMaterialTypes()).thenReturn(Stream.of("DNA"));
		promisePlasma = mock(PromiseMaterialType.class);
		when(promisePlasma.getId()).thenReturn("BLOEDPLASMA(EDTA)");
		when(promisePlasma.getMiabisMaterialTypes()).thenReturn(Stream.of("PLASMA"));
		promiseWeefsel = mock(PromiseMaterialType.class);
		when(promiseWeefsel.getId()).thenReturn("WEEFSEL");
		when(promiseWeefsel.getMiabisMaterialTypes()).thenReturn(
				Stream.of("TISSUE_FROZEN", "TISSUE_PARAFFIN_EMBEDDED"));

		miabisDna = mock(Entity.class);
		when(miabisDna.getIdValue()).thenReturn("DNA");
		miabisPlasma = mock(Entity.class);
		when(miabisPlasma.getIdValue()).thenReturn("PLASMA");
		miabisTissueFrozen = mock(Entity.class);
		when(miabisTissueFrozen.getIdValue()).thenReturn("TISSUE_FROZEN");
		miabisTissueEmbedded = mock(Entity.class);
		when(miabisTissueEmbedded.getIdValue()).thenReturn("TISSUE_PARAFFIN_EMBEDDED");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testToMaterialTypes()
	{
		String promiseMaterialTypes = "DNA_UIT_BLOEDCELLEN,BLOEDPLASMA(EDTA),WEEFSEL";

		when(dataService.findAll(eq(PROMISE_MATERIAL_TYPE), any(Stream.class),
				eq(PromiseMaterialType.class))).thenReturn(Stream.of(promiseDna, promisePlasma, promiseWeefsel));

		when(dataService.findAll(eq(REF_MATERIAL_TYPES), any(Stream.class))).thenReturn(
				Stream.of(miabisDna, miabisPlasma, miabisTissueEmbedded, miabisTissueFrozen));

		Iterable<Entity> materialTypes = parelMapper.toMaterialTypes(promiseMaterialTypes);
		assertEquals(newHashSet(materialTypes),
				newHashSet(miabisDna, miabisPlasma, miabisTissueEmbedded, miabisTissueFrozen));
	}

	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = MolgenisDataException.class, expectedExceptionsMessageRegExp = "ProMISe material type\\(s\\) \\[WEEFSEL\\] not found in \\[sys_promise_PromiseMaterialType\\]")
	public void testToMaterialTypesPromiseTypeNotFound()
	{
		String promiseMaterialTypes = "DNA_UIT_BLOEDCELLEN,BLOEDPLASMA(EDTA),WEEFSEL";

		when(dataService.findAll(eq(PROMISE_MATERIAL_TYPE), any(Stream.class),
				eq(PromiseMaterialType.class))).thenReturn(Stream.of(promiseDna, promisePlasma));

		parelMapper.toMaterialTypes(promiseMaterialTypes);
	}

	@SuppressWarnings("unchecked")
	@Test(expectedExceptions = MolgenisDataException.class, expectedExceptionsMessageRegExp = "MIABIS material type\\(s\\) \\[TISSUE_FROZEN\\] not found in \\[bbmri_nl_material_types\\]")
	public void testToMaterialTypesMiabisTypeNotFound()
	{
		String promiseMaterialTypes = "DNA_UIT_BLOEDCELLEN,BLOEDPLASMA(EDTA),WEEFSEL";

		when(dataService.findAll(eq(PROMISE_MATERIAL_TYPE), any(Stream.class),
				eq(PromiseMaterialType.class))).thenReturn(Stream.of(promiseDna, promisePlasma, promiseWeefsel));

		when(dataService.findAll(eq(REF_MATERIAL_TYPES), any(Stream.class))).thenReturn(
				Stream.of(miabisDna, miabisPlasma, miabisTissueEmbedded));

		parelMapper.toMaterialTypes(promiseMaterialTypes);
	}
}