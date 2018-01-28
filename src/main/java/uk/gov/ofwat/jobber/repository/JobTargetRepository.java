package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ofwat.jobber.domain.Target;

import java.util.Optional;

@Repository
public interface JobTargetRepository extends JpaRepository<Target, Long>{
    public Optional<Target> findByName(String name);
}
