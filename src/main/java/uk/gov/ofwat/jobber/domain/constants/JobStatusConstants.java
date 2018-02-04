package uk.gov.ofwat.jobber.domain.constants;

public final class JobStatusConstants {


    public static String JOB_STATUS_KEY = "JOB_STATUS_KEY";

    public static String LINKED_JOB_KEY = "LINKED_JOB_KEY";

    public static String RESPONSE_UNKNOWN = "UNKNOWN";

    public static String RESPONSE_SUCCESS = "SUCCESS";

    public static String RESPONSE_FAILURE = "FAILED";

    public static String RESPONSE_TARGET_PROCESSING = "TARGET_PROCESSING";

    public static String RESPONSE_TARGET_REJECTED = "TARGET_REJECTED";

    public static String RESPONSE_TARGET_ACCEPTED = "TARGET_ACCEPTED";

    //A new Job awaiting processing.
    public static String RESPONSE_JOB_CREATED = "JOB_CREATED";

    //A Job that has a response
    public static String RESPONSE_LINKED = "LINKED";

    private JobStatusConstants(){}

}
