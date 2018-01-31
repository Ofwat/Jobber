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
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.DataJob;
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
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
    public void shouldCreateDataJobWithMEtaData(){
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
}
