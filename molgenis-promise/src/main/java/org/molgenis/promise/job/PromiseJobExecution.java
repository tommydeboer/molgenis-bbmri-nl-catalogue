package org.molgenis.promise.job;

import org.molgenis.data.Entity;
import org.molgenis.data.jobs.model.JobExecution;
import org.molgenis.promise.PromiseMapperType;

import static org.molgenis.promise.job.PromiseJobExecutionMetadata.*;

public class PromiseJobExecution extends JobExecution
{
	public PromiseJobExecution(Entity entity)
	{
		super(entity);
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
