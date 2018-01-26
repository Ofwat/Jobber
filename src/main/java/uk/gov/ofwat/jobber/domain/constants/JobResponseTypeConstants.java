package uk.gov.ofwat.jobber.domain.constants;

public final class JobResponseTypeConstants {

    public static String RESPONSE_SUCCESS = "SUCCESS";

    public static String RESPONSE_FAILURE = "FAILED";

    public static String RESPONSE_PROCESSING = "PROCESSING";

    public static String RESPONSE_REJECTED = "REJECTED";

    public static String RESPONSE_ACCEPTED = "ACCEPTED";

    //A new Job awaiting processing.
    public static String RESPONSE_CREATED = "CREATED";

    private JobResponseTypeConstants(){}

}
