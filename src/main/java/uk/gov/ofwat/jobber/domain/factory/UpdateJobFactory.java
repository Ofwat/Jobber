package uk.gov.ofwat.jobber.domain.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.UpdateJob;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;

import java.util.HashMap;
import java.util.Map;

public class UpdateJobFactory implements AbstractJobFactory {

    Logger logger = LoggerFactory.getLogger(UpdateJobFactory.class);

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    private final JobStatusRepository jobStatusRepository;

    UpdateJob updateJob;

    public UpdateJobFactory(JobTypeRepository jobTypeRepository, JobStatusRepository jobStatusRepository){
        this.jobType = JobTypeConstants.UPDATE_JOB;
        this.jobTypeRepository = jobTypeRepository;
        this.jobStatusRepository = jobStatusRepository;
    }

    @Override
    public Job createNewJob(JobInformation jobInformation) {
        updateJob = new UpdateJob();
        updateJob.setNewStatus(getTargetJobNewStatus(jobInformation).getName());
        updateJob.setMetadata(jobInformation.getMetadata());
        updateJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.UPDATE_JOB).get());
        return updateJob;
    }

    private JobStatus getTargetJobNewStatus(JobInformation jobInformation){
        String status = jobInformation.getTargetJobNewStatus();
        if((status == null) || (status == "")){
            //logger.warn("New status for target job is null for target UUID:{} and UpdateJob with UUID: {}", updateJob.getTargetJobUuid().toString(), updateJob.getUuid().toString());
            logger.warn(" New status for target job is null! ");
            return jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_UNKNOWN).get();
        }else{
            return jobStatusRepository.findOneByName(status).get();
        }
    }

}
