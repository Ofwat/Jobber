package uk.gov.ofwat.jobber.config;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessJobsQuartzJob implements Job {

    private Logger logger = LoggerFactory.getLogger(ProcessJobsQuartzJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        logger.info("\n\n\n Running Job process.\n\n\n");
    }
}
