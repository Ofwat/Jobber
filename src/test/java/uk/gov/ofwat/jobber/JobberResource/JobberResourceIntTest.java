package uk.gov.ofwat.jobber.JobberResource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.TypeRef;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ofwat.jobber.Application;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobStatus;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTargetConstants;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.factory.AbstractJobFactory;
import uk.gov.ofwat.jobber.domain.factory.UpdateJobFactory;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.repository.JobTargetRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;
import uk.gov.ofwat.jobber.service.JobServiceProperties;
import uk.gov.ofwat.jobber.web.rest.JobberResource;
import uk.gov.ofwat.jobber.web.rest.errors.JobberExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class JobberResourceIntTest {

    Logger log = LoggerFactory.getLogger(JobberResourceIntTest.class);

    @Autowired
    JobService jobService;

    @Autowired
    JobBaseRepository jobBaseRepository;

    @Autowired
    JobTypeRepository jobTypeRepository;

    private MockMvc restJobberMockMvc;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private JobberExceptionTranslator jobberExceptionTranslator;

    @Autowired
    JobTargetRepository jobTargetRepository;

    @Autowired
    JobStatusRepository jobStatusRepository;

    @Autowired
    JobServiceProperties jobServiceProperties;

    @Autowired
    private EntityManager em;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        JobberResource jobberResource = new JobberResource(jobService);
        this.restJobberMockMvc = MockMvcBuilders.standaloneSetup(jobberResource)
                .setCustomArgumentResolvers(pageableArgumentResolver)
                .setControllerAdvice(jobberExceptionTranslator)
                .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
/*    public static Company createEntity(EntityManager em) {
        Company company = new Company()
                .name(DEFAULT_NAME);
        company.setDeleted(DEFAULT_DELETED);
        return company;
    }*/

    @Before
    public void initTest() {
        //company = createEntity(em);
    }

    @Test
    @Transactional
    public void listJobs() throws Exception {

        Job job1 = createUnprocessedJob();
        Job job2 = createUnprocessedJob();
        Job job3 = createUnprocessedJob();
        Job job4 = createUnprocessedJob();
        Job job5 = createUpdateJob();

        MvcResult result = restJobberMockMvc.perform(get("/jobber/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$", Matchers.hasSize(5)))
                .andReturn();
                //.andExpect(jsonPath("$.[*].id").value(hasItem(job1.getId().intValue())));
                //.andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                //.andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));;
        log.info(result.getResponse().getContentAsString());
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        List<Job> jobs = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Job>>(){});
        log.info(String.valueOf(jobs.size()));
    }

    @Test
    @Transactional
    public void createJob() throws Exception {
        int databaseSizeBeforeCreate = jobBaseRepository.findAll().size();

        AbstractJobFactory jobFactory = new UpdateJobFactory(jobTypeRepository);
        Job job = jobFactory.createNewJob();
        job.setTarget(jobTargetRepository.findByName(JobTargetConstants.DCS).get());
        job.setNickname("TEST_JOB");

        // Create the Job
        restJobberMockMvc.perform(post("/jobber/createJob")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(job)))
                .andExpect(status().isCreated());

        // Validate the Company in the database
/*
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(databaseSizeBeforeCreate + 1);
        Company testCompany = companyList.get(companyList.size() - 1);
        assertThat(testCompany.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompany.getDeleted()).isEqualTo(DEFAULT_DELETED);
*/
        List<Job> jobs = jobService.getAllJobs();
        assertThat(jobs.size()).isEqualTo(1);
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
        JobInformation jobInformation = new JobInformation.Builder(jobServiceProperties.getDefaultTarget()).type(JobTypeConstants.UPDATE_JOB).build();
        Job updateJob = jobService.createJob(jobInformation);
        return updateJob;
    };


}
