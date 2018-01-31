package uk.gov.ofwat.jobber.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import uk.gov.ofwat.jobber.domain.*;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.factory.*;
import uk.gov.ofwat.jobber.domain.jobs.RequestValidationJob;
import uk.gov.ofwat.jobber.domain.jobs.ResponseValidationJob;
import uk.gov.ofwat.jobber.repository.*;

import java.util.*;

@Service
@ConfigurationProperties("jobServiceProperties")
public class JobService {

    Logger logger = LoggerFactory.getLogger(JobService.class);

    private final JobMonitor jobMonitor;

    private final JobStatusRepository jobStatusRepository;

    private final JobTypeRepository jobTypeRepository;

    private final JobBaseRepository jobBaseRepository;

    private final JobServiceProperties jobServiceProperties;

    private final JobOriginatorRepository jobOriginatorRepository;

    private final JobTargetRepository jobTargetRepository;

    private final JobDataRepository jobDataRepository;

    private List<JobListener> jobListeners = new ArrayList<JobListener>();

    public JobService(JobMonitor jobMonitor,
                      JobTypeRepository jobTypeRepository,
                      JobBaseRepository jobBaseRepository,
                      JobStatusRepository jobStatusRepository,
                      JobServiceProperties jobServiceProperties,
                      JobOriginatorRepository jobOriginatorRepository,
                      JobTargetRepository jobTargetRepository,
                      JobDataRepository jobDataRepository){
        //this.defaultJobRepository = defaultJobRepository;
        this.jobMonitor = jobMonitor;
        this.jobTypeRepository = jobTypeRepository;
        this.jobBaseRepository = jobBaseRepository;
        this.jobStatusRepository = jobStatusRepository;
        this.jobServiceProperties = jobServiceProperties;
        this.jobTargetRepository = jobTargetRepository;
        this.jobOriginatorRepository = jobOriginatorRepository;
        this.jobDataRepository = jobDataRepository;
    }

    /**
     * Create a job without a payload.
     * @param jobInformation
     * @return
     */
    public Job createJob(JobInformation jobInformation){
        Job job = createJobFromFactory(jobInformation);
        job = assignTarget(job, jobInformation);
        job = assignOriginator(job, jobInformation);
        job = assignData(job, jobInformation);
        job = assignJobStatus(job);
        job.setUuid(UUID.randomUUID());
        job = (Job)jobBaseRepository.save(job);
        return job;
    }

