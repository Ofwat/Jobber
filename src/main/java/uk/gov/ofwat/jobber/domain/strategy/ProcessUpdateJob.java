package uk.gov.ofwat.jobber.domain.strategy;

import org.hibernate.cfg.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.jobs.UpdateJob;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.service.JobService;

import java.time.Instant;
import java.util.*;

public class ProcessUpdateJob extends ProcessJob {

    Logger logger = LoggerFactory.getLogger(ProcessUpdateJob.class);

    private final JobStatusRepository jobStatusRepository;

    private final JobBaseRepository jobBaseRepository;

    public ProcessUpdateJob(JobService jobService, JobStatusRepository jobStatusRepository, JobBaseRepository jobBaseRepository){
        super(jobService);
        this.jobStatusRepository = jobStatusRepository;
        this.jobBaseRepository = jobBaseRepository;
    }

    public List<Job> process(Job job){
        List<Job> modifiedJobs = new ArrayList<Job>();
        UpdateJob updateJob = (UpdateJob) job;
        Optional<Job> jobToBeUpdated = getJobToBeUpdated(updateJob);
        if(jobToBeUpdated.isPresent()){

            //Update the target of the update job and set it's data, metadata and status.
            Job updatedJob = updateJob(jobToBeUpdated.get(),
                    jobStatusRepository.findOneByName(updateJob.getNewStatus()).get(),
                    updateJob.getMetadata());
            jobBaseRepository.save(updatedJob);

            //Update tehe job we are processing to say it has completed and can be archived.
            updateJob.setJobStatus(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_SUCCESS).get());
            jobBaseRepository.save(updateJob);

            modifiedJobs.add(updatedJob);
        }else{
            logger.error("Could not find job to update with UUID: {} in updateJob with ID: {}", updateJob.getTargetJobUuid().toString(),
                    updateJob.getId().toString());
        }
        return  modifiedJobs;
    }

    private Optional<Job> getJobToBeUpdated(UpdateJob job){
        return jobService.getJobByUuid(job.getTargetJobUuid());
    }

    private Job updateJob(Job jobToBeUpdated, JobStatus newJobStatus, Map<String, String> metadata){
        jobToBeUpdated.setJobStatus(newJobStatus);
        jobToBeUpdated.getMetadata().putAll(metadata);
        jobToBeUpdated.setLastModifiedDate(Instant.now());
        return jobToBeUpdated;
    }
}
