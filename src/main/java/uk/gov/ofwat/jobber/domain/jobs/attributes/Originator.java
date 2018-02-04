package uk.gov.ofwat.jobber.domain.jobs.attributes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import uk.gov.ofwat.jobber.domain.jobs.Job;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "jobber_job_originator")
public class Originator {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "originator")
    private Set<Job> jobs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }
}
