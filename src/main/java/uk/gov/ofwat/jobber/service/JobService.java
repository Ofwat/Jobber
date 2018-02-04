package uk.gov.ofwat.jobber.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import uk.gov.ofwat.jobber.domain.*;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.factory.*;
import uk.gov.ofwat.jobber.domain.jobs.RequestValidationJob;
import uk.gov.ofwat.jobber.domain.jobs.ResponseValidationJob;
import uk.gov.ofwat.jobber.domain.jobs.UpdateJob;
import uk.gov.ofwat.jobber.domain.strategy.ProcessDataJob;
import uk.gov.ofwat.jobber.domain.strategy.ProcessJob;
import uk.gov.ofwat.jobber.domain.strategy.ProcessUpdateJob;
import uk.gov.ofwat.jobber.repository.*;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@ConfigurationProperties("jobServiceProperties")
public class JobService {

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);

    protected final Lock readLock = readWriteLock.readLock();

    protected final Lock writeLock = readWriteLock.writeLock();

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
        job.setMetadata(jobInformation.getMetadata());
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
        Target target = jobTargetRepository.findByName(jobInformation.getTargetPlatform()).get();
        job.setTarget(target);
        return job;
    }

    private Job assignOriginator(Job job, JobInformation jobInformation){
        String originatorName = jobServiceProperties.getWhoAmI();
        if(jobInformation.getOriginatorPlatform() != ""){
            originatorName = jobInformation.getOriginatorPlatform();
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
                factory = new UpdateJobFactory(jobTypeRepository, jobStatusRepository);
                break;
            default:
                factory = new DefaultJobFactory(jobTypeRepository);
                break;
        }
        Job job = factory.createNewJob(jobInformation);
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

    private Job updateJobStatus(String uuid, JobStatus jobStatus){
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

    public UpdateJob createUpdateJob(UUID uuid, String target, JobStatus jobStatus, HashMap<String, String> metadata){
        //Todo the metadata bit will only work with the dataJobs at the moment.
        JobInformation jobInformation = new JobInformation.Builder(target)
                .type(JobTypeConstants.UPDATE_JOB)
                .originator(jobServiceProperties.getWhoAmI())
                .setMetaData(metadata)
                .targetJobUuid(uuid.toString())
                .tartgetJobNewStatus(jobStatus.getName())
                .build();
        UpdateJob job = (UpdateJob) createJob(jobInformation);
        job.setTargetJobUuid(uuid);
        return job;
    }

    public Optional<Job> processNextJob(){
        ProcessJob processJob;
        List<Job> notifyJobs;
        Optional<Job> job = getNextJobForMe();
        if(job.isPresent()){
            //Process the job.
            Job j = job.get();
            logger.info("Processing JOB with UUID: {} of type {} originated from {} and targeted to {} with id {}",
                    j.getUuid().toString(),
                    j.getJobType().getName(),
                    j.getOriginator().getName(),
                    j.getTarget().getName(),
                    j.getId().toString());

            processJob = getProcessJob(j.getJobType());
            notifyJobs = processJob.process(j);
            notifyListeners(notifyJobs);
        }
        return job;
    }

    private void notifyListeners(List<Job> notifyJobs){
        notifyJobs.stream().forEachOrdered(updateJob -> {
            updateJobListeners(updateJob);
        });
    }

    private ProcessJob getProcessJob(JobType jobType){
        //We need to have a strategy here
        //Just deal with, updateJobs, dataJobs for the time being.
        ProcessJob processJob;
        switch (jobType.getName()){
            case JobTypeConstants.DATA_JOB:
                processJob = new ProcessDataJob(this, jobStatusRepository, jobBaseRepository);
                break;
            case JobTypeConstants.UPDATE_JOB:
                processJob = new ProcessUpdateJob(this, jobStatusRepository, jobBaseRepository);
                break;
            default:
                processJob = new ProcessUpdateJob(this, jobStatusRepository, jobBaseRepository);
                break;
        }
        return processJob;
    }

    /**
     * Add a listener for a job.
     * @param jobListener
     */
    public JobListener addJobListener(JobListener jobListener){
        this.writeLock.lock();
        try {
            // Add the listener to the list of registered listeners
            this.jobListeners.add(jobListener);
        }
        finally {
            // Unlock the writer lock
            this.writeLock.unlock();
        }
        return jobListener;
    }

    public void removeJobListener(JobListener jobListener){
        this.writeLock.lock();
        try {
            // Remove the listener from the list of registered listeners
            this.jobListeners.remove(jobListener);
        }
        finally {
            // Unlock the writer lock
            this.writeLock.unlock();
        }
    }
    public void removeAllJobListeners(){
        this.writeLock.lock();
        try {
            this.jobListeners.clear();
        }
        finally {
            // Unlock the writer lock
            this.writeLock.unlock();
        }
    }

    public void updateJobListeners(Job job){
        // Lock the list of listeners for reading
        this.readLock.lock();
        try {
            // Notify each of the listeners in the list of registered listeners
            this.jobListeners.forEach(jobListener -> {
                if (jobListener.getUUID().equals(job.getUuid())){
                    jobListener.update(job);
                };
            });
        }
        finally {
            // Unlock the reader lock
            this.readLock.unlock();
        }
    }

}
