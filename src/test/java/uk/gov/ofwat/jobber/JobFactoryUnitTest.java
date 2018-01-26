package uk.gov.ofwat.jobber;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobType;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.factory.*;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class JobFactoryUnitTest {

    @Mock
    private JobTypeRepository jobTypeRepository;

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
    }

    @Test
    public void shouldCreateAQueryJob(){
        Job job;
        AbstractJobFactory jobFactory;
        Given:{
            jobFactory = new QueryJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob();
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.QUERY_JOB_STATUS), is(true));
        }
    };

    @Test
    public void shouldCreateADataJob(){
        Job job;
        AbstractJobFactory jobFactory;
        Given:{
            jobFactory = new DataJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob();
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.DATA_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateAGetNewJob(){
        Job job;
        AbstractJobFactory jobFactory;
        Given:{
            jobFactory = new GetNewJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob();
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.GET_NEW_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateARequestValidationJob(){
        Job job;
        AbstractJobFactory jobFactory;
        Given:{
            jobFactory = new RequestValidationJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob();
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.REQUEST_VALIDATION_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateAResponseValidationJob(){
        Job job;
        AbstractJobFactory jobFactory;
        Given:{
            jobFactory = new ResponseValidationJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob();
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.RESPONSE_VALIDATION_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateAnUpdateJob(){
        Job job;
        AbstractJobFactory jobFactory;
        Given:{
            jobFactory = new UpdateJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob();
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.UPDATE_JOB), is(true));
        }
    };

    @Test
    public void shouldCreateADefaultJob(){
        Job job;
        AbstractJobFactory jobFactory;
        Given:{
            jobFactory = new DefaultJobFactory(jobTypeRepository);
        }
        When:{
            job = jobFactory.createNewJob();
        }
        Then:{
            assertThat(job.getJobType().getName().equals(JobTypeConstants.DEFAULT_JOB), is(true));
        }
    };

}
