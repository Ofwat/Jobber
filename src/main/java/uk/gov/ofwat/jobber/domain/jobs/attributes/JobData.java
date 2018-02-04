package uk.gov.ofwat.jobber.domain.jobs.attributes;

import uk.gov.ofwat.jobber.domain.jobs.Job;

import javax.persistence.*;

@Entity
@Table(name = "jobber_job_data")
public class JobData {

    @OneToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "job_data")
    String data;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
