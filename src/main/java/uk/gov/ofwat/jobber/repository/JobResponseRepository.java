package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ofwat.jobber.domain.JobResponse;

import java.util.Optional;

@Repository
public interface JobResponseRepository extends JpaRepository<JobResponse, Long>{
    public Optional<JobResponse> findOneByName(String name);
}
