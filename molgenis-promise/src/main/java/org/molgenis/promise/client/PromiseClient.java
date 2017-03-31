package org.molgenis.promise.client;

import org.molgenis.data.Entity;
import org.molgenis.promise.model.PromiseCredentials;

import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.function.Consumer;

public interface PromiseClient
{
	void getData(PromiseCredentials promiseCredentials, String seqNr, Consumer<XMLStreamReader> consumer) throws IOException;
}
