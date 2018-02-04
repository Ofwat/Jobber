package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.RequestValidationJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;

import java.util.HashMap;

public class RequestValidationJobFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public RequestValidationJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.REQUEST_VALIDATION_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob(JobInformation jobInformation) {
        RequestValidationJob requestValidationJob = new RequestValidationJob();
        requestValidationJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.REQUEST_VALIDATION_JOB).get());
        return requestValidationJob;
    }

}