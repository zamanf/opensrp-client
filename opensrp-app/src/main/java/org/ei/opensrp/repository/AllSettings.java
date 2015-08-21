package org.ei.opensrp.repository;

import org.ei.opensrp.repository.cloudant.ServiceProvidedModel;
import org.ei.opensrp.repository.cloudant.SettingsModel;

public class AllSettings {
    public static final String APPLIED_VILLAGE_FILTER_SETTING_KEY = "appliedVillageFilter";
    public static final String PREVIOUS_FETCH_INDEX_SETTING_KEY = "previousFetchIndex";
    public static final String PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY = "previousFormSyncIndex";
    private static final String ANM_PASSWORD_PREFERENCE_KEY = "anmPassword";
    private static final String ANM_LOCATION = "anmLocation";
    private static final String USER_INFORMATION = "userInformation";

    private AllSharedPreferences preferences;
    private SettingsRepository settingsRepository;

    SettingsModel mSettingsModel = org.ei.opensrp.Context.getInstance().settingsModel();

    public AllSettings(AllSharedPreferences preferences, SettingsRepository settingsRepository) {
        this.preferences = preferences;
        this.settingsRepository = settingsRepository;
    }

    public void registerANM(String userName, String password) {
        preferences.updateANMUserName(userName);
        mSettingsModel.updateSetting(ANM_PASSWORD_PREFERENCE_KEY, password);
    }

    public void savePreviousFetchIndex(String value) {
        mSettingsModel.updateSetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, value);
    }

    public String fetchPreviousFetchIndex() {
        return mSettingsModel.querySetting(PREVIOUS_FETCH_INDEX_SETTING_KEY, "0");
    }

    public void saveAppliedVillageFilter(String village) {
        mSettingsModel.updateSetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, village);
    }

    public String appliedVillageFilter(String defaultFilterValue) {
        return mSettingsModel.querySetting(APPLIED_VILLAGE_FILTER_SETTING_KEY, defaultFilterValue);
    }

    public String fetchANMPassword() {
        return mSettingsModel.querySetting(ANM_PASSWORD_PREFERENCE_KEY, "");
    }

    public String fetchPreviousFormSyncIndex() {
        return mSettingsModel.querySetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, "0");
    }

    public void savePreviousFormSyncIndex(String value) {
        mSettingsModel.updateSetting(PREVIOUS_FORM_SYNC_INDEX_SETTING_KEY, value);
    }

    public void saveANMLocation(String anmLocation) {
        mSettingsModel.updateSetting(ANM_LOCATION, anmLocation);
    }

    public String fetchANMLocation() {
        return mSettingsModel.querySetting(ANM_LOCATION, "");
    }

    public void saveUserInformation(String userInformation) {
        mSettingsModel.updateSetting(USER_INFORMATION, userInformation);
    }

    public String fetchUserInformation() { return mSettingsModel.querySetting(USER_INFORMATION, "");}
}
