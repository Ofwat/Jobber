package uk.gov.ofwat.jobber.domain;

import org.springframework.stereotype.Component;
import uk.gov.ofwat.jobber.domain.jobs.Job;

import java.util.List;

/**
 * Class to monitor the number of running jobs.
 */
@Component
public class JobMonitor {

    private Integer runningJobCount;

    private List<Job> runningJobs;

}
