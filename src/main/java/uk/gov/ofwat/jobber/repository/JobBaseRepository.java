package uk.gov.ofwat.jobber.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.JobResponse;

import java.util.List;

@NoRepositoryBean
public interface JobBaseRepository<T extends Job> extends JpaRepository<T, Long> {

    List<Job> findDistinctJobsByJobResponse(JobResponse jobResponse);

}
