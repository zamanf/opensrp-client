package org.ei.opensrp.view.controller;

import android.app.Activity;
import android.content.Intent;

import org.ei.opensrp.view.activity.ReportsActivity;
import org.ei.opensrp.view.activity.VideosActivity;

public class NavigationController {
    private Activity activity;

    public NavigationController(Activity activity) {
        this.activity = activity;
    }

    public void startReports() {
        activity.startActivity(new Intent(activity, ReportsActivity.class));
    }

    public void startVideos() {
        activity.startActivity(new Intent(activity, VideosActivity.class));
    }

    public void goBack() {
        activity.finish();
    }
}
