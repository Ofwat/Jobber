package uk.gov.ofwat.jobber.domain.strategy;

import org.hibernate.cfg.NotYetImplementedException;
import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.List;

public class ProcessDefaultJob extends ProcessJob {

    public ProcessDefaultJob(JobService jobService){
        super(jobService);
    }

    public List<Job> process(Job job){
        throw new NotYetImplementedException();
    }
}