package uk.gov.ofwat.jobber.domain.constants;

public final class JobStatusConstants {


    public static String JOB_STATUS_KEY = "JOB_STATUS_KEY";

    public static String LINKED_JOB_KEY = "LINKED_JOB_KEY";

    public static String RESPONSE_UNKNOWN = "UNKNOWN";

    public static String RESPONSE_SUCCESS = "SUCCESS";

    public static String RESPONSE_FAILURE = "FAILED";

    public static String RESPONSE_PROCESSING = "PROCESSING";

    public static String RESPONSE_REJECTED = "REJECTED";

    public static String RESPONSE_ACCEPTED = "ACCEPTED";

    //A new Job awaiting processing.
    public static String RESPONSE_CREATED = "CREATED";

    //A Job that has a response
    public static String RESPONSE_LINKED = "LINKED";

    private JobStatusConstants(){}

}
