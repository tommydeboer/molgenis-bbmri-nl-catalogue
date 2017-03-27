package org.molgenis.promise.model;

import org.molgenis.data.meta.SystemEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.meta.model.EntityType.AttributeRole.ROLE_ID;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;
import static org.molgenis.promise.model.PromisePackage.PACKAGE_PROMISE;

@Component
public class PromiseCredentialsMetadata extends SystemEntityType
{
	public static final String SIMPLE_NAME = "PromiseCredentials";
	public static final String PROMISE_CREDENTIALS = PACKAGE_PROMISE + PACKAGE_SEPARATOR + SIMPLE_NAME;

	public static final String ID = "ID";
	public static final String PROJ = "PROJ";
	public static final String USERNAME = "USERNAME";
	public static final String PASSW = "PASSW";
	public static final String PWS = "PWS";
	public static final String SECURITYCODE = "SECURITYCODE";
	public static final String URL = "URL";

	private final PromisePackage promisePackage;

	@Autowired
	public PromiseCredentialsMetadata(PromisePackage promisePackage)
	{
		super(SIMPLE_NAME, PACKAGE_PROMISE);
		this.promisePackage = requireNonNull(promisePackage);
	}

	@Override
	protected void init()
	{
		setLabel("ProMISe credentials");
		setDescription("Credentials for ProMISe SOAP endpoints");
		setPackage(promisePackage);

		addAttribute(ID, ROLE_ID);
		addAttribute(PROJ).setNillable(false);
		addAttribute(USERNAME).setNillable(false);
		addAttribute(PASSW).setNillable(false).setVisible(false);
		addAttribute(PWS).setNillable(false);
		addAttribute(SECURITYCODE).setNillable(false).setVisible(false);
		addAttribute(URL).setNillable(false);
	}
}
