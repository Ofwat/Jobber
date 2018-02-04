package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.jobs.DataJob;
import uk.gov.ofwat.jobber.domain.jobs.DataResponseJob;
import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;

public class DataJobResponseFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public DataJobResponseFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.DATA_RESPONSE_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob(JobInformation jobInformation) {
        DataResponseJob dataResponseJob = new DataResponseJob();
        dataResponseJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.DATA_RESPONSE_JOB).get());
        return dataResponseJob;
    }
}
