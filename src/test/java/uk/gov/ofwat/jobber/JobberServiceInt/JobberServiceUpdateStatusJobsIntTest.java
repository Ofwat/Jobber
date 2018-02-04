package uk.gov.ofwat.jobber.JobberServiceInt;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetPlatformConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;
import uk.gov.ofwat.jobber.service.JobServiceProperties;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class JobberServiceUpdateStatusJobsIntTest {

    Logger logger = LoggerFactory.getLogger(JobberServiceUpdateStatusJobsIntTest.class);

    @Autowired
    JobService jobService;

    @Autowired
    JobTypeRepository jobTypeRepository;

    @Autowired
    JobBaseRepository jobBaseRepository;

    @Autowired
    JobStatusRepository jobStatusRepository;

    @Autowired
    JobServiceProperties jobServiceProperties;

    private String base64EncodedJson;

    private String unencodedJson;

    @Before
    public void setup(){
        try {
            Resource stateFile = new ClassPathResource("SampleTestJson1.json");
            InputStream is = stateFile.getInputStream();
            unencodedJson = IOUtils.toString(is);
            is = stateFile.getInputStream();
            base64EncodedJson = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
            IOUtils.closeQuietly(is);
        }catch(FileNotFoundException e){
            logger.error("Unable to  find test file: {}", e.getMessage());
        }catch(IOException e){
            logger.error("IO Exception when loading test file: {}", e.getMessage());
        }
    }

    @Test
    public void shouldArchiveJob(){
/*        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        When:{
            jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job)jobBaseRepository.getOne(job.getId());
        }
        Then:{
            assertThat(retrievedJob, notNullValue());
            assertEquals(retrievedJob.getUuid(), job.getUuid());
            assertEquals(retrievedJob.getId(), job.getId());
            assertEquals(retrievedJob.getJobType(), job.getJobType());
            assertEquals(retrievedJob.getJobStatus().getName(), JobStatusConstants.RESPONSE_TARGET_ACCEPTED);
        }*/
    }

    @Test
    public void shouldHaveTargetFountain(){
        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        When:{
            jobInformation = new JobInformation.Builder(JobTargetPlatformConstants.FOUNTAIN).build();
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job)jobBaseRepository.getOne(job.getId());
        }
        Then:{
            assertThat(job.getTarget(), notNullValue());
            assertThat(job.getTarget().getName(), is(JobTargetPlatformConstants.FOUNTAIN));
        }
    }

    @Test
    public void shouldHaveOriginatorFountain(){
        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        When:{
            jobInformation = new JobInformation.Builder(JobTargetPlatformConstants.DCS).originator(JobTargetPlatformConstants.FOUNTAIN).build();
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job)jobBaseRepository.getOne(job.getId());
        }
        Then:{
            assertThat(job.getOriginator(), notNullValue());
            assertThat(job.getOriginator().getName(), is(JobTargetPlatformConstants.FOUNTAIN));
        }
    }

    @Test
    public void shouldHaveTargetDcs(){
        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        When:{
            jobInformation = new JobInformation.Builder(jobServiceProperties.getWhoAmI()).build();
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job)jobBaseRepository.getOne(job.getId());
        }
        Then:{
            assertThat(job.getTarget(), notNullValue());
            assertThat(job.getTarget().getName(), is(JobTargetPlatformConstants.DCS));
        }
    }

/*    @Test
    public void shouldUpdateJobStatusByUuid(){
        Job processedJob1;
        Job processedJob2;
        Job unprocessedJob1;
        Job updateJob;
        List<Job> unprocessedJobs;
        Job retrievedJob;
        Given:{
            updateJob = createUpdateJob();
            unprocessedJob1 = createUnprocessedJob();
            processedJob1 = createProcessedJob();
            processedJob2 = createProcessedJob();
        }
        When:{
            JobStatus processedJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_TARGET_PROCESSING).get();
            jobService.updateJobStatus(unprocessedJob1.getUuid(), processedJobStatus);
            retrievedJob = (Job)jobBaseRepository.getOne(unprocessedJob1.getId());
        }
        Then:{
            assertEquals(retrievedJob.getJobStatus().getName(), JobStatusConstants.RESPONSE_TARGET_PROCESSING);
        }
    }*/

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
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget())
                .setMetaData(new HashMap<String, String>(){{put("targetJobStatus", JobStatusConstants.RESPONSE_SUCCESS);}{put("targetJobUuid",UUID.randomUUID().toString());}})                .type(JobTypeConstants.UPDATE_STATUS_JOB)
                .build();
        Job updateJob = jobService.createJob(jobInformation);
        return updateJob;
    };
}

