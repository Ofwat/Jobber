package uk.gov.ofwat.jobber.domain.observer;

public interface JobObserver {

    void jobUpdateOccurred(JobObservationSubject job);

}
