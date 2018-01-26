package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.QueryJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;

public class QueryJobFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public QueryJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.QUERY_JOB_STATUS;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob() {
        QueryJob queryJob = new QueryJob();
        queryJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.QUERY_JOB_STATUS).get());
        return queryJob;
    }

}
