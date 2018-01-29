package uk.gov.ofwat.jobber.domain;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.gov.ofwat.jobber.domain.jobs.*;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "jobber_job")
@JsonDeserialize(as = DefaultJob.class)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "kind"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UpdateJob.class, name = "UpdateJob.KIND"),
        @JsonSubTypes.Type(value = DefaultJob.class, name = "DefaultJob.KIND"),
        @JsonSubTypes.Type(value = DataJob.class, name = "DataJob.KIND"),
        @JsonSubTypes.Type(value = QueryJob.class, name = "QueryJob.KIND"),
        @JsonSubTypes.Type(value = RequestValidationJob.class, name = "RequestValidation.KIND"),
        @JsonSubTypes.Type(value = ResponseValidationJob.class, name = "ResponseValidation.KIND"),
        @JsonSubTypes.Type(value = GetNewJob.class, name = "GetNew.KIND")
})
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

    @Column(name = "nickname")
    private String nickname;

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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Job)) return false;
        Job job = (Job) o;
        return Objects.equals(getId(), job.getId()) &&
                Objects.equals(getRetryCount(), job.getRetryCount()) &&
                Objects.equals(getUuid(), job.getUuid()) &&
                Objects.equals(getJobType(), job.getJobType());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getRetryCount(), getUuid(), getJobType());
    }
}
