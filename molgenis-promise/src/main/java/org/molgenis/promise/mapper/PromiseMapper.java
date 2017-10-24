package org.molgenis.promise.mapper;

import org.molgenis.data.jobs.Progress;
import org.molgenis.promise.model.PromiseCredentials;

import java.io.IOException;

/**
 * A mapper that translates ProMISe biobank entities to BBMRI sample collections
 */
public interface PromiseMapper
{
	/**
	 * Initiate the mapping.
	 *
	 * @param progress           Progress object to report progress to
	 * @param promiseCredentials credentials to use during ProMISe data request
	 * @param biobankId          identifier of the biobank to map to
	 * @return number of mapped biobanks
	 */
	int map(Progress progress, PromiseCredentials promiseCredentials, String biobankId) throws IOException;
}
