package uk.gov.ofwat.jobber.domain.jobs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.gov.ofwat.jobber.domain.constants.UtilConstants;
import uk.gov.ofwat.jobber.domain.jobs.attributes.*;
import uk.gov.ofwat.jobber.domain.observer.JobObservationSubject;

import javax.persistence.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
        @JsonSubTypes.Type(value = UpdateStatusJob.class, name = "UpdateStatusJob.KIND"),
        @JsonSubTypes.Type(value = DefaultJob.class, name = "DefaultJob.KIND"),
        @JsonSubTypes.Type(value = DataJob.class, name = "DataJob.KIND"),
        @JsonSubTypes.Type(value = QueryJob.class, name = "QueryJob.KIND"),
        @JsonSubTypes.Type(value = DataResponseJob.class, name = "DataResponseJob.KIND"),
        @JsonSubTypes.Type(value = RequestValidationJob.class, name = "RequestValidation.KIND"),
        @JsonSubTypes.Type(value = ResponseValidationJob.class, name = "ResponseValidation.KIND"),
        @JsonSubTypes.Type(value = GetNewJob.class, name = "GetNew.KIND")
})
public abstract class Job extends AbstractJobAuditingEntity implements JobObservationSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "retry_count")
    private Long retryCount = 0L;

    @Column(name = "uuid")
    private String uuid;

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

    @ElementCollection(targetClass=java.lang.String.class)
    @JoinTable(name="jobber_job_metadata",
            joinColumns=@JoinColumn(name="job_id"))
    @MapKeyColumn(name="metadata_key")
    @Column(name="metadata")
    private Map<String,String> metadata;

    public Long getId() {
        return id;
    };

    public void setId(Long id) {
        this.id = id;
    };

    public String getUuid(){
        return this.uuid;
    };

    public String setUuid(String uuid){
        this.uuid = uuid;
        return uuid;
    };

    public Map<String,String> getMetadata() {
        return metadata;
    }
    public void setMetadata(Map<String,String> metadata) {
        this.metadata = metadata;
    }

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

    @JsonIgnore
    public Optional<String> getLinkedJobUuid(){
        Optional<String> optionalUuid = Optional.empty();
        for(Map.Entry<String, String> entry : metadata.entrySet()) {
            if(entry.getKey().equals(UtilConstants.LINKED_JOB_KEY)){
                optionalUuid = (Optional<String>) Optional.of(entry.getValue());
            }
        };
        return optionalUuid;
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
