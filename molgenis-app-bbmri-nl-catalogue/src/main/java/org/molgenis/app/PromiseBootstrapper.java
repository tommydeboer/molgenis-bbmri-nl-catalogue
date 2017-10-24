package org.molgenis.app;

import org.molgenis.promise.mapper.PromiseMapper;
import org.molgenis.promise.mapper.PromiseMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.Objects.requireNonNull;

@Component
public class PromiseBootstrapper implements ApplicationListener<ContextRefreshedEvent>, PriorityOrdered
{
	private static final Logger LOG = LoggerFactory.getLogger(PromiseBootstrapper.class);

	private final PromiseMapperFactory promiseMapperFactory;
	private final List<PromiseMapper> promiseMapper;

	public PromiseBootstrapper(PromiseMapperFactory promiseMapperFactory, List<PromiseMapper> promiseMapper)
	{
		this.promiseMapperFactory = requireNonNull(promiseMapperFactory);
		this.promiseMapper = requireNonNull(promiseMapper);
	}

	private void bootstrap()
	{
		LOG.info("Bootstrapping ProMISe mappers...");
		promiseMapper.forEach(promiseMapperFactory::registerMapper);
		LOG.info("Bootstrapping ProMISe mappers completed");
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event)
	{
		bootstrap();
	}

	@Override
	public int getOrder()
	{
		return PriorityOrdered.HIGHEST_PRECEDENCE + 1; // bootstrap application after MOLGENIS bootstrapper
	}
}
