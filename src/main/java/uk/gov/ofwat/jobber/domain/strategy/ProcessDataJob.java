package uk.gov.ofwat.jobber.domain.strategy;

import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.domain.jobs.UpdateStatusJob;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.ArrayList;
import java.util.HashMap;
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

    //These should only get processed when we are the target!
    public List<Job> process(Job job){
        //Update the job status to say we have looked at it, send the update to be toargeted at the job originator.
        List<Job> jobs = new ArrayList<>();
        UpdateStatusJob updateStatusJob = jobService.createUpdateStatusJob(
                job.getUuid(), job.getOriginator().getName(), jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_TARGET_PROCESSING).get(),
                new HashMap<String, String>());
        jobs.add(updateStatusJob);
        //We need to update the job to show we have looked at it otherwise it will get processed again.
        job.setJobStatus(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_PENDING_ACTION).get());
        job = (Job) jobBaseRepository.save(job);
        job.alertJobObservers();
        jobs.add(job);
        return jobs;
    }
}