package org.ei.opensrp.indonesia.service;

import android.util.Log;

import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.domain.LoginResponse;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.indonesia.AllConstantsINA;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.repository.Repository;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.service.UserService;
import org.ei.opensrp.sync.SaveANMLocationTask;
import org.ei.opensrp.sync.SaveUserInfoTask;
import org.ei.opensrp.util.Session;
import org.json.JSONException;
import org.json.JSONObject;

import static org.ei.opensrp.AllConstants.ENGLISH_LANGUAGE;
import static org.ei.opensrp.AllConstants.ENGLISH_LOCALE;
import static org.ei.opensrp.AllConstants.KANNADA_LANGUAGE;
import static org.ei.opensrp.AllConstants.KANNADA_LOCALE;
import static org.ei.opensrp.AllConstants.OPENSRP_AUTH_USER_URL_PATH;
import static org.ei.opensrp.AllConstants.OPENSRP_LOCATION_URL_PATH;
import static org.ei.opensrp.event.Event.ON_LOGOUT;

public class BidanUserService extends UserService{

    public BidanUserService(Repository repository, AllSettings allSettings, AllSharedPreferences allSharedPreferences, HTTPAgent httpAgent, Session session, DristhiConfiguration configuration, SaveANMLocationTask saveANMLocationTask, SaveUserInfoTask saveUserInfoTask) {
        super(repository, allSettings, allSharedPreferences, httpAgent, session, configuration, saveANMLocationTask, saveUserInfoTask);
    }

    public void remoteLogin(String userName, String password, String userInfo) {
        super.remoteLogin(userName,password,userInfo);
        allSharedPreferences.setPreference(AllConstantsINA.LoginResponse.LOCATION_ANMIDS,getlocationAnmids(userInfo));
        //saveUserInfo(, );

    }

    private String getlocationAnmids(String userInfo) {
        try {
            JSONObject userLocationAnmidsJSON = new JSONObject(userInfo);

            if(!userLocationAnmidsJSON.has(AllConstantsINA.LoginResponse.LOCATION_ANMIDS)){
                return "";
            }

            return userLocationAnmidsJSON.getString(AllConstantsINA.LoginResponse.LOCATION_ANMIDS);
        } catch (JSONException e) {
            Log.v("Error : ", e.getMessage());
            return null;
        }
    }
}
