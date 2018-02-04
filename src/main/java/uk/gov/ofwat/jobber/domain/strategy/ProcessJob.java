package uk.gov.ofwat.jobber.domain.strategy;

import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.List;

public abstract class ProcessJob {

    JobService jobService;

    public ProcessJob(JobService jobService){
        this.jobService = jobService;
    }

    public abstract List<Job> process(Job job);
}
