package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ofwat.jobber.domain.jobs.attributes.JobData;

public interface JobDataRepository extends JpaRepository<JobData, Long>{
}
