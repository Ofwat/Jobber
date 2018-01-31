package uk.gov.ofwat.jobber.domain.factory;

import uk.gov.ofwat.jobber.domain.Job;
import uk.gov.ofwat.jobber.domain.constants.JobTypeConstants;
import uk.gov.ofwat.jobber.domain.jobs.DataJob;
import uk.gov.ofwat.jobber.repository.JobTypeRepository;

import java.util.HashMap;

public class DataJobFactory implements AbstractJobFactory {

    private final String jobType;

    private final JobTypeRepository jobTypeRepository;

    public DataJobFactory(JobTypeRepository jobTypeRepository){
        this.jobType = JobTypeConstants.DATA_JOB;
        this.jobTypeRepository = jobTypeRepository;
    }

    @Override
    public Job createNewJob(HashMap<String, String> metaData) {
        DataJob dataJob = new DataJob();
        dataJob.setJobType(jobTypeRepository.findByName(JobTypeConstants.DATA_JOB).get());
        dataJob = setMetadata(metaData, dataJob);
        return dataJob;
    }

    private DataJob setMetadata(HashMap<String,String> metadata, DataJob dataJob){
        dataJob.setAuditComment(metadata.get("auditComment"));
        dataJob.setExcelMongoDocId(metadata.get("excelDocMongoId"));
        dataJob.setCompanyId(metadata.get("companyId"));
        dataJob.setFountainReportId(metadata.get("fountainReportId"));
        dataJob.setRunId(metadata.get("runId"));
        return dataJob;
    }

}