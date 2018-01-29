package uk.gov.ofwat.jobber.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.List;

@RestController
@RequestMapping("/jobber")
public class JobberResource {

    Logger log = LoggerFactory.getLogger(JobberResource.class);

    private final JobService jobService;

    public JobberResource(JobService jobService){
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        log.info("REST request to get all jobs");
        List<Job> jobs = jobService.getAllJobs();
        return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
    }

    @PostMapping("/createJob")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        log.info("REST request to create a job");
        JobInformation jobInformation = new JobInformation.Builder(job.getTarget().getName()).type(job.getJobType().getName()).build();
        Job createdJob = jobService.createJob(jobInformation);
        return new ResponseEntity<Job>(createdJob, HttpStatus.CREATED);
    }
}
