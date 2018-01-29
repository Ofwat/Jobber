package uk.gov.ofwat.jobber.domain.jobs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;

@Entity
@JsonDeserialize(as = QueryJob.class)
public class QueryJob extends DefaultJob {
}


