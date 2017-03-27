package org.molgenis.promise.model;

import org.molgenis.data.AbstractSystemEntityFactory;
import org.molgenis.data.populate.EntityPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromiseCredentialsFactory
		extends AbstractSystemEntityFactory<PromiseCredentials, PromiseCredentialsMetadata, String>
{
	@Autowired
	PromiseCredentialsFactory(PromiseCredentialsMetadata promiseCredentialsMetadata, EntityPopulator entityPopulator)
	{
		super(PromiseCredentials.class, promiseCredentialsMetadata, entityPopulator);
	}
}
