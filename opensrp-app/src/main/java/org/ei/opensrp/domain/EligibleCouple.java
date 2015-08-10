package org.ei.opensrp.domain;

import com.cloudant.sync.datastore.BasicDocumentRevision;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ei.opensrp.AllConstants.BOOLEAN_TRUE;
import static org.ei.opensrp.AllConstants.ECRegistrationFields.*;
import static org.ei.opensrp.AllConstants.SPACE;

public class EligibleCouple {
    private String caseId;
    private String wifeName;
    private String husbandName;
    private String ecNumber;
    private final String village;
    private final String subcenter;
    private Map<String, String> details;
    private Boolean isOutOfArea;
    private Boolean isClosed;
    private String photoPath;

    // this is the revision in the database representing this task
    private BasicDocumentRevision revision;
    public BasicDocumentRevision getDocumentRevision() {
        return revision;
    }

    public EligibleCouple(String caseId, String wifeName, String husbandName, String ecNumber, String village, String subcenter, Map<String, String> details) {
        this.caseId = caseId;
        this.wifeName = wifeName;
        this.husbandName = husbandName;
        this.ecNumber = ecNumber;
        this.village = village;
        this.subcenter = subcenter;
        this.details = details;
        this.isOutOfArea = false;
        this.isClosed = false;
        this.photoPath = null;
    }

    public EligibleCouple asOutOfArea() {
        this.isOutOfArea = true;
        return this;
    }

    public EligibleCouple withPhotoPath(String photoPath) {
        this.photoPath = photoPath;
        return this;
    }

    public EligibleCouple withOutOfArea(String outOfArea) {
        if (Boolean.parseBoolean(outOfArea)) {
            isOutOfArea = true;
        }
        return this;
    }

    public String wifeName() {
        return wifeName;
    }

    public String husbandName() {
        return husbandName;
    }

    public String ecNumber() {
        return ecNumber;
    }

    public String caseId() {
        return caseId;
    }

    public String village() {
        return village;
    }

    public String subCenter() {
        return subcenter;
    }

    public boolean isOutOfArea() {
        return isOutOfArea;
    }

    public boolean isHighPriority() {
        return parseDetailFieldValueToBoolean(IS_HIGH_PRIORITY);
    }

    public String highPriorityReason() {
        String highRiskReason = details.get(HIGH_PRIORITY_REASON) == null ? "" : details.get(HIGH_PRIORITY_REASON);
        return StringUtils.join(new HashSet<String>(Arrays.asList(highRiskReason.split(SPACE))).toArray(), SPACE);
    }

    public boolean isYoungestChildUnderTwo() {
        return parseDetailFieldValueToBoolean("isYoungestChildUnderTwo");
    }

    private boolean parseDetailFieldValueToBoolean(String fieldName) {
        String isHighPriority = details.get(fieldName);
        return "1".equals(isHighPriority) || BOOLEAN_TRUE.equals(isHighPriority);
    }

    public Map<String, String> details() {
        return details;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public String photoPath() {
        return photoPath;
    }

    public EligibleCouple setIsClosed(boolean isClosed) {
        this.isClosed = isClosed;
        return this;
    }

    public String getDetail(String name) {
        return details.get(name);
    }

    public String age() {
        //TODO: Calculate age from DOB
        return details.get("wifeAge");
    }

    public boolean hasFPMethod() {
        String fpMethod = getDetail(CURRENT_FP_METHOD);
        return isNotBlank(fpMethod) && !"none".equalsIgnoreCase(fpMethod);
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

    public static EligibleCouple fromRevision(BasicDocumentRevision rev) {
        // this could also be done by a fancy object mapper
        Map<String, Object> map = rev.asMap();
        if(map.containsKey("caseId") && map.containsKey("wifeName") && map.containsKey("ecNumber") &&
                map.containsKey("village") && map.containsKey("subcenter") && map.containsKey("details")) {
            String caseId = (String) map.get("caseId");
            String wifeName = (String) map.get("wifeName");
            String husbandName = (String) map.get("husbandName");
            String ecNumber = (String) map.get("ecNumber");
            String village = (String) map.get("village");
            String subcenter = (String) map.get("subcenter");
            Map<String, String> details = (Map<String, String>) map.get("details");
            EligibleCouple eligibleCouple = new EligibleCouple(caseId, wifeName, husbandName, ecNumber, village, subcenter, details);
            eligibleCouple.revision = rev;
            return eligibleCouple;
        }
        return null;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("caseId", caseId);
        map.put("wifeName", wifeName);
        map.put("husbandName", husbandName);
        map.put("ecNumber", ecNumber);
        map.put("village", village);
        map.put("subcenter", subcenter);
        map.put("details", details);
        map.put("isOutOfArea", isOutOfArea);
        map.put("isClosed", isClosed);
        map.put("photoPath", photoPath);
        return map;
    }
}
