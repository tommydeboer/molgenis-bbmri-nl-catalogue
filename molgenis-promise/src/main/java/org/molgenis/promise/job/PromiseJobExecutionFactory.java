package org.molgenis.promise.job;

import org.molgenis.data.AbstractSystemEntityFactory;
import org.molgenis.data.populate.EntityPopulator;
import org.springframework.stereotype.Component;

@Component
public class PromiseJobExecutionFactory
		extends AbstractSystemEntityFactory<PromiseJobExecution, PromiseJobExecutionMetadata, String>
{
	PromiseJobExecutionFactory(PromiseJobExecutionMetadata promiseJobExecutionMetadata, EntityPopulator entityPopulator)
	{
		super(PromiseJobExecution.class, promiseJobExecutionMetadata, entityPopulator);
	}
}