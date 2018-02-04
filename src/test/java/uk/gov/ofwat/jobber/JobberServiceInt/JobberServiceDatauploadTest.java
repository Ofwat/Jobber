package uk.gov.ofwat.jobber.JobberServiceInt;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.jobs.RequestValidationJob;
import uk.gov.ofwat.jobber.domain.jobs.ResponseValidationJob;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobService;
import uk.gov.ofwat.jobber.service.JobServiceProperties;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JobberServiceDatauploadTest {

    Logger logger = LoggerFactory.getLogger(JobberServiceDatauploadTest.class);

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
    public void shouldCreateADataRequestValidationJob(){
        RequestValidationJob job;
        RequestValidationJob retrievedJob;
        String data = "";
        Given:{
            job = jobService.createDataValidationRequest(data);
        }
        When:{
             retrievedJob = (RequestValidationJob) jobBaseRepository.findByUuid(job.getUuid()).get();
        }
        Then:{
            assertEquals(retrievedJob.getJobStatus().getName(), JobStatusConstants.RESPONSE_JOB_CREATED);
        }
    }

    @Test
    public void shouldProcessADataValidationResponse(){
        ResponseValidationJob job;
        ResponseValidationJob retrievedJob;
        RequestValidationJob requestJob;
        String data = "";
        UUID originator = UUID.randomUUID();
        Given:{
            requestJob = jobService.createDataValidationRequest(data);
        }
        When:{
            job = jobService.createDataValidationResponse(data, requestJob);
            retrievedJob = (ResponseValidationJob) jobBaseRepository.findByUuid(job.getUuid()).get();
        }
        Then:{
            assertNotNull(retrievedJob);
            assertThat(retrievedJob.getTarget().getName(), is(requestJob.getOriginator().getName()));
            assertEquals(retrievedJob.getJobStatus().getName(), JobStatusConstants.RESPONSE_JOB_CREATED);
        }
    }

}
