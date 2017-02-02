package org.ei.opensrp.path.sync;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.path.db.Client;
import org.ei.opensrp.path.db.ECSQLiteHelper;
import org.ei.opensrp.path.db.Event;
import org.ei.opensrp.path.db.FilterType;
import org.ei.opensrp.path.db.Query;
import org.ei.opensrp.service.HTTPAgent;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import util.Utils;

public class ECSyncUpdater {
    public static final String CLIENT_SEARCH_URL = "/rest/client";
    public static final String EVENT_SEARCH_URL = "/rest/event";

    public static final String LAST_SYNC_DATETIME_CLIENT = "LAST_SYNC_DATETIME_CLIENT";
    public static final String LAST_SYNC_DATETIME_EVENT = "LAST_SYNC_DATETIME_EVENT";

    private ECSQLiteHelper db;
    private Context context;

    private static ECSyncUpdater instance;

    public static ECSyncUpdater getInstance(Context context) {
        if (instance == null) {
            instance = new ECSyncUpdater(context);
        }
        return instance;
    }

    public ECSyncUpdater(Context context) {
        this.context = context;
        db = new ECSQLiteHelper(context);
    }

    private JSONArray fetchAsJson(String lastSyncProperty, String serviceUrl, String filter, String filterValue) throws JSONException, UnsupportedEncodingException, Exception {
        HTTPAgent httpAgent = org.ei.opensrp.Context.getInstance().getHttpAgent();
        String baseUrl = org.ei.opensrp.Context.getInstance().configuration().dristhiBaseURL();
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
        }

        Long lastSyncDatetime = Long.parseLong(Utils.getPreference(context, lastSyncProperty, "0"));
        Log.i(ECSyncUpdater.class.getName(), "LAST SYNC DT :" + new DateTime(lastSyncDatetime));

        Query tq = new Query(FilterType.AND).between("lastEdited", new DateTime(lastSyncDatetime), DateTime.now());
        if(StringUtils.isNotBlank(filter) && StringUtils.isNotBlank(filterValue)) {
            tq.eq(filter, filterValue);
        }

        String url = baseUrl + serviceUrl + "?q=" + URLEncoder.encode(tq.query(), "UTF-8");
        Log.i(ECSyncUpdater.class.getName(), "URL: " + url);

        if (httpAgent == null) {
            throw new Exception(serviceUrl + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            throw new Exception(serviceUrl + " not returned data");
        }

        JSONArray jarr = new JSONArray((String) resp.payload());
        Log.i(ECSyncUpdater.class.getName(), "FETCHED" + jarr.toString());
        return jarr;
    }

    public void fetchAllClients() {
        try {

            JSONArray jarr = fetchAsJson(LAST_SYNC_DATETIME_CLIENT, CLIENT_SEARCH_URL, null, null);
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jo = jarr.getJSONObject(i);

                Client c = Utils.getLongDateAwareGson().fromJson(jo.toString(), Client.class);
                db.insert(c);
            }

            if(jarr.length() > 0) {
                Utils.writePreference(context, LAST_SYNC_DATETIME_CLIENT, System.currentTimeMillis() + "");
            }
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public void fetchAllEvents(String filterName, String filterValue) {
        try {
            JSONArray jarr = fetchAsJson(LAST_SYNC_DATETIME_EVENT, EVENT_SEARCH_URL, filterName, filterValue);
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject jo = jarr.getJSONObject(i);

                Event e = Utils.getLongDateAwareGson().fromJson(jo.toString(), Event.class);
                db.insert(e);
            }

            if(jarr.length() > 0) {
                Utils.writePreference(context, LAST_SYNC_DATETIME_EVENT, System.currentTimeMillis() + "");
            }
        }catch (Exception e){
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    public List<JSONObject> allEvents() {
        try {
            return db.getEvents(0);
        }catch (Exception e){
            Log.e(getClass().getName(), "Exception", e);
        }
        return new ArrayList<>();
    }
}