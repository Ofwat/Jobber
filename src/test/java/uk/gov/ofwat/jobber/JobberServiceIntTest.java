package uk.gov.ofwat.jobber;

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
import uk.gov.ofwat.jobber.domain.JobData;
import uk.gov.ofwat.jobber.domain.JobResponse;
import uk.gov.ofwat.jobber.domain.constants.JobResponseTypeConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobResponseRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobService;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class JobberServiceIntTest {

    Logger logger = LoggerFactory.getLogger(JobberServiceIntTest.class);

    @Autowired
    JobService jobService;

    @Autowired
    JobTypeRepository jobTypeRepository;

    @Autowired
    JobBaseRepository jobBaseRepository;

    @Autowired
    JobResponseRepository jobResponseRepository;

    private String base64EncodedJson;

    @Before
    public void setup(){
        try {
            Resource stateFile = new ClassPathResource("SampleTestJson1.json");
            base64EncodedJson = Base64.getEncoder().encodeToString(IOUtils.toByteArray(stateFile.getInputStream()));
        }catch(FileNotFoundException e){
            logger.error("Unable to  find test file: {}", e.getMessage());
        }catch(IOException e){
            logger.error("IO Exception when loading test file: {}", e.getMessage());
        }
    }

    @Test
    public void shouldCreateADefaultJobWithNoPayload(){
        Job job;
        Job retrievedJob;
        When:{
            job = jobService.createJob(JobTypeConstants.DEFAULT_JOB);
            retrievedJob = (Job)jobBaseRepository.findOne(job.getId());
        }
        Then:{
            assertThat(retrievedJob, notNullValue());
            assertEquals(retrievedJob.getId(), job.getId());
            assertEquals(retrievedJob.getJobType(), job.getJobType());
        }
    }

    @Test
    public void shouldCreateADefaultJobAPayload(){
        Job job;
        Job retrievedJob;
        Given:{
            assert base64EncodedJson != null;
            assert base64EncodedJson.length() > 1;
        }
        When:{
            JobData jobData = new JobData();
            jobData.setData(base64EncodedJson);
            job = jobService.createJob(JobTypeConstants.DEFAULT_JOB, jobData);
            retrievedJob = (Job)jobBaseRepository.findOne(job.getId());
        }
        Then:{
            assertThat(retrievedJob, notNullValue());
            assertEquals(retrievedJob.getId(), job.getId());
            assertEquals(retrievedJob.getJobType(), job.getJobType());
            assertEquals(retrievedJob.getJobData().getData(), base64EncodedJson);
        }
    }

    @Test
    public void shouldCreateADefaultUnprocessedJob(){
        Job job;
        Job retrievedJob;
        When:{
            job = jobService.createJob(JobTypeConstants.DEFAULT_JOB);
            retrievedJob = (Job)jobBaseRepository.findOne(job.getId());
        }
        Then:{
            assertThat(retrievedJob, notNullValue());
            assertEquals(retrievedJob.getId(), job.getId());
            assertEquals(retrievedJob.getJobType(), job.getJobType());
            assertEquals(retrievedJob.getJobResponse().getName(), JobResponseTypeConstants.RESPONSE_ACCEPTED);
        }
    }

    @Test
    public void shouldGetAllUnprocessedJobs(){
        Job processedJob1;
        Job processedJob2;
        Job unprocessedJob1;
        Job unprocessedJob2;
        List<Job> unprocessedJobs;
        Given:{
            processedJob1 = createProcessedJob();
            processedJob2 = createProcessedJob();
            unprocessedJob1 = createUnprocessedJob();
            unprocessedJob2 = createUnprocessedJob();
        }
        When:{
            unprocessedJobs = jobService.getUnprocessedJobs();
        }
        Then:{
            assertThat(unprocessedJobs.size(), is(2));
            assertTrue(unprocessedJobs.containsAll(new ArrayList<Job>(Arrays.asList(unprocessedJob1, unprocessedJob2))));
        }
    }

    @Test
    public void shouldArchiveJob(){
        fail();
    }

    @Test
    public void shouldHaveTargetFountain(){
        Job job;
        Job retrievedJob;
        When:{
            job = jobService.createJob(JobTypeConstants.DEFAULT_JOB);
            retrievedJob = (Job)jobBaseRepository.getOne(job.getId());
        }
        Then:{
            assertThat(job.getTarget(), notNullValue());
            assertThat(job.getTarget().getName(), is(JobTargetConstants.FOUNTAIN));
        }
    }

    @Test
    public void shouldHaveTargetDcs(){
        Job job;
        Job retrievedJob;
        When:{
            job = jobService.createJob(JobTypeConstants.DEFAULT_JOB);
            retrievedJob = (Job)jobBaseRepository.getOne(job.getId());
        }
        Then:{
            assertThat(job.getTarget(), notNullValue());
            assertThat(job.getTarget().getName(), is(JobTargetConstants.DCS));
        }
    }

    @Test
    public void shouldUpdateJobStatus(){
        Job processedJob1;
        Job processedJob2;
        Job unprocessedJob1;
        Job unprocessedJob2;
        Job updateJob;
        List<Job> unprocessedJobs;
        Given:{
            updateJob = createUpdateJob();
        }
        When:{

        }
        Then:{
            fail();
        }
    }

    private Job createProcessedJob(){
        Job job = jobService.createJob(JobTypeConstants.DEFAULT_JOB);
        JobResponse jobResponse = jobResponseRepository.findOneByName(JobResponseTypeConstants.RESPONSE_SUCCESS).get();
        job.setJobResponse(jobResponse);
        job = (Job)jobBaseRepository.save(job);
        return job;
    };

    private Job createUnprocessedJob(){
        return jobService.createJob(JobTypeConstants.DEFAULT_JOB);
    };

    private Job createUpdateJob(){
        Job updateJob = jobService.createJob(JobTypeConstants.UPDATE_JOB);
        return updateJob;
    };
}

