package org.molgenis.promise.job;

import org.molgenis.data.Entity;
import org.molgenis.data.jobs.model.JobExecution;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.promise.model.PromiseCredentials;

import static org.molgenis.promise.job.PromiseJobExecutionMetadata.*;

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

	public PromiseCredentials getCredentials()
	{
		return getEntity(CREDENTIALS, PromiseCredentials.class);
	}

	public void setCredentials(PromiseCredentials credentials)
	{
		set(CREDENTIALS, credentials);
	}

	public String getMapperType()
	{
		return getString(MAPPER);
	}

	public void setMapperType(String mapperType)
	{
		set(MAPPER, mapperType);
	}
}
