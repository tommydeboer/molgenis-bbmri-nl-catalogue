package org.molgenis.promise.model;

import org.molgenis.data.Entity;
import org.molgenis.data.meta.model.EntityType;
import org.molgenis.data.support.StaticEntity;

import static org.molgenis.promise.model.PromiseCredentialsMetadata.*;

public class PromiseCredentials extends StaticEntity
{
	public PromiseCredentials(Entity entity)
	{
		super(entity);
	}

	public PromiseCredentials(EntityType entityType)
	{
		super(entityType);
	}

	public PromiseCredentials(String id, EntityType entityType)
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

	public String getProject()
	{
		return getString(PROJ);
	}

	public void setProject(String project)
	{
		set(PROJ, project);
	}

	public String getUsername()
	{
		return getString(USERNAME);
	}

	public void setUsername(String username)
	{
		set(USERNAME, username);
	}

	public String getPassword()
	{
		return getString(PASSW);
	}

	public void setPassword(String password)
	{
		set(PASSW, password);
	}

	public String getPws()
	{
		return getString(PWS);
	}

	public void setPws(String pws)
	{
		set(PWS, pws);
	}

	public String getSecurityCode()
	{
		return getString(SECURITYCODE);
	}

	public void setSecurityCode(String securityCode)
	{
		set(SECURITYCODE, securityCode);
	}

	public String getUrl()
	{
		return getString(URL);
	}

	public void setUrl(String url)
	{
		set(URL, url);
	}
}