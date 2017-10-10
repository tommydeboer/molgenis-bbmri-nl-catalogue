package org.molgenis.promise.mapper;

import org.molgenis.data.jobs.Progress;
import org.molgenis.promise.model.PromiseCredentials;

public interface PromiseMapper
{
	MappingReport map(Progress progress, PromiseCredentials promiseCredentials, String biobankId);
}
