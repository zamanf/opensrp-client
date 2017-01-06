package org.ei.opensrp.path;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.path.child.ChildSmartRegisterActivity;
import org.ei.opensrp.path.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.path.report.VaccineReport;
import org.ei.opensrp.path.woman.WomanSmartRegisterActivity;

public class VaccinatorNavigationController extends org.ei.opensrp.view.controller.NavigationController {

    private Activity activity;

    public VaccinatorNavigationController(Activity activity) {
        super(activity, null);
        this.activity = activity;
    }

    @Override
    public void startReports() {
        activity.startActivity(new Intent(activity, VaccineReport.class));
    }

    public void startFieldMonitor() {
        activity.startActivity(new Intent(activity, FieldMonitorSmartRegisterActivity.class));
    }

    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, ChildSmartRegisterActivity.class));
    }

    public void startWomanSmartRegister() {
        activity.startActivity(new Intent(activity, WomanSmartRegisterActivity.class));
    }

    public void startProviderProfile() {
        activity.startActivity(new Intent(activity, ProviderProfileActivity.class));
    }
}
