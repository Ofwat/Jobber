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
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.DataJob;
import uk.gov.ofwat.jobber.domain.jobs.RequestValidationJob;
import uk.gov.ofwat.jobber.domain.jobs.UpdateJob;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;
import uk.gov.ofwat.jobber.service.JobServiceProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class JobberServiceCreateJobsIntTest {

    Logger logger = LoggerFactory.getLogger(JobberServiceCreateJobsIntTest.class);

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
    public void shouldCreateADefaultJobWithNoPayload(){
        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        Give:{
            jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
        }
        When:{
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job)jobBaseRepository.findOne(job.getId());
        }
        Then:{
            assertThat(retrievedJob, notNullValue());
            assertEquals(retrievedJob.getId(), job.getId());
            assertEquals(retrievedJob.getJobType(), job.getJobType());
        }
    }

    @Test
    public void shouldCreateADefaultJobWithAPayload(){
        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        Given:{
            assert base64EncodedJson != null;
            assert base64EncodedJson.length() > 1;
            jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).data(unencodedJson).build();
        }
        When:{
            job = jobService.createJob(jobInformation);
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
        JobInformation jobInformation;
        When:{
            jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).build();
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job)jobBaseRepository.findOne(job.getId());
        }
        Then:{
            assertThat(retrievedJob, notNullValue());
            assertEquals(retrievedJob.getId(), job.getId());
            assertEquals(retrievedJob.getJobType(), job.getJobType());
            assertEquals(retrievedJob.getJobStatus().getName(), JobStatusConstants.RESPONSE_ACCEPTED);
        }
    }

    @Test
    public void shouldCreateDataJobWithMetaData(){
        HashMap<String, String> metaData = new HashMap<String, String>();
        String fountainReportId = "999";
        String companyId = "123";
        String auditComment = "This is a comment.";
        String runId = "787";
        String excelDocMongoId = "FFFFFFFF";
        DataJob job;
        DataJob retrievedJob;
        JobInformation jobInformation;
        Given:{
            jobInformation = new JobInformation.Builder(JobTargetConstants.FOUNTAIN)
                    .originator(JobTargetConstants.DCS)
                    .type(JobTypeConstants.DATA_JOB)
                    .addMetaData("fountainReportId", fountainReportId)
                    .addMetaData("companyId", companyId)
                    .addMetaData("auditComment", auditComment)
                    .addMetaData("runId", runId)
                    .addMetaData("excelDocMongoId", excelDocMongoId)
                    .build();
        }
        When:{
            job = (DataJob) jobService.createJob(jobInformation);
            retrievedJob = (DataJob) jobService.getJobByUuid(job.getUuid()).get();
        }
        Then:{
            assertEquals(retrievedJob.getFountainReportId(), fountainReportId);
            assertEquals(retrievedJob.getCompanyId(), companyId);
            assertEquals(retrievedJob.getRunId(), runId);
            assertEquals(retrievedJob.getExcelMongoDocId(), excelDocMongoId);
            assertEquals(retrievedJob.getAuditComment(), auditComment);
            assertThat(retrievedJob.getJobType().getName().equals(JobTypeConstants.DATA_JOB), is(true));
        }
    }

    @Test
    public void shouldCreateAndProcessAnUpdateJob(){
        RequestValidationJob requestJob;
        RequestValidationJob retrievedRequestJob;
        UpdateJob updateJob;
        UpdateJob retrievedUpdateJob;
        UUID targetJobUuid;
        JobStatus expectedRequestJobStatus;
        JobStatus originalRequestJobStatus;
        JobStatus originalUpdateJobStatus;
        JobStatus expectedUpdateJobStatus;
        HashMap<String, String> updateMetadata;
        String targetPlatform = JobTargetConstants.DCS;
        String originatorPlatform = JobTargetConstants.FOUNTAIN;
        UUID originatorJobUuid = UUID.randomUUID();
        Given:{
            //Create requestJob
            requestJob = jobService.createDataValidationRequest(unencodedJson);
            //Process it!
            jobService.processNextJob();
            targetJobUuid = requestJob.getUuid();
            //Make a note of it's status
            originalRequestJobStatus = jobService.getJobByUuid(requestJob.getUuid()).get().getJobStatus();
            //The status that the jobs *should* go to.
            expectedRequestJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_PROCESSING).get();
            expectedUpdateJobStatus = jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_SUCCESS).get();
            //The metadata the reques job should have.
            updateMetadata = new HashMap<String, String>(){{put("dataKey1", "dataValue1");
                put(JobStatusConstants.JOB_STATUS_KEY, JobStatusConstants.RESPONSE_PROCESSING);}};
        }
        When:{
            //Create the update job
            updateJob = jobService.createUpdateJob(targetJobUuid, targetPlatform, expectedRequestJobStatus, updateMetadata);
            originalUpdateJobStatus = updateJob.getJobStatus();
            //Process it.
            jobService.processNextJob();
            //Get the updated request job back again.
            retrievedRequestJob = (RequestValidationJob) jobService.getJobByUuid(requestJob.getUuid()).get();
            //Get the processed update job back again.
            retrievedUpdateJob = (UpdateJob) jobService.getJobByUuid(updateJob.getUuid()).get();
        }
        Then:{
            //Check the request status has changed.
            assertNotNull(retrievedRequestJob);
            assertNotNull(retrievedUpdateJob);
            assertThat(retrievedRequestJob.getJobStatus(), is(expectedRequestJobStatus));
            //Check the request job has the updated metadata.
            Map<String, String> retrievedMetadata = retrievedRequestJob.getMetadata();
            //Check that the updateJobStatus has changed.
            assertThat(retrievedUpdateJob.getJobStatus(), is(expectedUpdateJobStatus));
        }
    }


}
