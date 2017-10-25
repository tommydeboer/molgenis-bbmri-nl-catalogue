package org.molgenis.promise.job;

import org.molgenis.data.Entity;
import org.molgenis.data.jobs.model.JobExecution;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.promise.PromiseMapperType;

import static org.molgenis.promise.job.PromiseJobExecutionMetadata.*;

@SuppressWarnings("unused")
public class PromiseJobExecution extends JobExecution
{
	public PromiseJobExecution(Entity entity)
	{
		super(entity);
		setType(JOB_TYPE);
	}

	public PromiseJobExecution(EntityType entityType)
	{
		super(entityType);
		setType(JOB_TYPE);
	}

	public PromiseJobExecution(String identifier, EntityType entityType)
	{
		super(identifier, entityType);
		setType(JOB_TYPE);
	}

	String getBiobankId()
	{
		return getString(BIOBANK_ID);
	}

	String getCredentials()
	{
		return getString(CREDENTIALS);
	}

	public PromiseMapperType getMapper()
	{
		return PromiseMapperType.valueOf(getString(MAPPER));
	}

	public void setMapper(PromiseMapperType mapper)
	{
		set(MAPPER, mapper.name());
	}
}
