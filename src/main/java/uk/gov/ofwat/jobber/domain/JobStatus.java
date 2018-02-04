package uk.gov.ofwat.jobber.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.Set;

/**
 *
 */
@Entity
@Table(name = "jobber_job_status")
public class JobStatus {

    public JobStatus(String name){
        this.name = name;
    }

    public JobStatus(){

    }

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @JsonBackReference
    @OneToMany(mappedBy = "jobStatus")
    private Set<Job> jobs;

    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

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
}