    private Job assignJobStatus(Job job){
        Optional<JobStatus> jobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_ACCEPTED);
        job.setJobStatus(jobStatus.get());
        return job;
    }

    private Job assignTarget(Job job, JobInformation jobInformation){
        Target target = jobTargetRepository.findByName(jobInformation.getTarget()).get();
        job.setTarget(target);
        return job;
    }

    private Job assignOriginator(Job job, JobInformation jobInformation){
        String originatorName = jobServiceProperties.getWhoAmI();
        if(jobInformation.getOriginator() != ""){
            originatorName = jobInformation.getOriginator();
        }
        Originator originator = jobOriginatorRepository.findByName(originatorName).get();
        job.setOriginator(originator);
        return job;
    }

    private Job assignData(Job job, JobInformation jobInformation){
        if(jobInformation.getData() != ""){
            JobData jobData = new JobData();
            jobData.setData(Base64.getEncoder().encodeToString(jobInformation.getData().getBytes()));
            jobDataRepository.save(jobData);
            job.setJobData(jobData);
        }
        return job;
    }

    private Job createJobFromFactory(JobInformation jobInformation){
        AbstractJobFactory factory;
        HashMap<String, String> metaData =  jobInformation.getMetadata();
        switch (jobInformation.getType()) {
            case JobTypeConstants.QUERY_JOB_STATUS:
                factory = new QueryJobFactory(jobTypeRepository);
                break;
            case JobTypeConstants.DATA_JOB:
                factory = new DataJobFactory(jobTypeRepository);
                break;
            case JobTypeConstants.GET_NEW_JOB:
                factory = new GetNewJobFactory(jobTypeRepository);
                break;
            case JobTypeConstants.REQUEST_VALIDATION_JOB:
                factory = new RequestValidationJobFactory(jobTypeRepository);
                break;
            case JobTypeConstants.RESPONSE_VALIDATION_JOB:
                factory = new ResponseValidationJobFactory(jobTypeRepository);
                break;
            case JobTypeConstants.UPDATE_JOB:
                factory = new UpdateJobFactory(jobTypeRepository);
                break;
            default:
                factory = new DefaultJobFactory(jobTypeRepository);
                break;
        }
        Job job = factory.createNewJob(metaData);
        return job;
    }

    public List<Job> getUnprocessedJobs(){
        JobStatus unprocessedJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_ACCEPTED).get();
        return jobBaseRepository.findDistinctJobsByJobStatusOrderByCreatedDateAsc(unprocessedJobStatus);
    }

    public List<Job> getUnprocessedJobsForMe(){
        JobStatus unprocessedJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_ACCEPTED).get();
        Target target = jobTargetRepository.findByName(jobServiceProperties.getWhoAmI()).get();
        return jobBaseRepository.findDistinctJobsByJobStatusAndTargetOrderByCreatedDateAsc(unprocessedJobStatus, target);
    }

    public Optional<Job> getNextJobForMe(){
        Target target = jobTargetRepository.findByName(jobServiceProperties.getWhoAmI()).get();
        return getNextJobForTarget(target.getName());
    };

    public Optional<Job> getNextJobForTarget(String jobTarget){
        Target target = jobTargetRepository.findByName(jobTarget).get();
        JobStatus jobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_ACCEPTED).get();
        List<Job> jobs = jobBaseRepository.findDistinctJobsByJobStatusAndTargetOrderByCreatedDateAsc(jobStatus, target);
        return jobs.stream().findFirst();
    };

    public List<Job> getAllJobs(){
        return jobBaseRepository.findAll();
    }

    public Optional<Job> getJobByUuid(UUID uuid){
        return jobBaseRepository.findByUuid(uuid);
    }

    public Job updateJobStatus(String uuid, JobStatus jobStatus){
        UUID u = UUID.fromString(uuid);
        return updateJobStatus(u, jobStatus);
    }

    public Job updateJobStatus(UUID uuid, JobStatus jobStatus){
        Job job = (Job) jobBaseRepository.findByUuid(uuid).get();
        job.setJobStatus(jobStatus);
        return (Job) jobBaseRepository.save(job);
    }

    /**
     * Create a request validation job that can be then processed.
     * @param data
     * @return
     */
    public RequestValidationJob createDataValidationRequest(String data) {
        JobType jobType = jobTypeRepository.findByName(JobTypeConstants.REQUEST_VALIDATION_JOB).get();
        JobInformation ji = new JobInformation.Builder(jobServiceProperties.getDefaultTarget())
                .data(data)
                .type(jobType.getName())
                .build();
        RequestValidationJob job = (RequestValidationJob) createJob(ji);
        return job;
    };

    /**
     * Test that we can create a response to a request.
     * @param responseData
     * @param requestValidationJob
     * @return
     */
    public ResponseValidationJob createDataValidationResponse(String responseData, RequestValidationJob requestValidationJob){
        JobType jobType = jobTypeRepository.findByName(JobTypeConstants.RESPONSE_VALIDATION_JOB).get();
        //Create a response job that goes to the request.
        JobInformation ji = new JobInformation.Builder(requestValidationJob.getOriginator().getName())
                .data(responseData)
                .type(jobType.getName())
                .build();
        ResponseValidationJob job = (ResponseValidationJob) createJob(ji);
        return job;
    }

    /**
     * Add a listener for a job.
     * @param jobListener
     */
    public void addJobListener(JobListener jobListener){
        jobListeners.add(jobListener);
    }

    public void updateJobListeners(Job job){
        jobListeners.stream().filter(jobListener -> jobListener.getUUID().equals(job.getUuid()))
                .forEachOrdered(jobListener -> jobListener.update(job));
    }

}
