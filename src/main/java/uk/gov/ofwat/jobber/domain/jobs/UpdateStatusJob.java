package uk.gov.ofwat.jobber.domain.jobs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@JsonDeserialize(as = UpdateStatusJob.class)
public class UpdateStatusJob extends DefaultJob{

    @Column(name = "target_job_uuid")
    private String targetJobUuid;

    @Column(name = "originator_job_uuid")
    private String originatorJobUuid;

    @Column(name = "new_job_status")
    private String newStatus;

    public String getTargetJobUuid() {
        return targetJobUuid;
    }

    public void setTargetJobUuid(String targetJobUuid) {
        this.targetJobUuid = targetJobUuid;
    }

    public String getOriginatorJobUuid() {
        return originatorJobUuid;
    }

    public void setOriginatorJobUuid(String originatorJobUuid) {
        this.originatorJobUuid = originatorJobUuid;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
