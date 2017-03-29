package org.molgenis.promise.mapper;

import java.util.Map;

import org.molgenis.security.core.runas.RunAsSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;

@Component
public class PromiseMapperFactory
{
	private static final Logger LOG = LoggerFactory.getLogger(PromiseMapperFactory.class);

	private final Map<String, PromiseMapper> mappers = Maps.newHashMap();

	@RunAsSystem
	public void registerMapper(String id, PromiseMapper promiseMapper)
	{
		LOG.info("Registering ProMISe mapper [{}].", id);
		mappers.put(id, promiseMapper);
	}

	public PromiseMapper getMapper(String id)
	{
		PromiseMapper mapper = mappers.get(id);
		if (mapper == null)
		{
			throw new IllegalArgumentException("Unknown ProMISe mapper [" + id + "]");
		}

		return mapper;
	}
}
