package org.ei.opensrp.mcare.application;

import android.content.Intent;

import org.ei.opensrp.mcare.LoginActivity;
import org.ei.opensrp.view.activity.DrishtiApplication;

/**
 * Created by koros on 1/22/16.
 */
public class McareApplication extends DrishtiApplication {

    public static final String TAG = "McareApplication";
    private static McareApplication instance;

    public static McareApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void logoutCurrentUser(){
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
        context.userService().logoutSession();
    }

}
