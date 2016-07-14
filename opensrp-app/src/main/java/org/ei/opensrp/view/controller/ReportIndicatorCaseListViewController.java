package org.ei.opensrp.view.controller;

import android.content.Context;

import com.google.gson.Gson;

import org.ei.opensrp.domain.ReportIndicator;
import org.ei.opensrp.view.contract.Beneficiary;
import org.ei.opensrp.view.contract.IndicatorReportCases;

import java.util.List;

public class ReportIndicatorCaseListViewController {
    private final Context context;
    private String indicator;
    private final List<String> caseIds;
    private final String month;

    public ReportIndicatorCaseListViewController(Context context, String indicator, List<String> caseIds, String month) {
        this.context = context;
        this.indicator = indicator;
        this.caseIds = caseIds;
        this.month = month;
    }

    public String get() {
        List<Beneficiary> beneficiaries = ReportIndicator.fetchCaseList(indicator, caseIds);
        return new Gson().toJson(new IndicatorReportCases(month, beneficiaries));
    }

    public void startReportIndicatorCaseDetail(String caseId) {
        ReportIndicator.startCaseDetailActivity(context, indicator, caseId);
    }
}
