package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ofwat.jobber.domain.jobs.attributes.Originator;

import java.util.Optional;

@Repository
public interface JobOriginatorRepository extends JpaRepository<Originator, Long> {
    public Optional<Originator> findByName(String name);
}
