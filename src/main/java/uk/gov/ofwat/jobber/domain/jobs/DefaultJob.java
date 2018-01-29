package uk.gov.ofwat.jobber.domain.jobs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobType;

import javax.persistence.Entity;

@Entity
@JsonDeserialize(as = DefaultJob.class)
public class DefaultJob extends Job {

}
