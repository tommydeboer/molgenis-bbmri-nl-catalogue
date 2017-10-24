package org.molgenis.promise.job;

import org.molgenis.data.jobs.model.JobExecutionMetaData;
import org.molgenis.data.jobs.model.JobPackage;
import org.molgenis.data.meta.AttributeType;
import org.molgenis.data.meta.SystemEntityType;
import org.molgenis.promise.PromiseMapperType;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static org.molgenis.data.jobs.model.JobPackage.PACKAGE_JOB;

@Component
public class PromiseJobExecutionMetadata extends SystemEntityType
{
	private static final String SIMPLE_NAME = "PromiseJobExecution";

	static final String JOB_TYPE = "promise";
	static final String BIOBANK_ID = "biobankId";
	static final String CREDENTIALS = "credentials";
	static final String MAPPER = "mapper";

	private final JobExecutionMetaData jobExecutionMetaData;
	private final JobPackage jobPackage;

	PromiseJobExecutionMetadata(JobExecutionMetaData jobExecutionMetaData, JobPackage jobPackage)
	{
		super(SIMPLE_NAME, PACKAGE_JOB);
		this.jobExecutionMetaData = requireNonNull(jobExecutionMetaData);
		this.jobPackage = requireNonNull(jobPackage);
	}

	@Override
	public void init()
	{
		setLabel("ProMISe Job Execution");
		setExtends(jobExecutionMetaData);
		setPackage(jobPackage);

		addAttribute(BIOBANK_ID).setLabel("Biobank Identifier").setNillable(false);
		addAttribute(CREDENTIALS).setLabel("Credentials Entity")
								 .setNillable(false)
								 .setDescription("The identifier of the PromiseCredentials entity to use.");
		addAttribute(MAPPER).setLabel("Mapper Type")
							.setDataType(AttributeType.ENUM)
							.setEnumOptions(Arrays.stream(PromiseMapperType.values())
												  .map(PromiseMapperType::name)
												  .collect(Collectors.toList()))
							.setNillable(false);
	}
}