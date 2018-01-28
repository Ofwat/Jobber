package uk.gov.ofwat.jobber.domain.jobs;

import uk.gov.ofwat.jobber.domain.JobStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class UpdateJob extends DefaultJob{

    @Column(name = "target_job_uuid")
    private UUID targetJobUuid;

    @Column(name = "originator_job_uuid")
    private UUID originatorJobUuid;

    @Column(name = "new_job_status")
    private String newStatus;

    public UUID getTargetJobUuid() {
        return targetJobUuid;
    }

    public void setTargetJobUuid(UUID targetJobUuid) {
        this.targetJobUuid = targetJobUuid;
    }

    public UUID getOriginatorJobUuid() {
        return originatorJobUuid;
    }

    public void setOriginatorJobUuid(UUID originatorJobUuid) {
        this.originatorJobUuid = originatorJobUuid;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }
}
