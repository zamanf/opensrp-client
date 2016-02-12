package org.ei.opensrp.application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import org.acra.ACRA;
import org.ei.opensrp.Context;
import org.ei.opensrp.sync.DrishtiSyncScheduler;

import java.util.Locale;

import static org.ei.opensrp.util.Log.logInfo;

/**
 * Created by koros on 2/12/16.
 */
public class OpenSRPApplication extends Application{

    protected Locale locale = null;
    protected Context context;

    private static final String TAG = "OpenSRPApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        applyUserLanguagePreference();
        cleanUpSyncState();
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        logInfo("Application is terminating. Stopping Dristhi Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
    }

    private void applyUserLanguagePreference() {
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = context.allSharedPreferences().fetchLanguagePreference();
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            locale = new Locale(lang);
            updateConfiguration(config);
        }
    }

    private void updateConfiguration(Configuration config) {
        config.locale = locale;
        Locale.setDefault(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    public void logoutCurrentUser(){
        Log.e(TAG, "Child classes should implement this function");
    }

}
