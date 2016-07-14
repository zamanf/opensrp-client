package org.ei.opensrp.domain;

import org.ei.opensrp.Context;
import org.ei.opensrp.view.contract.Beneficiary;

import java.util.List;

public class ReportIndicator {
    private String value;
    private String name;

    private String description;

    public ReportIndicator(String name, String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String description() {
        return description;
    }

    public String value() {
        return value;
    }

    public String name() {
        return name;
    }

    public static List<Beneficiary> fetchCaseList(String indicator, List<String> caseIds){
        if(ReportsCategory.ANC_SERVICES.inCategory(indicator)){
            return fetchMotherCaseList(caseIds);
        }
        else if (ReportsCategory.BENEFICIARY_SCHEMES.inCategory(indicator)){
            return fetchECCaseList(caseIds);
        }
        else if (ReportsCategory.CHILD_SERVICES.inCategory(indicator)){
            return fetchChildCaseList(caseIds);
        }
        else if (ReportsCategory.FPS.inCategory(indicator)){
            return fetchECCaseList(caseIds);
        }
        else if (ReportsCategory.MORTALITY.inCategory(indicator)){
            return fetchChildCaseList(caseIds);
        }
        else if (ReportsCategory.PNC_SERVICES.inCategory(indicator)){
            return fetchMotherCaseList(caseIds);
        }
        else if (ReportsCategory.PREGNANCY_OUTCOMES.inCategory(indicator)){
            return fetchMotherCaseList(caseIds);
        }
        return null;
    }

    public static void startCaseDetailActivity(android.content.Context context, String indicator, String caseId){
        throw new UnsupportedOperationException("Not implemented yet");
    }

    private static List<Beneficiary> fetchECCaseList(List<String> caseIds) {
        return Context.getInstance().beneficiaryService().fetchFromEcCaseIds(caseIds);
    }

    private static List<Beneficiary> fetchMotherCaseList(List<String> caseIds) {
        return Context.getInstance().beneficiaryService().fetchFromMotherCaseIds(caseIds);
    }

    private static List<Beneficiary> fetchChildCaseList(List<String> caseIds) {
        return Context.getInstance().beneficiaryService().fetchFromChildCaseIds(caseIds);
    }

    public static ReportIndicator parseToReportIndicator(List<ReportIndicator> indicators, String indicator) {
        for (ReportIndicator reportIndicator : indicators) {
            if (reportIndicator.value().equalsIgnoreCase(indicator))
                return reportIndicator;
        }
        throw new IllegalArgumentException("Could not find ReportIndicator for value: " + indicator);
    }
}
