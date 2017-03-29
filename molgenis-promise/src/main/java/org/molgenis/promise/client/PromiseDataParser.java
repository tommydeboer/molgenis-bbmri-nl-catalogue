package org.molgenis.promise.client;

import com.google.common.collect.Maps;
import org.molgenis.data.MolgenisDataException;
import org.molgenis.promise.model.PromiseCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;
import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

@Component
public class PromiseDataParser
{
	private final PromiseClient promiseClient;

	private static final Logger LOG = LoggerFactory.getLogger(PromiseDataParser.class);

	@Autowired
	public PromiseDataParser(PromiseClient promiseClient)
	{
		this.promiseClient = requireNonNull(promiseClient);
	}

	public void parse(PromiseCredentials credentials, Integer seqNr, Consumer<Map<String, String>> entityConsumer)
			throws IOException
	{
		promiseClient.getData(credentials, seqNr.toString(), reader ->
		{
			try
			{
				boolean inDocumentElement = false;
				while (reader.hasNext())
				{
					switch (reader.next())
					{
						case START_ELEMENT:
							if ("DocumentElement".equals(reader.getLocalName()))
							{
								inDocumentElement = true;
							}
							else
							{
								if (inDocumentElement)
								{
									Map<String, String> contentMap = parseContent(reader);
									if (contentMap.get("RM") != null)
									{
										throw new MolgenisDataException(contentMap.get("RM"));
									}
									entityConsumer.accept(contentMap);
								}
							}
							break;
						case END_ELEMENT:
							if ("DocumentElement".equals(reader.getLocalName()))
							{
								inDocumentElement = false;
							}
							break;
					}
				}
			}
			catch (XMLStreamException e)
			{
				LOG.error("Something went wrong: ", e);
			}
		});
	}

	/**
	 * Parses one entity from the reader
	 */
	private static Map<String, String> parseContent(XMLStreamReader reader) throws XMLStreamException
	{
		Map<String, String> contentMap = Maps.newHashMap();
		while (reader.hasNext())
		{
			switch (reader.next())
			{
				case START_ELEMENT:
					contentMap.put(reader.getLocalName(), reader.getElementText());
					break;
				case END_ELEMENT:
					return contentMap;
				default:
					break;
			}
		}
		return contentMap;
	}
}
