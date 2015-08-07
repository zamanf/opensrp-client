package org.ei.opensrp.domain;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.drishti.dto.AlertStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.ei.opensrp.AllConstants.BOOLEAN_TRUE;
import static org.ei.opensrp.AllConstants.ChildRegistrationFields.HIGH_RISK_REASON;
import static org.ei.opensrp.AllConstants.ChildRegistrationFields.IS_CHILD_HIGH_RISK;
import static org.ei.opensrp.AllConstants.SPACE;

public class Child {
    private final String caseId;
    private final String motherCaseId;
    private String thayiCardNumber;
    private String dateOfBirth;
    private final String gender;
    private Map<String, String> details;
    private boolean isClosed;
    private Mother mother;
    private EligibleCouple eligibleCouple;
    private String photoPath;

    // this is the revision in the database representing this task
    private BasicDocumentRevision rev;
    public BasicDocumentRevision getDocumentRevision() {
        return rev;
    }

    public Child(String caseId, String motherCaseId, String thayiCardNumber, String dateOfBirth, String gender, Map<String, String> details) {
        this.caseId = caseId;
        this.motherCaseId = motherCaseId;
        this.thayiCardNumber = thayiCardNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.details = details;
        this.isClosed = false;
    }

    public Child(String caseId, String motherCaseId, String gender, Map<String, String> details) {
        this.caseId = caseId;
        this.motherCaseId = motherCaseId;
        this.gender = gender;
        this.details = details;
    }

    public String caseId() {
        return caseId;
    }

    public String motherCaseId() {
        return motherCaseId;
    }

    public String thayiCardNumber() {
        return thayiCardNumber;
    }

    public String dateOfBirth() {
        return dateOfBirth;
    }

    public String gender() {
        return gender;
    }

    public Mother mother() {
        return mother;
    }

    public EligibleCouple ec() {
        return eligibleCouple;
    }

    public Map<String, String> details() {
        return details;
    }

    public void setDetails(Map<String, String> details) {
        this.details = details;
    }

    public boolean isHighRisk() {
        return BOOLEAN_TRUE.equals(details.get(IS_CHILD_HIGH_RISK));
    }

    public boolean isClosed() {
        return isClosed;
    }

    public Child setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
        return this;
    }

    public String highRiskReason() {
        String highRiskReason = details.get(HIGH_RISK_REASON) == null ? "" : details.get(HIGH_RISK_REASON);
        return StringUtils.join(new HashSet<String>(Arrays.asList(highRiskReason.split(SPACE))).toArray(), SPACE);
    }

    public Child setThayiCardNumber(String thayiCardNumber) {
        this.thayiCardNumber = thayiCardNumber;
        return this;
    }

    public Child setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public String getDetail(String name) {
        return details.get(name);
    }

    public String photoPath() {
        return photoPath;
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

    public Child withMother(Mother mother) {
        this.mother = mother;
        return this;
    }

    public Child withEC(EligibleCouple eligibleCouple) {
        this.eligibleCouple = eligibleCouple;
        return this;
    }

    public Child withPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public Child withDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public static Child fromRevision(BasicDocumentRevision rev) {
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.asMap();
        if(map.containsKey("caseId") && map.containsKey("motherCaseId") && map.containsKey("thayiCardNumber") &&
                map.containsKey("dateOfBirth") && map.containsKey("gender") && map.containsKey("details")) {
            String caseId = (String) map.get("caseId");
            String motherCaseId = (String) map.get("motherCaseId");
            String thayiCardNumber = (String) map.get("thayiCardNumber");
            String dateOfBirth = (String) map.get("dateOfBirth");
            String gender = (String) map.get("gender");
            Map<String, String> details = (Map<String, String>) map.get("details");
            String isClosed = (String) map.get("isClosed");
            String photoPath = (String) map.get("photoPath");
            Child a = new Child(caseId, motherCaseId, thayiCardNumber, dateOfBirth, gender, details);
            return a;
        }
        return null;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("caseId", caseId);
        map.put("motherCaseId", motherCaseId);
        map.put("thayiCardNumber", thayiCardNumber);
        map.put("dateOfBirth", dateOfBirth);
        map.put("gender", gender);
        map.put("details", details);
        map.put("isClosed", isClosed);
        map.put("photoPath", photoPath);
        return map;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
