package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.UpdateJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;

public class UpdateJobFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public UpdateJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.UPDATE_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob() {
        UpdateJob updateJob = new UpdateJob();
        updateJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.UPDATE_JOB).get());
        return updateJob;
    }

}
