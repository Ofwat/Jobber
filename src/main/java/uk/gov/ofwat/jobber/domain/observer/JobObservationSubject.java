package uk.gov.ofwat.jobber.domain.observer;

import java.util.ArrayList;
import java.util.List;

public interface JobObservationSubject {

    List<JobObserver> JOB_OBSERVERS = new ArrayList<JobObserver>();

    default void registerJobObserver(JobObserver jobObserver) {
        JOB_OBSERVERS.add(jobObserver);
    }

    default void registerJobObservers(List<JobObserver> jobObservers) {
        for (JobObserver jobObserver : jobObservers) {
            JOB_OBSERVERS.add(jobObserver);
        }
    }

    default void unregisterJobObserver(JobObserver jobObserver) {
        JOB_OBSERVERS.remove(jobObserver);
    }

    default void alertJobObservers() {
        for (JobObserver jobObserver : JOB_OBSERVERS) {
            jobObserver.jobUpdateOccurred(this);
        }
    }
}
