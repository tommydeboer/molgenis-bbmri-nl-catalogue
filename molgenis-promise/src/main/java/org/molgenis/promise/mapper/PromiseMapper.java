package org.molgenis.promise.mapper;

import org.molgenis.promise.model.PromiseMappingProject;

public interface PromiseMapper
{
	String getId();

	MappingReport map(PromiseMappingProject promiseMappingProject);
}
