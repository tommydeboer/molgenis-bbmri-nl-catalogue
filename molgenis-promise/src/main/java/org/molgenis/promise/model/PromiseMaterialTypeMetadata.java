package org.molgenis.promise.model;

import org.molgenis.data.meta.SystemEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.meta.model.EntityType.AttributeRole.ROLE_ID;
import static org.molgenis.data.meta.model.Package.PACKAGE_SEPARATOR;
import static org.molgenis.promise.model.PromisePackage.PACKAGE_PROMISE;

@Component
public class PromiseMaterialTypeMetadata extends SystemEntityType
{
	public static final String SIMPLE_NAME = "PromiseMaterialType";
	public static final String PROMISE_MATERIAL_TYPE = PACKAGE_PROMISE + PACKAGE_SEPARATOR + SIMPLE_NAME;

	public static final String ID = "id";
	public static final String MIABIS_TYPES = "miabisMaterialTypes";

	private final PromisePackage promisePackage;

	@Autowired
	public PromiseMaterialTypeMetadata(PromisePackage promisePackage)
	{
		super(SIMPLE_NAME, PACKAGE_PROMISE);
		this.promisePackage = requireNonNull(promisePackage);
	}

	@Override
	protected void init()
	{
		setLabel("ProMISe material types");
		setDescription("Mapping of ProMISe material types to MIABIS material types");
		setPackage(promisePackage);

		addAttribute(ID, ROLE_ID).setLabel("ProMISe material type")
								 .setDescription("Identifier of the ProMISe material type");
		addAttribute(MIABIS_TYPES).setNillable(false)
								  .setLabel("MIABIS material type")
								  .setDescription("Comma-separated list of MIABIS material type identifiers");
	}
}
