package uk.gov.ofwat.jobber.domain.strategy;

import org.hibernate.cfg.NotYetImplementedException;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.strategy.ProcessJob;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.List;

public class ProcessDataJob extends ProcessJob {

    JobBaseRepository jobBaseRepository;

    JobStatusRepository jobStatusRepository;

    public ProcessDataJob(JobService jobService,
                          JobStatusRepository jobStatusRepository,
                          JobBaseRepository jobBaseRepository){
        super(jobService);
        this.jobBaseRepository = jobBaseRepository;
        this.jobStatusRepository = jobStatusRepository;
    }

    public List<Job> process(Job job){
        throw new NotYetImplementedException();
    }
}