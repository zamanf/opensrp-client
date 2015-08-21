package org.ei.opensrp.domain;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.drishti.dto.AlertStatus;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.dto.AlertStatus.complete;

public class Alert {
    private String caseID;
    private final String scheduleName;
    private String visitCode;
    private AlertStatus status;
    private String startDate;
    private String expiryDate;
    private String completionDate;

    // this is the revision in the database representing this task
    private BasicDocumentRevision revision;
    public BasicDocumentRevision getDocumentRevision() {
        return revision;
    }

    public Alert(String caseID, String scheduleName, String visitCode, AlertStatus status, String startDate, String expiryDate) {
        this.caseID = caseID;
        this.visitCode = visitCode;
        this.status = status;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.scheduleName = scheduleName;
    }

    public Alert withCompletionDate(String completionDate) {
        this.completionDate = completionDate;
        return this;
    }

    public String scheduleName() {
        return scheduleName;
    }

    public String visitCode() {
        return visitCode;
    }

    public AlertStatus status() {
        return status;
    }

    public String startDate() {
        return startDate;
    }

    public String expiryDate() {
        return expiryDate;
    }

    public String caseId() {
        return caseID;
    }

    public String completionDate() {
        return completionDate;
    }

    public boolean isComplete() {
        return complete.equals(status);
    }

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public static Alert fromRevision(BasicDocumentRevision rev) {
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.asMap();
        if(map.containsKey("caseID") && map.containsKey("visitCode") && map.containsKey("status") &&
                map.containsKey("startDate") && map.containsKey("expiryDate") && map.containsKey("scheduleName")) {
            String caseID = (String) map.get("caseID");
            String visitCode = (String) map.get("visitCode");
            String statusStr = (String) map.get("status");
            AlertStatus status = AlertStatus.from(statusStr);
            String startDate = (String) map.get("startDate");
            String expiryDate = (String) map.get("expiryDate");
            String scheduleName = (String) map.get("scheduleName");
            Alert alert = new Alert(caseID, scheduleName, visitCode, status, startDate, expiryDate);
            alert.revision = rev;
            return alert;
        }
        return null;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("caseID", caseID);
        map.put("visitCode", visitCode);
        map.put("status", status);
        map.put("startDate", startDate);
        map.put("expiryDate", expiryDate);
        map.put("scheduleName", scheduleName);
        return map;
    }

    //setters used while updating the alert
    public void setCaseID(String caseID) {
        this.caseID = caseID;
    }

    public void setVisitCode(String visitCode) {
        this.visitCode = visitCode;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCompletionDate(String completionDate) {
        this.completionDate = completionDate;
    }

    public void setRevision(BasicDocumentRevision rev) {
        this.revision = rev;
    }
}
