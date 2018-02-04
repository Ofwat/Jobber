package uk.gov.ofwat.jobber.domain.strategy;

import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.UtilConstants;
import uk.gov.ofwat.jobber.domain.jobs.DataJob;
import uk.gov.ofwat.jobber.domain.jobs.DataResponseJob;
import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.domain.jobs.attributes.JobStatus;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.ArrayList;
import java.util.List;

public class ProcessDataResponseJob extends ProcessJob {

    JobStatusRepository jobStatusRepository;

    JobBaseRepository jobBaseRepository;

    public ProcessDataResponseJob(JobService jobService,
                                  JobStatusRepository jobStatusRepository,
                                  JobBaseRepository jobBaseRepository){
        super(jobService);
        this.jobStatusRepository = jobStatusRepository;
        this.jobBaseRepository = jobBaseRepository;
    }

    @Override
    public List<Job> process(Job job) {
        List<Job> updatedJobs = new ArrayList<Job>();
        DataResponseJob dataResponseJob = (DataResponseJob) job;
        JobStatus linkedJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_LINKED).get();
        JobStatus updatedDataJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_SUCCESS).get();
        //Get the original DataJob
        DataJob originalJob = (DataJob) jobService.getJobByUuid(dataResponseJob.getLinkedJobUuid().get()).get();
        //Update it's status to say it has been linked!
        originalJob.setJobStatus(linkedJobStatus);
        //Add a link to this job
        originalJob.getMetadata().put(UtilConstants.LINKED_JOB_KEY, dataResponseJob.getUuid().toString());
        //Update this jobs status
        dataResponseJob.setJobStatus(updatedDataJobStatus);
        //return all the jobs we have modified.
        jobBaseRepository.save(dataResponseJob);
        jobBaseRepository.save(originalJob);
        updatedJobs.add(dataResponseJob);
        updatedJobs.add(originalJob);
        return updatedJobs;
    }
}
