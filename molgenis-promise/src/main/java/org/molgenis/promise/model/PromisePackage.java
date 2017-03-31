package org.molgenis.promise.model;

import org.molgenis.data.meta.SystemPackage;
import org.molgenis.data.meta.model.PackageMetadata;
import org.molgenis.data.system.model.RootSystemPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.system.model.RootSystemPackage.PACKAGE_SYSTEM;

@Component
public class PromisePackage extends SystemPackage
{
	private static final String SIMPLE_NAME = "promise";
	public static final String PACKAGE_PROMISE = PACKAGE_SYSTEM + PACKAGE_SEPARATOR + SIMPLE_NAME;

	private final RootSystemPackage rootSystemPackage;

	@Autowired
	public PromisePackage(PackageMetadata packageMetadata, RootSystemPackage rootSystemPackage)
	{
		super(SIMPLE_NAME, packageMetadata);
		this.rootSystemPackage = requireNonNull(rootSystemPackage);
	}

	@Override
	protected void init()
	{
		setLabel("ProMISe");
		setDescription("ProMISe");
		setParent(rootSystemPackage);
	}
}
