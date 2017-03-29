package org.molgenis.promise;

import org.molgenis.data.DataService;
import org.molgenis.data.UnknownEntityException;
import org.molgenis.promise.mapper.MappingReport;
import org.molgenis.promise.mapper.PromiseMapper;
import org.molgenis.promise.mapper.PromiseMapperFactory;
import org.molgenis.promise.model.PromiseMappingProject;
import org.molgenis.security.core.runas.RunAsSystem;
import org.molgenis.ui.MolgenisPluginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.molgenis.promise.PromiseDataLoaderController.URI;
import static org.molgenis.promise.model.PromiseMappingProjectMetadata.PROMISE_MAPPING_PROJECT;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@Controller
@EnableScheduling
@RequestMapping(URI)
public class PromiseDataLoaderController extends MolgenisPluginController
{
	public static final String ID = "promiseloader";
	static final String URI = MolgenisPluginController.PLUGIN_URI_PREFIX + ID;

	private static final Logger LOG = LoggerFactory.getLogger(PromiseDataLoaderController.class);

	private final DataService dataService;
	private final PromiseMapperFactory promiseMapperFactory;

	@Autowired
	public PromiseDataLoaderController(DataService dataService, PromiseMapperFactory promiseMapperFactory)
	{
		super(URI);
		this.dataService = requireNonNull(dataService);
		this.promiseMapperFactory = requireNonNull(promiseMapperFactory);
	}

	@RequestMapping(method = GET)
	public String init(Model model)
	{
		model.addAttribute("promiseMappingProjectEntityTypeId", PROMISE_MAPPING_PROJECT);
		return "view-promiseloader";
	}

	/**
	 * Returns a list of the project names so they can be listed in the control panel
	 */
	@RequestMapping(value = "projects", method = GET)
	@ResponseBody
	public List<String> projects()
	{
		return dataService.findAll(PROMISE_MAPPING_PROJECT, PromiseMappingProject.class)
				.map(PromiseMappingProject::getName).collect(toList());
	}

	@RequestMapping(value = "map/{name}", method = GET)
	@ResponseBody
	@Transactional
	public MappingReport map(@PathVariable("name") String projectName) throws IOException
	{
		PromiseMappingProject promiseMappingProject = dataService
				.findOneById(PROMISE_MAPPING_PROJECT, projectName, PromiseMappingProject.class);
		if (promiseMappingProject == null)
		{
			throw new UnknownEntityException(format("Unknown promise mapping project [%s]", projectName));
		}
		PromiseMapper promiseMapper = promiseMapperFactory.getMapper(promiseMappingProject.getMapper());
		return promiseMapper.map(promiseMappingProject);
	}

	@Scheduled(cron = "0 0 0 * * *")
	@RunAsSystem
	public void executeScheduled()
	{
		// TODO make configurable via MOLGENIS 'scheduler'

		Stream<PromiseMappingProject> promiseMappingProjectStream = dataService
				.findAll(PROMISE_MAPPING_PROJECT, PromiseMappingProject.class);
		promiseMappingProjectStream.forEach(project ->
		{
			LOG.info("Starting scheduled mapping task for ProMISe biobank " + project.getName());
			PromiseMapper promiseMapper = promiseMapperFactory.getMapper(project.getString("mapper"));
			promiseMapper.map(project);
		});
	}
}
