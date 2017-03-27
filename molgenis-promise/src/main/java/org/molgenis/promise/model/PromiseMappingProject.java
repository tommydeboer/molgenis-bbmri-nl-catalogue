package org.molgenis.promise.model;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.StaticEntity;

import static org.molgenis.promise.model.PromiseCredentialsMetadata.ID;

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

	public PromiseMappingProject(String id, EntityType entityType)
	{
		super(entityType);
		setId(id);
	}

	public String getId()
	{
		return getString(ID);
	}

	public void setId(String id)
	{
		set(ID, id);
	}
}