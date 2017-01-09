package util.sid.org.myapp;

//import org.ei.opensrp.view.controller.ANMController;
//import org.ei.opensrp.view.controller.NavigationController;

/**
 * Created by wildan on 1/8/17.
 */
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import org.ei.opensrp.view.controller.ANMController;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class NavigationControllerINA extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;

    public NavigationControllerINA(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }
    @Override
    public void startECSmartRegistry() {
        activity.startActivity(new Intent(activity, NativeKISmartRegisterActivity.class));
        SharedPreferences sharedPreferences = getDefaultSharedPreferences(this.activity);

        if(sharedPreferences.getBoolean("firstlauch",true)) {
            sharedPreferences.edit().putBoolean("firstlauch",false).commit();
        }

    }
//    @Override
//    public void startFPSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativeKBSmartRegisterActivity.class));
//    }
//    @Override
//    public void startANCSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativeKIANCSmartRegisterActivity.class));
//    }
//    @Override
//    public void startPNCSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativeKIPNCSmartRegisterActivity.class));
//    }
//    @Override
//    public void startChildSmartRegistry() {
//        activity.startActivity(new Intent(activity, NativeKIAnakSmartRegisterActivity.class));
//    }

}
