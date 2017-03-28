package org.molgenis.promise.client;

import org.mockito.*;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.promise.model.PromiseCredentials;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.collect.Lists.newArrayList;
import static org.testng.Assert.assertEquals;

public class PromiseDataParserTest
{
	@Mock
	private PromiseClient promiseClient;

	@Mock
	private PromiseCredentials promiseCredentials;

	private PromiseDataParser parser;

	@Captor
	private ArgumentCaptor<Consumer<XMLStreamReader>> consumerArgumentCaptor;

	@BeforeMethod
	public void beforeMethod()
	{
		MockitoAnnotations.initMocks(this);
		parser = new PromiseDataParser(promiseClient);
	}

	@Test
	public void testParse() throws IOException, XMLStreamException
	{
		List<Map<String, String>> entities = newArrayList();
		parser.parse(promiseCredentials, 0, entities::add);

		Mockito.verify(promiseClient)
				.getData(Mockito.eq(promiseCredentials), Mockito.eq("0"), consumerArgumentCaptor.capture());

		InputStream is = getClass().getResourceAsStream("/cva_biobank_response.xml");
		XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(is);
		consumerArgumentCaptor.getValue().accept(xmlStreamReader);

		Map<String, String> entity1 = new HashMap<>();
		entity1.put("PSI_REG_ID", "1006");
		entity1.put("MATERIAL_TYPES", "DNA_UIT_BLOEDCELLEN,BLOEDPLASMA(EDTA),BLOEDSERUM");

		Map<String, String> entity2 = new HashMap<>();
		entity2.put("PSI_REG_ID", "1042");
		entity2.put("MATERIAL_TYPES", "OORSMEER");

		assertEquals(entities, newArrayList(entity1, entity2));
	}

	@Test(expectedExceptions = MolgenisDataException.class, expectedExceptionsMessageRegExp = "Username-Password combination not found<BR>Combinatie Gebruikersnaam-Wachtwoord niet gevonden")
	public void testErroneousParse() throws IOException, XMLStreamException
	{
		List<Map<String, String>> entities = newArrayList();
		parser.parse(promiseCredentials, 0, entities::add);

		Mockito.verify(promiseClient)
				.getData(Mockito.eq(promiseCredentials), Mockito.eq("0"), consumerArgumentCaptor.capture());

		InputStream is = getClass().getResourceAsStream("/erroneous_biobank_response.xml");
		XMLStreamReader xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(is);
		consumerArgumentCaptor.getValue().accept(xmlStreamReader);
	}
}
