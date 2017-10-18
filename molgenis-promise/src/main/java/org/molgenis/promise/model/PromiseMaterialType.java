package org.molgenis.promise.model;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.StaticEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.molgenis.promise.model.PromiseCredentialsMetadata.ID;
import static org.molgenis.promise.model.PromiseMaterialTypeMetadata.*;

public class PromiseMaterialType extends StaticEntity
{
	public PromiseMaterialType(Entity entity)
	{
		super(entity);
	}

	public PromiseMaterialType(EntityType entityType)
	{
		super(entityType);
	}

	public PromiseMaterialType(String id, EntityType entityType)
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

	public String getPromiseMaterialType()
	{
		return getString(PROMISE_MATERIAL_TYPE);
	}

	public void setPromiseMaterialType(String promiseMaterialType)
	{
		set(PROMISE_TYPE, promiseMaterialType);
	}

	public List<String> getMiabisMaterialTypes()
	{
		return Arrays.asList(getString(MIABIS_TYPES).split(","));
	}

	public void setMiabisMaterialTypes(List<String> miabisMaterialTypes)
	{
		set(MIABIS_TYPES, miabisMaterialTypes.stream().collect(Collectors.joining(",")));
	}
}