package uk.gov.ofwat.jobber.domain.jobs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Entity;

@Entity
@JsonDeserialize(as = DefaultJob.class)
public class DefaultJob extends Job {

}
