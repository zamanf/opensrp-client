package org.ei.opensrp.view.controller;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.util.Log;

public class HomeController {
    private UpdateController updateController;

    public HomeController(UpdateController updateController) {
        OpenSRPApplication.getInstance().inject(this);
        this.updateController = updateController;
    }

    public void pageHasFinishedLoading() {
        updateController.pageHasFinishedLoading();
    }

    public void log(String text) {
        Log.logInfo(text);
    }
}

