package uk.gov.ofwat.jobber.JobberServiceInt;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobListener;
import uk.gov.ofwat.jobber.domain.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetPlatformConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;
import uk.gov.ofwat.jobber.service.JobServiceProperties;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class JobListenerIntTest {

    public Logger logger = LoggerFactory.getLogger(JobListenerIntTest.class);

    @Autowired
    JobServiceProperties jobServiceProperties;

    @Autowired
    JobStatusRepository jobStatusRepository;

    @Autowired
    JobBaseRepository jobBaseRepository;

    @Autowired
    JobService jobService;

    @Before
    public void setup(){

    }

    @Test
    public void shouldAddAJobListenerAndGetAnUpdate(){
        Job job1;
        Job job2;
        Job updateJob;
        TestJobListener testJobListener;
        HashMap<String, String> metadata = new HashMap<String, String>();
        JobStatus jobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_PROCESSING).get();
        Given:{
            job1 = createUnprocessedJob();
            job2 = createUnprocessedJob();
            testJobListener = new TestJobListener(job2.getUuid());
            testJobListener = (TestJobListener) jobService.addJobListener(testJobListener);
        }
        When:{
            updateJob = jobService.createUpdateJob(job2.getUuid(), JobTargetPlatformConstants.DCS, jobStatus, metadata);
            //TODO Do the process.
            Optional<Job> job = jobService.processNextJob();
        }
        Then:{
            assertThat(testJobListener.getUUID(),is(job2.getUuid()));
            assertEquals(testJobListener.getUpdateCount(), 1);
        }
    }

    @Test
    public void shouldRemoveAjobListenerAndNotGetAnUpdate(){
        Given:{}
        When:{}
        Then:{}
    }

    @Test
    public void shouldTestForThreadSafety(){
        Given:{}
        When:{}
        Then:{}
    }

    private class TestJobListener extends JobListener{

        private JobStatus currentJobStatus;

        private Job currentJob;

        private int updateCount = 0;

        public JobStatus getCurrentJobStatus() {
            return currentJobStatus;
        }

        public int getUpdateCount(){
            return this.updateCount;
        }

        public Job getCurrentJob() {
            return currentJob;
        }

        public TestJobListener(UUID uuid) {
            super(uuid);
        }

        @Async
        public synchronized CompletableFuture<Job> update (Job job) {
            // Print the name of the newly added animal
            logger.info("Added a new job with uuid '" + job.getUuid().toString() + "'");
            this.currentJobStatus = job.getJobStatus();
            this.currentJob = job;
            updateCount++;
            return CompletableFuture.completedFuture(job);
        }

    }

    private Job createProcessedJob(){
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
        Job job = jobService.createJob(jobInformation);
        JobStatus jobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_SUCCESS).get();
        job.setJobStatus(jobStatus);
        job = (Job)jobBaseRepository.save(job);
        return job;
    };

    private Job createUnprocessedJob(){
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
        return jobService.createJob(jobInformation);
    };

    private Job createUpdateJob(){
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).type(JobTypeConstants.UPDATE_STATUS_JOB).build();
        Job updateJob = jobService.createJob(jobInformation);
        return updateJob;
    };


}



