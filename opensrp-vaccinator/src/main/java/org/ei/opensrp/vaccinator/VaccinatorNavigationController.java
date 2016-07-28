package org.ei.opensrp.vaccinator;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.vaccinator.report.VaccineReport;

public class VaccinatorNavigationController extends org.ei.opensrp.view.controller.NavigationController {

    private Activity activity;

    public VaccinatorNavigationController(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void startReports() {
        activity.startActivity(new Intent(activity, VaccineReport.class));
    }
}
