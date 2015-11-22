package org.ei.opensrp.vaccinator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;


import org.ei.opensrp.vaccinator.child.ChildSmartRegisterActivity;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.view.controller.ANMController;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class VaccinatorNavigationController extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;

    public VaccinatorNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }
    @Override
    public void startECSmartRegistry() {

      /*  activity.startActivity(new Intent(activity, HouseHoldSmartRegisterActivity.class));
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if(sharedPreferences.getBoolean("firstlauch",true)) {
            sharedPreferences.edit().putBoolean("firstlauch",false).commit();
            activity.startActivity(new Intent(activity, tutorialCircleViewFlow.class));
        }
*/
    }

    @Override
    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, ChildSmartRegisterActivity.class));

       // activity.startActivity(new Intent(activity , ChildSmartRegisterActivity.class));
    }

    @Override
    public void startFPSmartRegistry() {
        activity.startActivity(new Intent(activity, FieldMonitorSmartRegisterActivity.class));

        //  activity.startActivity(new Intent(activity, ElcoSmartRegisterActivity.class));
    }


}
