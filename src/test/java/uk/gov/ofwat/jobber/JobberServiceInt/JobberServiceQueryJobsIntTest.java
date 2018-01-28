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
import uk.gov.ofwat.jobber.domain.constants.JobTargetConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;
import uk.gov.ofwat.jobber.service.JobServiceProperties;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class JobberServiceQueryJobsIntTest {

    Logger logger = LoggerFactory.getLogger(JobberServiceQueryJobsIntTest.class);

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
    public void setup() {
        try {
            Resource stateFile = new ClassPathResource("SampleTestJson1.json");
            InputStream is = stateFile.getInputStream();
            unencodedJson = IOUtils.toString(is);
            is = stateFile.getInputStream();
            base64EncodedJson = Base64.getEncoder().encodeToString(IOUtils.toByteArray(is));
            IOUtils.closeQuietly(is);
        } catch (FileNotFoundException e) {
            logger.error("Unable to  find test file: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("IO Exception when loading test file: {}", e.getMessage());
        }
    }


    @Test
    public void shouldGetAllUnprocessedJobsForMe(){

    }

    @Test
    public void shouldGetNextUnprocessedJobForMe(){

    }


    @Test
    public void shouldGetAllUnprocessedJobs() {
        Job processedJob1;
        Job processedJob2;
        Job unprocessedJob1;
        Job unprocessedJob2;
        List<Job> unprocessedJobs;
        Given:
        {
            processedJob1 = createProcessedJob();
            processedJob2 = createProcessedJob();
            unprocessedJob1 = createUnprocessedJob();
            unprocessedJob2 = createUnprocessedJob();
        }
        When:
        {
            unprocessedJobs = jobService.getUnprocessedJobs();
        }
        Then:
        {
            assertThat(unprocessedJobs.size(), is(2));
            assertTrue(unprocessedJobs.containsAll(new ArrayList<Job>(Arrays.asList(unprocessedJob1, unprocessedJob2))));
        }
    }

    @Test
    public void shouldGetAJobByUuid() {
        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        When:
        {
            jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job) jobService.getJobByUuid(job.getUuid()).get();
        }
        Then:
        {
            assertThat(retrievedJob, notNullValue());
            assertEquals(retrievedJob.getUuid(), job.getUuid());
            assertEquals(retrievedJob.getId(), job.getId());
            assertEquals(retrievedJob.getJobType(), job.getJobType());
            assertEquals(retrievedJob.getJobStatus().getName(), JobStatusConstants.RESPONSE_ACCEPTED);
        }
    }

    private Job createProcessedJob() {
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
        Job job = jobService.createJob(jobInformation);
        JobStatus jobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_SUCCESS).get();
        job.setJobStatus(jobStatus);
        job = (Job) jobBaseRepository.save(job);
        return job;
    }

    ;

    private Job createUnprocessedJob() {
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
        return jobService.createJob(jobInformation);
    }

    ;

    private Job createUpdateJob() {
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).type(JobTypeConstants.UPDATE_JOB).build();
        Job updateJob = jobService.createJob(jobInformation);
        return updateJob;
    };

}