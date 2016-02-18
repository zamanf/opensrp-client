package org.ei.opensrp.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.db.RepositoryManager;
import org.ei.opensrp.util.Session;

/**
 * Created by koros on 2/12/16.
 */
public class SettingsRepository {
    public static final String APPLIED_VILLAGE_FILTER_SETTING_KEY = "appliedVillageFilter";
    public static final String PREVIOUS_FETCH_INDEX_SETTING_KEY = "previousFetchIndex";
    public static final String PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY = "previousFormSyncIndex";
    private static final String ANM_PASSWORD_PREFERENCE_KEY = "anmPassword";
    private static final String ANM_LOCATION = "anmLocation";
    private static final String USER_INFORMATION = "userInformation";
    private SharedPreferencesAdapter preferences;

    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_KEY_COLUMN = "key";
    public static final String SETTINGS_VALUE_COLUMN = "value";

    private Context context;
    private Session session;

    public SettingsRepository(Context context, Session session){
        this.context = context;
        this.session = session;
    }

    public void updateSetting(String key, String value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public void updateBLOB(String key, byte[] value) {
        ContentValues values = new ContentValues();
        values.put(SETTINGS_KEY_COLUMN, key);
        values.put(SETTINGS_VALUE_COLUMN, value);

        replace(values);
    }

    public String querySetting(String key, String defaultValue) {
        Cursor cursor = getCursor(key);
        if (cursor == null) {
            return defaultValue;
        }

        String value = cursor.getString(0);
        cursor.close();
        return value;
    }

    public byte[] queryBLOB(String key) {
        Cursor cursor = getCursor(key);
        if (cursor == null) {
            return null;
        }

        byte[] value = cursor.getBlob(0);
        cursor.close();
        return value;
    }

    private void replace(ContentValues values) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        database.replace(SETTINGS_TABLE_NAME, null, values);
    }

    private Cursor getCursor(String key) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        Cursor cursor = database.query(SETTINGS_TABLE_NAME, new String[]{SETTINGS_VALUE_COLUMN}, SETTINGS_KEY_COLUMN + " = ?", new String[]{key}, null, null, null, "1");
        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    public void registerANM(String userName, String password) {
        preferences.updateANMUserName(userName);
        updateSetting(ANM_PASSWORD_PREFERENCE_KEY, password);
    }

    public void savePreviousFetchIndex(String value) {
        updateSetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, value);
    }

    public String fetchPreviousFetchIndex() {
        return querySetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, "0");
    }

    public void saveAppliedVillageFilter(String village) {
        updateSetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, village);
    }

    public String appliedVillageFilter(String defaultFilterValue) {
        return querySetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, defaultFilterValue);
    }

    public String fetchANMPassword() {
        return querySetting(ANM_PASSWORD_PREFERENCE_KEY, "");
    }

    public String fetchPreviousFormSyncIndex() {
        return querySetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, "0");
    }

    public void savePreviousFormSyncIndex(String value) {
        updateSetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, value);
    }

    public void saveANMLocation(String anmLocation) {
        updateSetting(ANM_LOCATION, anmLocation);
    }

    public String fetchANMLocation() {
        return querySetting(ANM_LOCATION, "");
    }

    public void saveUserInformation(String userInformation) {
        updateSetting(USER_INFORMATION, userInformation);
    }

    public String fetchUserInformation() { return querySetting(USER_INFORMATION, "");}
}
