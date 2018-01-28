package uk.gov.ofwat.jobber.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ofwat.jobber.domain.Job;

import java.util.List;

@RestController
@RequestMapping("/jobber")
public class TestJobberResource {

    Logger log = LoggerFactory.getLogger(TestJobberResource.class);

    @PostMapping("/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        log.info("REST request to testjobber");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
