package uk.gov.ofwat.jobber.domain;

import uk.gov.ofwat.jobber.domain.jobs.AbstractJobAuditingEntity;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "jobber_job")
public abstract class Job extends AbstractJobAuditingEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "retry_count")
    private Long retryCount;

    @Column(name = "uuid")
    private UUID uuid;

    @ManyToOne
    private JobType jobType;

    @OneToOne
    @JoinColumn(name = "job_data_id")
    private JobData jobData;

    @ManyToOne
    private Originator originator;

    @ManyToOne
    private Target target;

    @ManyToOne
    private JobStatus jobStatus;

    public Long getId() {
        return id;
    };

    public void setId(Long id) {
        this.id = id;
    };

    public UUID getUuid(){
        return this.uuid;
    };

    public UUID setUuid(UUID uuid){
        this.uuid = uuid;
        return uuid;
    };

    public void setJobType(JobType jobType){
        this.jobType = jobType;
    };

    public JobType getJobType(){
        return this.jobType;
    };

    public JobData getJobData(){
        return this.jobData;
    };

    public JobData setJobData(JobData jobData){
        this.jobData = jobData;
        return jobData;
    };

    public Originator getOriginator(){
        return this.originator;
    };

    public Originator setOriginator(Originator originator){
        this.originator = originator;
        return originator;
    };

    public Target getTarget(){
        return this.target;
    };

    public Target setTarget(Target target){
        this.target = target;
        return target;
    };

    public Long getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Long retryCount) {
        this.retryCount = retryCount;
    }

    public JobStatus getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(JobStatus jobStatus) {
        this.jobStatus = jobStatus;
    }
}
