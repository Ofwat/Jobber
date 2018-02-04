package uk.gov.ofwat.jobber.service;

import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;

import java.util.HashMap;


public class JobInformation {

    private final String type;
    private final String data;
    private final String originatorPlatform;
    private final String targetPlatform;
    private final HashMap<String, String> metadata;
    //private final String targetJobUuid;
    private final String originatorJobUuid;
    //private final String targetJobNewStatus;


    private JobInformation(Builder builder){
        type = builder.type;
        data = builder.data;
        originatorPlatform = builder.originatorPlatform;
        targetPlatform = builder.targetPlatform;
        metadata = builder.metadata;
/*        targetJobUuid = builder.targetJobUuid;
        targetJobNewStatus = builder.targetJobNewStatus;*/
        originatorJobUuid = builder.originatorJobUuid;
    }

    public static class Builder{

        private String type = JobTypeConstants.DEFAULT_JOB;
        private String data = "";
        private String originatorPlatform = "";
        private final String targetPlatform;
        //private String targetJobUuid = "";
        private String originatorJobUuid = "";
        //private String targetJobNewStatus = "";

        private HashMap<String, String> metadata = new HashMap<String, String>();

        public Builder(String targetPlatform){
            this.targetPlatform = targetPlatform;
        }

        public Builder data(String data){
            this.data = data;
            return this;
        }

        public Builder originator(String originatorPlatform){
            this.originatorPlatform = originatorPlatform;
            return this;
        }

        public Builder addMetaData(String key, String value){
          this.metadata.put(key, value);
          return this;
        };

        public Builder setMetaData(HashMap<String, String> metaData){
            this.metadata = new HashMap<String, String>(metaData);
            return this;
        }

        public Builder type(String type){
            this.type = type;
            return this;
        }

/*        public Builder targetJobUuid(String targetJobUuid){
            this.targetJobUuid = targetJobUuid;
            return this;
        }*/

        public Builder originatorJobUuid(String originatorJobUuid){
            this.originatorJobUuid = originatorJobUuid;
            return this;
        }

/*        public Builder tartgetJobNewStatus(String targetJobNewStatus){
            this.targetJobNewStatus = targetJobNewStatus;
            return this;
        }*/

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

    public String getOriginatorPlatform() {
        return originatorPlatform;
    }

    public String getTargetPlatform() {
        return targetPlatform;
    }

    public HashMap<String, String> getMetadata() {
        return metadata;
    }

/*    public String getTargetJobUuid() {
        return targetJobUuid;
    }*/

    public String getOriginatorJobUuid() {
        return originatorJobUuid;
    }

/*    public String getTargetJobNewStatus() {
        return targetJobNewStatus;
    }*/
}
