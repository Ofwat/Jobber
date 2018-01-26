package uk.gov.ofwat.jobber.domain;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "jobber_job_type")
public class JobType {

    public JobType(){}

    public JobType(String jobTypeName){
        this.name = jobTypeName;
    }

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "jobType")
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
