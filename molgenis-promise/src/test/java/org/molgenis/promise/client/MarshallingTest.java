package org.molgenis.promise.client;

import org.molgenis.promise.PromiseConfig;
import org.molgenis.promise.model.PromiseCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.xml.transform.StringResult;
import org.testng.annotations.Test;

import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

@ContextConfiguration(classes = PromiseConfig.class)
public class MarshallingTest extends AbstractTestNGSpringContextTests
{
	@Autowired
	Marshaller marshaller;

	@Autowired
	Unmarshaller unmarshaller;

	@Test
	public void testMarshal() throws XmlMappingException, IOException
	{
		PromiseCredentials promiseCredentials = mock(PromiseCredentials.class);
		when(promiseCredentials.getProject()).thenReturn("proj");
		when(promiseCredentials.getPws()).thenReturn("pws");
		when(promiseCredentials.getSecurityCode()).thenReturn("securityCode");
		when(promiseCredentials.getUsername()).thenReturn("userName");
		when(promiseCredentials.getPassword()).thenReturn("passw");

		PromiseRequest request = PromiseRequest.create(promiseCredentials, "10");

		StringResult result = new StringResult();
		marshaller.marshal(request, result);

		assertEquals(result.toString(), "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
				+ "<getData xmlns=\"http://tempuri.org/\"><proj>proj</proj><PWS>pws</PWS><SEQNR>10</SEQNR>"
				+ "<securitycode>securityCode</securitycode><username>userName</username><passw>passw</passw></getData>");
	}

	@Test
	public void testUnmarshal() throws TransformerException, XmlMappingException, IOException
	{
		String result = "<tempuri:getDataForXMLResult xmlns:tempuri=\"http://tempuri.org/\">"
				+ "&lt;blah/&gt;&lt;blah2/&gt;</tempuri:getDataForXMLResult>";
		String data = (String) unmarshaller.unmarshal(new StreamSource(new StringReader(result)));
		assertEquals(data, "<blah/><blah2/>");
	}
}
