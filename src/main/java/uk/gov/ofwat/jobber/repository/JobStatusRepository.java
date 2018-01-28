package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ofwat.jobber.domain.JobStatus;

import java.util.Optional;

@Repository
public interface JobStatusRepository extends JpaRepository<JobStatus, Long>{
    public Optional<JobStatus> findOneByName(String name);
}
