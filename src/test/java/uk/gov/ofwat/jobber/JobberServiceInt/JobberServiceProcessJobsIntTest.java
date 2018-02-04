package uk.gov.ofwat.jobber.JobberServiceInt;

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
import uk.gov.ofwat.jobber.domain.jobs.DataResponseJob;
import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.domain.jobs.attributes.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetPlatformConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.DataJob;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;
import uk.gov.ofwat.jobber.service.JobServiceProperties;

import java.io.*;
import java.util.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class JobberServiceProcessJobsIntTest {

    Logger logger = LoggerFactory.getLogger(JobberServiceProcessJobsIntTest.class);

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
    /*
    We will create a data job and a mock job update for the job followed by a response to the data job.
     */
    public void shouldCreateAndProcessAndUpdateADataJob(){
        HashMap<String, String> metaData = createMetadataWithFountainReportInfo();
        HashMap<String, String> emptyMetaData = new HashMap<String, String>();
        DataJob job;
        DataJob updatedDataJob;
        JobInformation jobInformation;
        JobStatus updateJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_TARGET_PROCESSING).get();
        Given:{
            //Create an update response from Gofer!
            job = jobService.createDataJob(JobTargetPlatformConstants.DCS, metaData, unencodedJson);
        }
        When:{
            //This should send an update job to update this job as we are targetting ourselves! ?!
            jobService.processNextJob();

            //Process the generated update job.
            jobService.processNextJob();

            updatedDataJob = (DataJob) jobService.getJobByUuid(job.getUuid()).get();
        }
        Then:{
            assertThat(jobService.getUnprocessedJobs().size(), is(0));
            //This won't work as we are updating the same job in the job process, which would never happen unless the originator and target are the same.
            //assertThat(updatedDataJob.getJobStatus().getName(), is(JobStatusConstants.RESPONSE_PENDING_ACTION));
            assertThat(jobService.getPendingDataJobs().size(), is(0));
        }
    }


    @Test
    /*
    We will create a data job and a mock job update for the job followed by a response to the data job.
     */
    public void shouldLinkAndUpdateADataJob(){
        HashMap<String, String> metaData = createMetadataWithFountainReportInfo();
        HashMap<String, String> emptyMetaData = new HashMap<String, String>();
        DataJob job;
        DataJob updatedDataJob;
        DataResponseJob dataResponseJob;
        DataResponseJob retrievedDataResponseJob;
        JobInformation jobInformation;
        JobStatus updateJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_TARGET_PROCESSING).get();
        Given:{
            //Create an update response from Gofer!
            job = jobService.createDataJob(JobTargetPlatformConstants.DCS, metaData, unencodedJson);
        }
        When:{
            //This should send an update job to update this job as we are targetting ourselves! ?!
            jobService.processNextJob();
            //Process the generated update job.
            jobService.processNextJob();
            dataResponseJob = (DataResponseJob) jobService.createDataResponseJob(job, unencodedJson);
            jobService.processNextJob();
            updatedDataJob = (DataJob) jobService.getJobByUuid(job.getUuid()).get();
            retrievedDataResponseJob = (DataResponseJob) jobService.getJobByUuid(dataResponseJob.getUuid()).get();
        }
        Then:{
            assertThat(jobService.getUnprocessedJobs().size(), is(0));
            assertThat(jobService.getPendingDataJobs().size(), is(1)); //We have a linked job to process!
            assertThat(retrievedDataResponseJob.getJobStatus().getName(), is(JobStatusConstants.RESPONSE_SUCCESS));
            assertThat(updatedDataJob.getJobStatus().getName(), is(JobStatusConstants.RESPONSE_LINKED));
            assertThat(updatedDataJob.getLinkedJobUuid().get(), is(retrievedDataResponseJob.getUuid()));
            assertThat(retrievedDataResponseJob.getJobData().getData(), is(base64EncodedJson));
        }
    }


    public void shouldCreateAndProcessAndRespondToADataJobFromDcsToFountain(){

    }

    private HashMap<String, String> createMetadataWithFountainReportInfo(){
        HashMap<String, String> metadata = new HashMap<String, String>();
        metadata.put("fountainReportId", "999");
        metadata.put("companyId", "1");
        metadata.put("auditComment", "Audit comment form test");
        metadata.put("runId", "100");
        metadata.put("excelDocMongoId", "Mongo_Id");
        return metadata;
    }

    @Test
    /**
     * TODO We can implement this after we have it working!
     */
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
    /**
     * We will set up a mock validation response job and make sure that it gets processed -
     * i.e and update is sent back to the originator.
     */
    public void shouldProcessADataValidationResponse(){

    }

    @Test
    /**
     * We will set up an update job and make sure that the update required in the job is processed.
     */
    public void shouldProcessAnUpdateJob(){
        Given:{

        }
        When:{

        }
        Then:{

        }
    }

    @Test
    /**
     * We will set up a validation request and send a response from the target.
     */
    public void shouldProcessAValidationRequest(){

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

