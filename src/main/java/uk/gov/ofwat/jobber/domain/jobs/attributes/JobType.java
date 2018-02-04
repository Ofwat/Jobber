package uk.gov.ofwat.jobber.domain.jobs.attributes;

import com.fasterxml.jackson.annotation.JsonBackReference;
import uk.gov.ofwat.jobber.domain.jobs.Job;

import javax.persistence.*;
import java.util.Objects;
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

    @JsonBackReference
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JobType)) return false;
        JobType jobType = (JobType) o;
        return Objects.equals(getId(), jobType.getId()) &&
                Objects.equals(getName(), jobType.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName());
    }
}
