package uk.gov.ofwat.jobber.JobberServiceInt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTargetConstants;
import uk.gov.ofwat.jobber.repository.JobBaseRepository;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class JobMetaDataIntTest {

    private Logger logger = LoggerFactory.getLogger(JobMetaDataIntTest.class);

    @Autowired
    private JobBaseRepository jobBaseRepository;

    @Autowired
    private JobTypeRepository jobTypeRepository;

    @Autowired
    private JobService jobService;

    @Test
    public void shouldStoreMetaData(){
        Job job;
        Job retrievedJob;
        JobInformation jobInformation;
        HashMap<String, String> metadata;
        Given:{
            metadata = new HashMap<String,String>(){{
                put("key1", "val1");
                put("key2", "val2");
                };
            };
            jobInformation = new JobInformation.Builder(JobTargetConstants.DCS)
                    .setMetaData(metadata)
                    .build();
        }
        When:{
            job = jobService.createJob(jobInformation);
            retrievedJob = (Job)jobBaseRepository.findByUuid(job.getUuid()).get();
        }
        Then:{
            Map<String, String> retrievedMeta = retrievedJob.getMetadata();
            assertNotNull(retrievedMeta);
            assertThat(retrievedMeta.size(),is(2));
            assertThat(retrievedMeta.get("key1"), is("val1"));
            assertThat(retrievedMeta.get("key2"), is("val2"));
        }
    }

}
