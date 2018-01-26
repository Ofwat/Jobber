package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.ResponseValidationJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;

public class ResponseValidationJobFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public ResponseValidationJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.RESPONSE_VALIDATION_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob() {
        ResponseValidationJob responseValidationJob = new ResponseValidationJob();
        responseValidationJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.RESPONSE_VALIDATION_JOB).get());
        return responseValidationJob;
    }

}