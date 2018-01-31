package uk.gov.ofwat.jobber.domain;

import java.util.UUID;

public interface JobListener {

    public UUID getUUID();

    public void setUUID(UUID uuid);

    public void update(Job job);

}
