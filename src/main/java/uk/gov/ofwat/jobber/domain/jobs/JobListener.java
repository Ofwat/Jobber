package uk.gov.ofwat.jobber.domain.jobs;

import org.springframework.scheduling.annotation.Async;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class JobListener {

    private String uuid;

    public String getUUID(){
        return this.uuid;
    };

    public void setUUID(String uuid){
        this.uuid = uuid;
    };

    public JobListener(String uuid){
        this.uuid = uuid;
    }

    @Async
    public abstract CompletableFuture<Job> update(Job job);

}
