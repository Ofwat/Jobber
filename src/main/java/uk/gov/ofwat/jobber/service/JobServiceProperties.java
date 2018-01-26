package uk.gov.ofwat.jobber.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties
//@ConfigurationProperties("jobService")
public class JobServiceProperties {
    /**
     * A message for the service.
     */
    private String defaultTarget;

    private String defaultOriginator;

    public String getDefaultTarget() {
        return defaultTarget;
    }

    public void setDefaultTarget(String defaultTarget) {
        this.defaultTarget = defaultTarget;
    }

    public String getDefaultOriginator() {
        return defaultOriginator;
    }

    public void setDefaultOriginator(String defaultOriginator) {
        this.defaultOriginator = defaultOriginator;
    }
}
