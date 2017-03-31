package org.molgenis.promise.model;

import org.molgenis.data.AbstractSystemEntityFactory;
import org.molgenis.data.populate.EntityPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PromiseMappingProjectFactory
		extends AbstractSystemEntityFactory<PromiseMappingProject, PromiseMappingProjectMetadata, String>
{
	@Autowired
	PromiseMappingProjectFactory(PromiseMappingProjectMetadata promiseMappingProjectMetadata,
			EntityPopulator entityPopulator)
	{
		super(PromiseMappingProject.class, promiseMappingProjectMetadata, entityPopulator);
	}
}
