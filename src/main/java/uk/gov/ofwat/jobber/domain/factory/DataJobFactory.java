package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.DataJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;

public class DataJobFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public DataJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.DATA_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob() {
        DataJob dataJob = new DataJob();
        dataJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.DATA_JOB).get());
        return dataJob;
    }

}