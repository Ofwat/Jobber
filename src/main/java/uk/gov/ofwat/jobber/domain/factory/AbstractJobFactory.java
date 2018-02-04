package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.jobs.Job;
import uk.gov.ofwat.jobber.service.JobInformation;

public interface AbstractJobFactory {

    public Job createNewJob(JobInformation jobInformation);

}
