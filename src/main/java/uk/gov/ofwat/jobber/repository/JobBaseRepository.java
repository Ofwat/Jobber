package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@NoRepositoryBean
public interface JobBaseRepository<T extends Job> extends JpaRepository<T, Long> {

    List<Job> findDistinctJobsByJobStatus(JobStatus jobStatus);

    Optional<Job> findByUuid(UUID uuid);


}
