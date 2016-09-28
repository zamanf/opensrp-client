package org.ei.opensrp.household;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.household.report.VaccineReport;

public class HouseholdNavigationController extends org.ei.opensrp.view.controller.NavigationController {

    private Activity activity;

    public HouseholdNavigationController(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    public void startReports() {
        activity.startActivity(new Intent(activity, VaccineReport.class));
    }
}
