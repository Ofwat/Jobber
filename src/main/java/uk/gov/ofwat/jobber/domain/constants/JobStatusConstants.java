package uk.gov.ofwat.jobber.domain.constants;

public final class JobStatusConstants {


    public static String RESPONSE_UNKNOWN = "UNKNOWN"; //We don't know what happened - shouldn't see this!

    public static String RESPONSE_SUCCESS = "SUCCESS"; //The job has been completed to it's maximum extent.

    public static String RESPONSE_FAILURE = "FAILED"; //The job could not be completed. See the metadata 'error' field for more information

    public static String RESPONSE_TARGET_PROCESSING = "TARGET_PROCESSING"; //The job is being processed by the target.

    public static String RESPONSE_TARGET_REJECTED = "TARGET_REJECTED"; //The job has been rejected by the target see the metadata 'rejectedReason' key for more information.

    public static String RESPONSE_TARGET_ACCEPTED = "TARGET_ACCEPTED"; //The job has been passed to the target system or the Gofer has it.

    public static String RESPONSE_PENDING_ACTION = "PENDING_ACTION"; //The job is waiting for an external service to do something with it, e.g gofer send to Fountain or DCS/Fountain to process the data

    //A new Job awaiting processing.
    public static String RESPONSE_JOB_CREATED = "JOB_CREATED"; //The job has been created locally and is waiting to be processed.

    //A Job that has a response
    public static String RESPONSE_LINKED = "LINKED"; //The job has been updated and there is some updated data on the job whose UUID is in the metadata LINKED_JOB_KEY

    private JobStatusConstants(){}

}
