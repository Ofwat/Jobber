package uk.gov.ofwat.jobber.domain.jobs;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@JsonDeserialize(as = DataJob.class)
public class DataJob extends DefaultJob {

    @Column(name = "fountain_report_id")
    private String fountainReportId;

    @Column(name = "fountain_company_id")
    private String companyId;

    @Column(name = "fountain_audit_comment")
    private String auditComment;

    @Column(name = "fountain_run_id")
    private String runId;

    @Column(name = "fountain_excel_mongo_id")
    private String excelMongoDocId;

    public String getFountainReportId() {
        return fountainReportId;
    }

    public void setFountainReportId(String fountainReportId) {
        this.fountainReportId = fountainReportId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getAuditComment() {
        return auditComment;
    }

    public void setAuditComment(String auditComment) {
        this.auditComment = auditComment;
    }

    public String getRunId() {
        return runId;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getExcelMongoDocId() {
        return excelMongoDocId;
    }

    public void setExcelMongoDocId(String excelMongoDocId) {
        this.excelMongoDocId = excelMongoDocId;
    }
}
