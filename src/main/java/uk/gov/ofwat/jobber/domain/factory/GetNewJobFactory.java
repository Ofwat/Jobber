package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.GetNewJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;

public class GetNewJobFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public GetNewJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.GET_NEW_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob() {
        GetNewJob getNewJob = new GetNewJob();
        getNewJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.GET_NEW_JOB).get());
        return getNewJob;
    }

}