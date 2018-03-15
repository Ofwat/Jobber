package uk.gov.ofwat.jobber.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.gov.ofwat.jobber.domain.constants.JobStatusConstants;
import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.domain.jobs.attributes.JobStatus;
import uk.gov.ofwat.jobber.repository.JobStatusRepository;
import uk.gov.ofwat.jobber.service.JobInformation;
import uk.gov.ofwat.jobber.service.JobService;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/jobber")
public class JobberResource {

    Logger log = LoggerFactory.getLogger(JobberResource.class);

    private final JobService jobService;
    private final JobStatusRepository jobStatusRepository;

    public JobberResource(JobService jobService, JobStatusRepository jobStatusRepository){
        this.jobService = jobService;
        this.jobStatusRepository = jobStatusRepository;
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        log.info("REST request to get all jobs");
        List<Job> jobs = jobService.getAllJobs();
        return new ResponseEntity<List<Job>>(jobs, HttpStatus.OK);
    }

    @GetMapping("/jobs/next")
    public ResponseEntity<Job> getNextJob(@RequestParam(name = "target") String target){
        log.info("REST request to get next job with param: {}", target);
        Optional<Job> job = jobService.getNextJobForTarget(target);
        if(job.isPresent()){
            return new ResponseEntity<Job>(job.get(), HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/createJob")
    public ResponseEntity<Job> createJob(@RequestBody Job job) {
        log.info("REST request to create a job");
        String jobData = job.getJobData() == null ? "" : job.getJobData().getData();
        JobInformation jobInformation = new JobInformation.Builder(job.getTarget().getName())
                .type(job.getJobType().getName())
                .setMetaData((HashMap<String, String>) job.getMetadata())
                .data(jobData)
                .build();
        Job createdJob = jobService.createJob(jobInformation);
        return new ResponseEntity<Job>(createdJob, HttpStatus.CREATED);
    }

    @PostMapping("/job/{uuid}/status")
    public ResponseEntity updateJobStatus(@PathVariable String uuid, @RequestBody String status) {
        log.info("REST request to update a job status");
        log.info("uuid: " + uuid + "  status: " + status);

        Optional<Job> schrodingersJob = jobService.getJobByUuid(uuid);
        if(!schrodingersJob.isPresent()){
            return ResponseEntity.notFound().build();
        }

        Optional<JobStatus> schrodingersJobStatus = Optional.of(jobStatusRepository.findOneByName(status).get());
        if (!schrodingersJobStatus.isPresent()) {
            return ResponseEntity.unprocessableEntity().build();
        }

        Job job = schrodingersJob.get();
        jobService.updateJobStatus(job.getUuid(), schrodingersJobStatus.get());

        return ResponseEntity.ok().build();
    }
}
