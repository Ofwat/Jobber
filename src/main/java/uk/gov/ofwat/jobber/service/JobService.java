package uk.gov.ofwat.jobber.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.gov.ofwat.jobber.domain.*;
import uk.gov.ofwat.jobber.domain.constants.JobResponseTypeConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.constants.OriginatorTargetConstants;
import uk.gov.ofwat.jobber.domain.factory.AbstractJobFactory;
import uk.gov.ofwat.jobber.domain.factory.QueryJobFactory;
import uk.gov.ofwat.jobber.repository.*;

import java.util.List;
import java.util.Optional;

@Service
@ConfigurationProperties("jobServiceProperties")
public class JobService {

    Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobMonitor jobMonitor;

    private final JobResponseRepository jobResponseRepository;

    private final JobTypeRepository jobTypeRepository;

    private final JobBaseRepository jobBaseRepository;

    private final JobServiceProperties jobServiceProperties;

    private final OriginatorRepository originatorRepository;

    private final TargetRepository targetRepository;

    public JobService(JobMonitor jobMonitor,
                      JobTypeRepository jobTypeRepository,
                      JobBaseRepository jobBaseRepository,
                      JobResponseRepository jobResponseRepository,
                      JobServiceProperties jobServiceProperties,
                      OriginatorRepository originatorRepository,
                      TargetRepository targetRepository){
        //this.defaultJobRepository = defaultJobRepository;
        this.jobMonitor = jobMonitor;
        this.jobTypeRepository = jobTypeRepository;
        this.jobBaseRepository = jobBaseRepository;
        this.jobResponseRepository = jobResponseRepository;
        this.jobServiceProperties = jobServiceProperties;
        this.targetRepository = targetRepository;
        this.originatorRepository = originatorRepository;
    }


    /**
     * Create a job wiht a payload.
     * @param jobTypeName
     * @param payload
     * @return
     */
    public Job createJob(String jobTypeName, JobData jobData){
        Job job = createJob(jobTypeName);
        job.setJobData(jobData);
        jobBaseRepository.save(job);
        return job;
    }

    /**
     * Create a job wihout a payload.
     * @param jobTypeName
     * @return
     */
    public Job createJob(String jobTypeName){
        AbstractJobFactory factory;
                switch (jobTypeName) {
            case JobTypeConstants.QUERY_JOB_STATUS:
                factory = new QueryJobFactory(jobTypeRepository);
            default:
                factory = new QueryJobFactory(jobTypeRepository);
        }
        Job job = factory.createNewJob();

        Target target = targetRepository.findByName(jobServiceProperties.getDefaultTarget()).get();
        Originator originator = originatorRepository.findByName(jobServiceProperties.getDefaultOriginator()).get();
        job.setTarget(target);
        job.setOriginator(originator);

        Optional<JobResponse> jobResponse = jobResponseRepository.findOneByName(JobResponseTypeConstants.RESPONSE_ACCEPTED);
        job.setJobResponse(jobResponse.get());
        job = (Job)jobBaseRepository.save(job);
        return job;
    }

    public List<Job> getUnprocessedJobs(){
        JobResponse unprocessedJobResponse = jobResponseRepository.findOneByName(JobResponseTypeConstants.RESPONSE_ACCEPTED).get();
        return jobBaseRepository.findDistinctJobsByJobResponse(unprocessedJobResponse);
    }

}
