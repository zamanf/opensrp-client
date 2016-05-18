package org.ei.opensrp.steppingstones;

import android.app.Activity;

import org.ei.opensrp.view.controller.ANMController;

public class SteppingStonesNavigationController extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;

    public SteppingStonesNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }
    @Override
    public void startECSmartRegistry() {

     //  activity.startActivity(new Intent(activity, ChildrenSmartRegisterActivity.class));

    }
    @Override
    public void startFPSmartRegistry() {
      // activity.startActivity(new Intent(activity, NativeChildrenSmartRegisterActivity.class));
    }


}
