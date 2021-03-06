package org.molgenis.promise.job;

import org.molgenis.data.Entity;
import org.molgenis.data.jobs.model.JobExecution;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.promise.PromiseMapperType;

import static org.molgenis.promise.job.PromiseJobExecutionMetadata.*;

@SuppressWarnings({ "unused", "WeakerAccess" })
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

	public String getBiobankId()
	{
		return getString(BIOBANK_ID);
	}

	public void setBiobankId(String biobankId)
	{
		set(BIOBANK_ID, biobankId);
	}

	public String getCredentials()
	{
		return getString(CREDENTIALS);
	}

	public void setCredentials(String credentials)
	{
		set(CREDENTIALS, credentials);
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
