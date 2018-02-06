package uk.gov.ofwat.jobber.domain.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.domain.jobs.attributes.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.UpdateStatusJob;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;

import java.util.UUID;

public class UpdateStatusJobFactory implements AbstractJobFactory {

    Logger logger = LoggerFactory.getLogger(UpdateStatusJobFactory.class);

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    private final JobStatusRepository jobStatusRepository;

    UpdateStatusJob updateStatusJob;

    public UpdateStatusJobFactory(JobTypeRepository jobTypeRepository, JobStatusRepository jobStatusRepository){
        this.jobType = JobTypeConstants.UPDATE_STATUS_JOB;
        this.jobTypeRepository = jobTypeRepository;
        this.jobStatusRepository = jobStatusRepository;
    }

    @Override
    public Job createNewJob(JobInformation jobInformation) {
        if(jobInformation.getMetadata().get("targetJobUuid") == null){
            //Check the meta data.
            logger.error("Unable to parse target UUID for update job. Target UUID required.");
            throw new RuntimeException("Unable to parse target UUID for update job. Target UUID required.");
        }
        if(jobInformation.getMetadata().get("targetJobStatus") == null){
            //Check the meta data.
            logger.error("Unable to parse target status for update job. Target status required.");
            throw new RuntimeException("Unable to parse target status for update job. Target status required.");
        }

        updateStatusJob = new UpdateStatusJob();
        updateStatusJob.setTargetJobUuid(jobInformation.getMetadata().get("targetJobUuid"));
        updateStatusJob.setNewStatus(getTargetJobNewStatus(jobInformation).getName());
        updateStatusJob.setMetadata(jobInformation.getMetadata());
        updateStatusJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.UPDATE_STATUS_JOB).get());
        return updateStatusJob;
    }

    private JobStatus getTargetJobNewStatus(JobInformation jobInformation){
        String status = jobInformation.getMetadata().get("targetJobStatus");
        if((status == null) || (status == "")){
            //logger.warn("New status for target job is null for target UUID:{} and UpdateStatusJob with UUID: {}", updateStatusJob.getTargetJobUuid().toString(), updateStatusJob.getUuid().toString());
            logger.warn(" New status for target job is null! ");
            return jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_UNKNOWN).get();
        }else{
            return jobStatusRepository.findOneByName(status).get();
        }
    }

}
