package org.ei.opensrp.household;

import android.content.Intent;

import org.ei.opensrp.vaccinator.application.ConfigSyncReceiver;
import org.ei.opensrp.view.activity.LoginActivity;
import org.json.JSONException;

public class HouseholdLoginActivity extends LoginActivity {
    @Override
    protected int loginLayout() {
        return org.ei.opensrp.vaccinator.R.layout.login;
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
        startActivity(new Intent(this, HouseholdHomeActivity.class));
        finish();
    }
}
