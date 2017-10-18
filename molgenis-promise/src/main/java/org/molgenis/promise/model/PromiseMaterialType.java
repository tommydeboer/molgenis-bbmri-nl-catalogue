package org.molgenis.promise.model;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.StaticEntity;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

	public Stream<String> getMiabisMaterialTypes()
	{
		return Arrays.stream(getString(MIABIS_TYPES).split(","));
	}

	public void setMiabisMaterialTypes(Stream<String> miabisMaterialTypes)
	{
		set(MIABIS_TYPES, miabisMaterialTypes.collect(Collectors.joining(",")));
	}
}