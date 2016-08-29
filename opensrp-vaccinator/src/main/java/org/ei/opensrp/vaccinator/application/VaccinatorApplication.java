package org.ei.opensrp.vaccinator.application;

import android.content.res.Configuration;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.sync.DrishtiSyncScheduler;
import org.ei.opensrp.view.activity.DrishtiApplication;
import org.ei.opensrp.view.receiver.SyncBroadcastReceiver;

import java.util.Locale;

import static org.ei.opensrp.util.Log.logInfo;

/**
 * Created by koros on 2/3/16.
 */
public class VaccinatorApplication extends DrishtiApplication{
    private Locale locale = null;
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        DrishtiSyncScheduler.setReceiverClass(SyncBroadcastReceiver.class);

        context = Context.getInstance();
        context.updateApplicationContext(getApplicationContext());
        context.updateCommonFtsObject(createCommonFtsObject());
        applyUserLanguagePreference();
        cleanUpSyncState();
        startCESyncService(getApplicationContext());
        ConfigSyncReceiver.scheduleFirstSync(getApplicationContext());
    }

    private void cleanUpSyncState() {
        DrishtiSyncScheduler.stop(getApplicationContext());
        context.allSharedPreferences().saveIsSyncInProgress(false);
    }

    private void startCESyncService(android.content.Context context){
        CESyncReceiver.scheduleFirstSync(context);
    }

    @Override
    public void onTerminate() {
        logInfo("Application is terminating. Stopping Bidan Sync scheduler and resetting isSyncInProgress setting.");
        cleanUpSyncState();
        super.onTerminate();
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

    private String[] getFtsSearchFields(String tableName){
        if(tableName.equals("pkchild")){
            String[] ftsSearchFileds =  { "program_client_id", "epi_card_number", "first_name", "last_name", "father_name", "mother_name", "contact_phone_number" };
            return ftsSearchFileds;
        }else if(tableName.equals("pkwoman")){
            String[] ftsSearchFileds =  { "program_client_id", "epi_card_number", "first_name", "last_name", "father_name", "husband_name", "contact_phone_number" };
            return ftsSearchFileds;
        }
        return null;
    }

    private String[] getFtsSortFields(String tableName){
        if(tableName.equals("pkchild") || tableName.equals("pkwoman")) {
            String[] sortFields = {"first_name", "dob", "program_client_id"};
            return sortFields;
        }
        return null;
    }

    private String[] getFtsTables(){
        String[] ftsTables = { "pkchild", "pkwoman" };
        return ftsTables;
    }

    private CommonFtsObject createCommonFtsObject(){
        CommonFtsObject commonFtsObject = new CommonFtsObject(getFtsTables());
        for(String ftsTable: commonFtsObject.getTables()){
            commonFtsObject.updateSearchFields(ftsTable, getFtsSearchFields(ftsTable));
            commonFtsObject.updateSortFields(ftsTable, getFtsSortFields(ftsTable));
        }
        return commonFtsObject;
    }
}
