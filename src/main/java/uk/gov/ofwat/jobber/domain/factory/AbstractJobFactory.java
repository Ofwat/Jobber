package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.service.JobInformation;

import java.util.HashMap;

public interface AbstractJobFactory {

    public Job createNewJob(JobInformation jobInformation);

}
