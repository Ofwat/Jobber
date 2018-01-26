package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ofwat.jobber.domain.Originator;

import java.util.Optional;

@Repository
public interface OriginatorRepository extends JpaRepository<Originator, Long> {
    public Optional<Originator> findByName(String name);
}
