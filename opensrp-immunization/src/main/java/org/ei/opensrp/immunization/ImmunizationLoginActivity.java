package org.ei.opensrp.immunization;

import android.content.Intent;

import org.ei.opensrp.immunization.application.ConfigSyncReceiver;
import org.ei.opensrp.view.activity.LoginActivity;
import org.json.JSONException;

public class ImmunizationLoginActivity extends LoginActivity {
    @Override
    protected int loginLayout() {
        return R.layout.login;
    }

    @Override
    protected void customTaskWithRemoteLogin(android.content.Context context, String username, String password) {
        try {
            ConfigSyncReceiver.fetchConfig(context, username, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void goToHome() {
        startActivity(new Intent(this, ImmunizationHomeActivity.class));
        finish();
    }
}
