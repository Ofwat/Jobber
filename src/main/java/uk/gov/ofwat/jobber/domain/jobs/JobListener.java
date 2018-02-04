package uk.gov.ofwat.jobber.domain.jobs;

import org.springframework.scheduling.annotation.Async;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class JobListener {

    private UUID uuid;

    public UUID getUUID(){
        return this.uuid;
    };

    public void setUUID(UUID uuid){
        this.uuid = uuid;
    };

    public JobListener(UUID uuid){
        this.uuid = uuid;
    }

    @Async
    public abstract CompletableFuture<Job> update(Job job);

}
