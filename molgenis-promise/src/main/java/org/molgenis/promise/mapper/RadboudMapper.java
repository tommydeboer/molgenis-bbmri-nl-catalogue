package org.molgenis.promise.mapper;

import org.molgenis.data.DataService;
import org.molgenis.data.Entity;
import org.molgenis.data.EntityManager;
import org.molgenis.data.jobs.Progress;
import org.molgenis.promise.PromiseMapperType;
import org.molgenis.promise.client.PromiseDataParser;
import org.molgenis.promise.model.PromiseCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.molgenis.promise.model.BbmriNlCheatSheet.SAMPLE_COLLECTIONS_ENTITY;

@Component
class RadboudMapper implements PromiseMapper, ApplicationListener<ContextRefreshedEvent>
{
	private static final Logger LOG = LoggerFactory.getLogger(RadboudMapper.class);

	static final String XML_ID = "ID";
	static final String XML_IDAA = "IDAA";

	private final PromiseDataParser promiseDataParser;
	private final DataService dataService;
	private final PromiseMapperFactory promiseMapperFactory;
	private final EntityManager entityManager;

	private int newBiobanks;
	private int existingBiobanks;

	@Autowired
	public RadboudMapper(PromiseDataParser promiseDataParser, DataService dataService,
			PromiseMapperFactory promiseMapperFactory, EntityManager entityManager)
	{
		this.promiseDataParser = requireNonNull(promiseDataParser);
		this.dataService = requireNonNull(dataService);
		this.promiseMapperFactory = requireNonNull(promiseMapperFactory);
		this.entityManager = requireNonNull(entityManager);
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent)
	{
		promiseMapperFactory.registerMapper(PromiseMapperType.RADBOUD, this);
	}

	@Override
	@Transactional
	public int map(Progress progress, PromiseCredentials promiseCredentials, String unusedBiobankId) throws IOException
	{
		RadboudSampleMap samples = new RadboudSampleMap(dataService);
		RadboudDiseaseMap diseases = new RadboudDiseaseMap(dataService);
		RadboudBiobankMapper biobankMapper = new RadboudBiobankMapper(dataService, entityManager);

		progress.setProgressMax(3);
		progress.progress(0, "Reading RADBOUD samples");
		promiseDataParser.parse(promiseCredentials, 1, sampleEntity ->
		{
			if (shouldMap(sampleEntity)) samples.addSample(sampleEntity);
		});
		progress.status(format("Processed %d RADBOUD samples", samples.getNumberOfSamples()));
		progress.increment(1);

		progress.status("Reading RADBOUD disease types");
		promiseDataParser.parse(promiseCredentials, 2, diseases::addDisease);
		progress.status(format("Processed %d RADBOUD disease types", diseases.getNumberOfDiseaseTypes()));
		progress.increment(1);

		progress.status("Mapping RADBOUD biobanks");
		newBiobanks = 0;
		existingBiobanks = 0;
		promiseDataParser.parse(promiseCredentials, 0, biobankEntity ->
		{
			if (!shouldMap(biobankEntity)) return;

			String biobankId = getBiobankId(biobankEntity);
			if (samples.hasSamplesFor(biobankId))
			{
				Entity bbmriSampleCollection = dataService.findOneById(SAMPLE_COLLECTIONS_ENTITY, biobankId);
				if (bbmriSampleCollection == null)
				{
					bbmriSampleCollection = biobankMapper.mapNewBiobank(biobankEntity, samples, diseases);
					dataService.add(SAMPLE_COLLECTIONS_ENTITY, bbmriSampleCollection);
					newBiobanks++;
				}
				else
				{
					bbmriSampleCollection = biobankMapper.mapExistingBiobank(biobankEntity, samples, diseases,
							bbmriSampleCollection);
					dataService.update(SAMPLE_COLLECTIONS_ENTITY, bbmriSampleCollection);
					existingBiobanks++;
				}
			}
			else
			{
				progress.status(format("No samples found for biobank with id %s", biobankId));
			}
		});

		progress.status(format("Mapped %d new biobanks and %d existing biobanks", newBiobanks, existingBiobanks));
		progress.increment(1);
		return newBiobanks + existingBiobanks;
	}

	static String getBiobankId(Map<String, String> radboudEntity)
	{
		return radboudEntity.get(XML_ID) + '_' + radboudEntity.get(XML_IDAA);
	}

	/**
	 * Biobanks with an IDAA < 100 (the "parels") should not be included in the catalogue.
	 */
	private boolean shouldMap(Map<String, String> radboudEntity)
	{
		return Integer.valueOf(radboudEntity.get(XML_IDAA)) >= 100;
	}
}
