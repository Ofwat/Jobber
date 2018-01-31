package uk.gov.ofwat.jobber.service;

import org.springframework.stereotype.Component;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;

import java.util.HashMap;


public class JobInformation {

    private final String type;
    private final String data;
    private final String originator;
    private final String target;
    private final HashMap<String, String> metadata;

    private JobInformation(Builder builder){
        type = builder.type;
        data = builder.data;
        originator = builder.originator;
        target = builder.target;
        metadata = builder.metadata;
    }

    public static class Builder{

        private String type = JobTypeConstants.DEFAULT_JOB;
        private String data = "";
        private String originator = "";
        private final String target;
        private HashMap<String, String> metadata = new HashMap<String, String>();

        public Builder(String target){
            this.target = target;
        }

        public Builder data(String data){
            this.data = data;
            return this;
        }

        public Builder originator(String originator){
            this.originator = originator;
            return this;
        }

        public Builder addMetaData(String key, String value){
          this.metadata.put(key, value);
          return this;
        };

        public Builder type(String type){
            this.type = type;
            return this;
        }

        public JobInformation build(){
            return new JobInformation(this);
        }
    }

    public String getType() {
        return type;
    }

    public String getData() {
        return data;
    }

    public String getOriginator() {
        return originator;
    }

    public String getTarget() {
        return target;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }
}
