package org.molgenis.promise.model;

import org.molgenis.data.AbstractSystemEntityFactory;
import org.molgenis.data.populate.EntityPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromiseMaterialTypeFactory
		extends AbstractSystemEntityFactory<PromiseMaterialType, PromiseMaterialTypeMetadata, String>
{
	@Autowired
	PromiseMaterialTypeFactory(PromiseMaterialTypeMetadata promiseMaterialTypeMetadata,
			EntityPopulator entityPopulator)
	{
		super(PromiseMaterialType.class, promiseMaterialTypeMetadata, entityPopulator);
	}
}
