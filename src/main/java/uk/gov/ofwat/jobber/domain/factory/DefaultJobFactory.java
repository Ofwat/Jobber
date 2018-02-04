package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.DefaultJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;

import java.util.HashMap;

public class DefaultJobFactory implements AbstractJobFactory {
    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public DefaultJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.DEFAULT_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob(JobInformation jobInformation) {
        DefaultJob defaultJob = new DefaultJob();
        defaultJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.DEFAULT_JOB).get());
        return defaultJob;
    }
}
