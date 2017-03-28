package org.molgenis.promise.model;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.StaticEntity;

import static org.molgenis.promise.model.PromiseMappingProjectMetadata.*;

public class PromiseMappingProject extends StaticEntity
{
	public PromiseMappingProject(Entity entity)
	{
		super(entity);
	}

	public PromiseMappingProject(EntityType entityType)
	{
		super(entityType);
	}

	public PromiseMappingProject(String name, EntityType entityType)
	{
		super(entityType);
		setName(name);
	}

	public String getName()
	{
		return getString(NAME);
	}

	public void setName(String name)
	{
		set(NAME, name);
	}

	public String getBiobankId()
	{
		return getString(BIOBANK_ID);
	}

	public void setBiobankId(String biobankId)
	{
		set(BIOBANK_ID, biobankId);
	}

	public PromiseCredentials getPromiseCredentials()
	{
		return getEntity(CREDENTIALS, PromiseCredentials.class);
	}

	public void setPromiseCredentials(String promiseCredentials)
	{
		set(CREDENTIALS, promiseCredentials);
	}

	public String getMapper()
	{
		return getString(MAPPER);
	}

	public void setMapper(String mapper)
	{
		set(MAPPER, mapper);
	}
}