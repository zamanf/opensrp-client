package org.ei.opensrp.service;

import android.util.Log;

import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.OpenSRPSQLiteOpenHelper;
import org.ei.opensrp.db.adapters.SettingsRepository;
import org.ei.opensrp.db.adapters.SharedPreferencesAdapter;
import org.ei.opensrp.domain.LoginResponse;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.repository.Repository;
import org.ei.opensrp.sync.SaveANMLocationTask;
import org.ei.opensrp.sync.SaveUserInfoTask;
import org.ei.opensrp.util.Session;
import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import static org.ei.opensrp.AllConstants.*;
import static org.ei.opensrp.AllConstants.ENGLISH_LANGUAGE;
import static org.ei.opensrp.AllConstants.ENGLISH_LOCALE;
import static org.ei.opensrp.AllConstants.KANNADA_LANGUAGE;
import static org.ei.opensrp.AllConstants.KANNADA_LOCALE;
import static org.ei.opensrp.AllConstants.OPENSRP_AUTH_USER_URL_PATH;
import static org.ei.opensrp.AllConstants.OPENSRP_LOCATION_URL_PATH;
import static org.ei.opensrp.event.Event.ON_LOGOUT;

public class UserService {
    //FIXME: !!!
    private Repository repository;

    @Inject
    private SettingsRepository allSettings;

    @Inject
    private SharedPreferencesAdapter allSharedPreferences;

    @Inject
    private HTTPAgent httpAgent;

    @Inject
    private Session session;

    @Inject
    private DristhiConfiguration configuration;

    @Inject
    private SaveANMLocationTask saveANMLocationTask;

    @Inject
    private SaveUserInfoTask saveUserInfoTask;

    public UserService() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public boolean isValidLocalLogin(String userName, String password) {
        return allSharedPreferences.fetchRegisteredANM().equals(userName) && repository.canUseThisPassword(password);
    }

    public LoginResponse isValidRemoteLogin(String userName, String password) {
        String requestURL = configuration.dristhiBaseURL() + OPENSRP_AUTH_USER_URL_PATH;
        return httpAgent.urlCanBeAccessWithGivenCredentials(requestURL, userName, password);
    }

    public Response<String> getLocationInformation() {
        String requestURL = configuration.dristhiBaseURL() + OPENSRP_LOCATION_URL_PATH;
        return httpAgent.fetch(requestURL);
    }

    private void loginWith(String userName, String password) {
        setupContextForLogin(userName, password);
        allSettings.registerANM(userName, password);
    }

    public void localLogin(String userName, String password) {
        loginWith(userName, password);
    }

    public void remoteLogin(String userName, String password, String userInfo) {
        loginWith(userName, password);
        saveAnmLocation(getUserLocation(userInfo));
        saveUserInfo(getUserData(userInfo));
    }

    public String getUserData(String userInfo) {
        try {
            JSONObject userInfoJson = new JSONObject(userInfo);
            return userInfoJson.getString("user");
        } catch (JSONException e) {
            Log.v("Error : ", e.getMessage());
            return null;
        }
    }

    public String getUserLocation(String userInfo) {
        try {
            JSONObject userLocationJSON = new JSONObject(userInfo);
            return userLocationJSON.getString("locations");
        } catch (JSONException e) {
            Log.v("Error : ", e.getMessage());
            return null;
        }
    }

    public void saveAnmLocation(String anmLocation) {
        saveANMLocationTask.save(anmLocation);
    }

    public void saveUserInfo(String userInfo) { saveUserInfoTask.save(userInfo); }

    public boolean hasARegisteredUser() {
        return !allSharedPreferences.fetchRegisteredANM().equals("");
    }

    public void logout() {
        logoutSession();
        allSettings.registerANM("", "");
        allSettings.savePreviousFetchIndex("0");
        repository.deleteRepository();
    }

    public void logoutSession() {
        session().expire();
        ON_LOGOUT.notifyListeners(true);
    }

    public boolean hasSessionExpired() {
        return session().hasExpired();
    }

    protected void setupContextForLogin(String userName, String password) {
        session().start(session().lengthInMilliseconds());
        session().setPassword(password);
    }

    protected Session session() {
        return session;
    }

    public String switchLanguagePreference() {
        String preferredLocale = allSharedPreferences.fetchLanguagePreference();
        if (ENGLISH_LOCALE.equals(preferredLocale)) {
            allSharedPreferences.saveLanguagePreference(KANNADA_LOCALE);
            return KANNADA_LANGUAGE;
        } else {
            allSharedPreferences.saveLanguagePreference(ENGLISH_LOCALE);
            return ENGLISH_LANGUAGE;
        }
    }
}
