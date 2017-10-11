package org.molgenis.promise.mapper;

import org.molgenis.data.jobs.Progress;
import org.molgenis.promise.model.PromiseCredentials;

import java.io.IOException;

public interface PromiseMapper
{
	int map(Progress progress, PromiseCredentials promiseCredentials, String biobankId) throws IOException;
}
