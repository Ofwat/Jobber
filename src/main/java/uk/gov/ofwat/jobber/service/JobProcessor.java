package uk.gov.ofwat.jobber.service;

import uk.gov.ofwat.jobber.domain.Job;

public interface JobProcessor {

    public Job ProcessJob(Job job);

    public Job GetNextJob();

}
