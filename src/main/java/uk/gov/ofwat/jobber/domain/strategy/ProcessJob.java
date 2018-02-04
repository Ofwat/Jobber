package uk.gov.ofwat.jobber.domain.strategy;

import org.springframework.stereotype.Service;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.List;

public abstract class ProcessJob {

    JobService jobService;

    public ProcessJob(JobService jobService){
        this.jobService = jobService;
    }

    public abstract List<Job> process(Job job);
}
