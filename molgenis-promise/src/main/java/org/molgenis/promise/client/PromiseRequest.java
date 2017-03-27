package org.molgenis.promise.client;

import com.google.auto.value.AutoValue;
import org.molgenis.data.Entity;
import org.molgenis.promise.model.PromiseCredentials;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import static org.molgenis.promise.client.PromiseClientImpl.ACTION_GETDATA;
import static org.molgenis.promise.client.PromiseClientImpl.NAMESPACE_VALUE;

@AutoValue
@XmlType(propOrder =
{ "proj", "PWS", "SEQNR", "securitycode", "username", "passw" })
@XmlRootElement(name = ACTION_GETDATA, namespace = NAMESPACE_VALUE)
public abstract class PromiseRequest
{
	@XmlElement(namespace = NAMESPACE_VALUE)
	public abstract String getProj();

	@XmlElement(namespace = NAMESPACE_VALUE)
	public abstract String getPWS();

	@XmlElement(namespace = NAMESPACE_VALUE)
	public abstract String getSEQNR();

	@XmlElement(namespace = NAMESPACE_VALUE)
	public abstract String getSecuritycode();

	@XmlElement(namespace = NAMESPACE_VALUE)
	public abstract String getUsername();

	@XmlElement(namespace = NAMESPACE_VALUE)
	public abstract String getPassw();

	public static PromiseRequest create(PromiseCredentials promiseCredentials, String seqNr)
	{
		return new AutoValue_PromiseRequest(promiseCredentials.getProject(), promiseCredentials.getPws(), seqNr,
				promiseCredentials.getSecurityCode(), promiseCredentials.getUsername(),
				promiseCredentials.getPassword());
	}

}
