package uk.gov.ofwat.jobber;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobStatus;
import uk.gov.ofwat.jobber.domain.JobType;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.factory.*;
import uk.gov.ofwat.jobber.domain.jobs.DataJob;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class JobFactoryUnitTest {

    @Mock
    private JobTypeRepository jobTypeRepository;

    @Mock
    private JobStatusRepository jobStatusRepository;

    private HashMap<String, String> metaData;

    @Before
    public void setupMock(){
        MockitoAnnotations.initMocks(this);
        JobType qjt = new JobType(JobTypeConstants.QUERY_JOB_STATUS);
        JobType djt = new JobType(JobTypeConstants.DATA_JOB);
        JobType gnjt = new JobType(JobTypeConstants.GET_NEW_JOB);
        JobType reqvjt = new JobType(JobTypeConstants.REQUEST_VALIDATION_JOB);
        JobType resvt = new JobType(JobTypeConstants.RESPONSE_VALIDATION_JOB);
        JobType ut = new JobType(JobTypeConstants.UPDATE_JOB);
        JobType dj = new JobType(JobTypeConstants.DEFAULT_JOB);
        Mockito.when(jobTypeRepository.findByName(JobTypeConstants.QUERY_JOB_STATUS)).thenReturn(Optional.of(qjt));
        Mockito.when(jobTypeRepository.findByName(JobTypeConstants.DATA_JOB)).thenReturn(Optional.of(djt));
        Mockito.when(jobTypeRepository.findByName(JobTypeConstants.GET_NEW_JOB)).thenReturn(Optional.of(gnjt));
        Mockito.when(jobTypeRepository.findByName(JobTypeConstants.REQUEST_VALIDATION_JOB)).thenReturn(Optional.of(reqvjt));
        Mockito.when(jobTypeRepository.findByName(JobTypeConstants.RESPONSE_VALIDATION_JOB)).thenReturn(Optional.of(resvt));
        Mockito.when(jobTypeRepository.findByName(JobTypeConstants.UPDATE_JOB)).thenReturn(Optional.of(ut));
        Mockito.when(jobTypeRepository.findByName(JobTypeConstants.DEFAULT_JOB)).thenReturn(Optional.of(dj));

        JobStatus rsjs = new JobStatus(JobStatusConstants.RESPONSE_SUCCESS);
        JobStatus rsjp = new JobStatus(JobStatusConstants.RESPONSE_PROCESSING);
        JobStatus rsja = new JobStatus(JobStatusConstants.RESPONSE_ACCEPTED);
        JobStatus rsjc = new JobStatus(JobStatusConstants.RESPONSE_CREATED);
        JobStatus rsjf = new JobStatus(JobStatusConstants.RESPONSE_FAILURE);
        JobStatus rsjr = new JobStatus(JobStatusConstants.RESPONSE_REJECTED);
        JobStatus rsju = new JobStatus(JobStatusConstants.RESPONSE_UNKNOWN);
        Mockito.when(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_PROCESSING)).thenReturn(Optional.of(rsjp));
        Mockito.when(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_SUCCESS)).thenReturn(Optional.of(rsjs));
        Mockito.when(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_REJECTED)).thenReturn(Optional.of(rsjr));
        Mockito.when(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_ACCEPTED)).thenReturn(Optional.of(rsja));
        Mockito.when(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_FAILURE)).thenReturn(Optional.of(rsjf));
        Mockito.when(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_CREATED)).thenReturn(Optional.of(rsjc));
        Mockito.when(jobStatusRepository.findOneByName(JobStatusConstants.RESPONSE_UNKNOWN)).thenReturn(Optional.of(rsju));

        //Clear metaData.
        metaData = new HashMap<String, String>();

    }

    @Test
    public void shouldCreateAQueryJob(){
        Job job;
        AbstractJobFactory jobFactory;
        JobInformation jobInformation;
        Given:{
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS).type(JobTypeConstants.QUERY_JOB_STATUS).build();
            jobFactory = new QueryJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob(jobInformation);
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.QUERY_JOB_STATUS), is(true));
        }
    };

    @Test
    public void shouldCreateADataJob(){
        DataJob job;
        AbstractJobFactory jobFactory;
        String fountainReportId = "999";
        String companyId = "123";
        String auditComment = "This is a comment.";
        String runId = "787";
        String excelDocMongoId = "FFFFFFFF";
        JobInformation jobInformation;
        Given:{
            metaData.put("fountainReportId", fountainReportId);
            metaData.put("companyId", companyId);
            metaData.put("auditComment", auditComment);
            metaData.put("runId", runId);
            metaData.put("excelDocMongoId", excelDocMongoId);
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS)
                    .setMetaData(metaData)
                    .type(JobTypeConstants.DATA_JOB)
                    .build();
            jobFactory = new DataJobFactory(jobTypeRepository);
        }
        When:{
            job = (DataJob) jobFactory.createNewJob(jobInformation);
        }
        Then:{
            assertEquals(job.getFountainReportId(), fountainReportId);
            assertEquals(job.getCompanyId(), companyId);
            assertEquals(job.getRunId(), runId);
            assertEquals(job.getExcelMongoDocId(), excelDocMongoId);
            assertEquals(job.getAuditComment(), auditComment);
            assertThat(job.getJobType().getName().equals(JobTypeConstants.DATA_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateAGetNewJob(){
        Job job;
        AbstractJobFactory jobFactory;
        JobInformation jobInformation;
        Given:{
            jobFactory = new GetNewJobFactory(jobTypeRepository);
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS).type(JobTypeConstants.GET_NEW_JOB).build();
        }
        When:{
            job = jobFactory.createNewJob(jobInformation);
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.GET_NEW_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateARequestValidationJob(){
        Job job;
        AbstractJobFactory jobFactory;
        JobInformation jobInformation;
        Given:{
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS).type(JobTypeConstants.REQUEST_VALIDATION_JOB).build();
            jobFactory = new RequestValidationJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob(jobInformation);
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.REQUEST_VALIDATION_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateAResponseValidationJob(){
        Job job;
        AbstractJobFactory jobFactory;
        JobInformation jobInformation;
        Given:{
            jobFactory = new ResponseValidationJobFactory(jobTypeRepository);
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS).type(JobTypeConstants.RESPONSE_VALIDATION_JOB).build();
        }
        When:{
            job = jobFactory.createNewJob(jobInformation);
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.RESPONSE_VALIDATION_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateAnUpdateJob(){
        Job job;
        AbstractJobFactory jobFactory;
        JobInformation jobInformation;
        Given:{
            jobFactory = new UpdateJobFactory(jobTypeRepository, jobStatusRepository);
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS).type(JobTypeConstants.UPDATE_JOB).build();
        }
        When:{
            job = jobFactory.createNewJob(jobInformation);
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.UPDATE_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateADefaultJob(){
        Job job;
        AbstractJobFactory jobFactory;
        JobInformation jobInformation;
        Given:{
            jobFactory = new DefaultJobFactory(jobTypeRepository);
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS).type(JobTypeConstants.DEFAULT_JOB).build();
        }
        When:{
            job = jobFactory.createNewJob(jobInformation);
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.DEFAULT_JOB), is(true));
        }
    };

}
