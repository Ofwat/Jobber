package uk.gov.ofwat.jobber.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class JobProcessService {

    Logger log = LoggerFactory.getLogger(JobProcessService.class);

    //TODO - Can we just use the scheduled annotation instead of all the other guff?
    @Scheduled(initialDelay=10000, fixedRate=30000)
    public void testMethod(){
        log.info("In the annotated Quartz job...");
    }

}
