package org.ei.opensrp.repository;

import org.ei.opensrp.domain.Report;
import org.ei.opensrp.domain.ReportIndicator;
import org.ei.drishti.dto.Action;
import org.ei.opensrp.repository.cloudant.ReportsModel;
import org.ei.opensrp.repository.cloudant.TimelineEventsModel;

import java.util.ArrayList;
import java.util.List;

public class AllReports {
    private ReportRepository repository;

    ReportsModel mReportsModel = org.ei.opensrp.Context.getInstance().reportsModel();

    public AllReports(ReportRepository repository) {
        this.repository = repository;
    }

    public void handleAction(Action action) {
        mReportsModel.update(new Report(action.type(), action.get("annualTarget"), action.get("monthlySummaries")));
    }

    public List<Report> allFor(List<ReportIndicator> indicators) {
        List<String> indicatorList = new ArrayList<String>();
        for (ReportIndicator indicator : indicators) {
            indicatorList.add(indicator.value());
        }
        return mReportsModel.allFor(indicatorList.toArray(new String[indicatorList.size()]));
    }
}
