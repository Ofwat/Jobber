package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;

import java.util.HashMap;

public interface AbstractJobFactory {

    public Job createNewJob(HashMap<String, String> jobMetaData);

}
