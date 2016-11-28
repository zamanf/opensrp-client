package com.opensrp.jilinde;

import android.app.Activity;
import android.content.Intent;


import com.opensrp.jilinde.child.CRVSChildSmartRegisterActivity;

import org.ei.opensrp.view.controller.ANMController;

public class McareNavigationController extends org.ei.opensrp.view.controller.NavigationController {
    private Activity activity;
    private ANMController anmController;

    public McareNavigationController(Activity activity, ANMController anmController) {
        super(activity,anmController);
        this.activity = activity;
        this.anmController = anmController;
    }

    public void startChildSmartRegistry() {
        activity.startActivity(new Intent(activity, CRVSChildSmartRegisterActivity.class));
    }


}
