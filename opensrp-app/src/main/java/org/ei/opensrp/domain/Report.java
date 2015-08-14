package org.ei.opensrp.domain;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.ei.drishti.dto.MonthSummaryDatum;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.domain.ReportIndicator.parseToReportIndicator;

public class Report implements Serializable {
    private final String indicator;
    private final String annualTarget;
    private final String monthlySummaries;

    private BasicDocumentRevision revision;
    public BasicDocumentRevision getDocumentRevision() {
        return revision;
    }

    public Report(String indicator, String annualTarget, String monthlySummaries) {
        this.indicator = indicator;
        this.annualTarget = annualTarget;
        this.monthlySummaries = monthlySummaries;
    }

    public String indicator() {
        return indicator;
    }

    public ReportIndicator reportIndicator() {
        return parseToReportIndicator(indicator);
    }

    public String annualTarget() {
        return annualTarget;
    }

    public String monthlySummariesJSON() {
        return monthlySummaries;
    }

    public List<MonthSummaryDatum> monthlySummaries() {
        return new Gson().fromJson(monthlySummaries, new TypeToken<List<MonthSummaryDatum>>() {
        }.getType());
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

    public static Report fromRevision(BasicDocumentRevision revision) {
        // this could also be done by a fancy object mapper
        Map<String, Object> map = revision.asMap();
        if(map.containsKey("indicator") && map.containsKey("annualTarget") && map.containsKey("monthlySummaries")) {
            String indicator = (String) map.get("indicator");
            String annualTarget = (String) map.get("annualTarget");
            String monthlySummaries = (String) map.get("monthlySummaries");
            Report report = new Report(indicator, annualTarget, monthlySummaries);
            report.revision = revision;
            return report;
        }
        return null;
    }

    public Map<String, Object> asMap() {
        // this could also be done by a fancy object mapper
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("indicator", indicator);
        map.put("annualTarget", annualTarget);
        map.put("monthlySummaries", monthlySummaries);
        return map;
    }
}
