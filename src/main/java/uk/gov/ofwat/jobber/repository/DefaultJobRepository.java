package uk.gov.ofwat.jobber.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ofwat.jobber.domain.jobs.DefaultJob;

@Transactional
@Repository
public interface DefaultJobRepository extends JobBaseRepository<DefaultJob>{
}
