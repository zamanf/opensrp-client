package org.ei.opensrp.immunization;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.immunization.report.VaccineReport;

public class ImmunizationNavigationController extends org.ei.opensrp.view.controller.NavigationController {

    private Activity activity;

    public ImmunizationNavigationController(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void startReports() {
        activity.startActivity(new Intent(activity, VaccineReport.class));
    }
}
