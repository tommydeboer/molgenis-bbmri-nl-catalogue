# molgenis-bbmri-nl-catalogue

### Mocking the ProMISe link
To test the ProMISe links locally, you can mock a SOAP service with SoapUI and serve the appropriate responses.

1. Install [SoapUI](https://www.soapui.org/downloads/soapui.html) if you haven't already
2. Download the **promise-soapui-project.xml** from Google Drive (@ *Projects / BBMRI - Catalogus 2.0 / Promise*)
3. SSH to the test server and:
  - Find the **soap.xml** in the home folder, or make one:
 
```xml
  <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:tem="http://tempuri.org/">
   <soap:Header/>
   <soap:Body>
      <tem:getData>
         <!--Optional:-->
         <tem:proj>?</tem:proj>
         <!--Optional:-->
         <tem:PWS>?</tem:PWS>
         <!--Optional:-->
         <tem:SEQNR>?</tem:SEQNR>
         <!--Optional:-->
         <tem:securitycode>?</tem:securitycode>
         <!--Optional:-->
         <tem:username>?</tem:username>
         <!--Optional:-->
         <tem:passw>?</tem:passw>
      </tem:getData>
   </soap:Body>
</soap:Envelope>
```

  - Supply the appropriate credentials and the SEQNR you want to request in the xml file
  - Do a request and save the response to a file:
  
```bash
curl --header "Content-Type: text/xml;charset=UTF-8" --data @soap.xml SOAP_SERVICE_URL > response.xml
```

  - Repeat this process for each needed response
- Finally, ```scp``` the files to your machine
  
4. Open the SoapUI project and add the content of the files to the MockResponse in the MockService. Copy the responses where needed.
5. Start the MockService. You can now request data from ```http://localhost:8081```!
