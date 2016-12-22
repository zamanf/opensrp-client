package org.ei.opensrp.dghs.healthIDtasks;

import android.content.ContentValues;
import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.DristhiConfiguration;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.service.HTTPAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.AsyncTask;

import static java.text.MessageFormat.format;
import static org.ei.opensrp.domain.FetchStatus.nothingFetched;
import static org.ei.opensrp.util.Log.logError;

/**
 * Created by raihan on 11/3/16.
 */
public class health_ID_task extends AsyncTask {
    private AllSharedPreferences allSharedPreferences;
    private DristhiConfiguration configuration;
    public static final String client_list = "client-healthid-list";
    private  HTTPAgent httpAgent;
    private AllSettings allSettings;
    public Context context;

    public health_ID_task(Context context, HTTPAgent httpAgent, DristhiConfiguration configuration, AllSettings allSettings, AllSharedPreferences allSharedPreferences) {
        this.httpAgent = httpAgent;
        this.configuration = configuration;
        this.allSettings = allSettings;
        this.allSharedPreferences = allSharedPreferences;
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] params) {
        String pulledVaccineList= pullVaccineListFromServer();
        if(!pulledVaccineList.equalsIgnoreCase("")){
            processPulledVaccineList(pulledVaccineList);
        }
//        Log.v("pullv",pullv);
        return null;
    }

    private void processPulledVaccineList(String pulledVaccineList) {
        long Lasttimestamp = context.applicationContext().getSharedPreferences("health_HHID", android.content.Context.MODE_PRIVATE).getLong("lastTimeStampForHHID",0);
        try {
            JSONArray vaccineClientList = new JSONArray(pulledVaccineList);
            for(int i = 0;i<vaccineClientList.length();i++){
                JSONObject vaccineClient = vaccineClientList.getJSONObject(i);
                String entityId = vaccineClient.getString("entityId");
                String healthid = vaccineClient.getString("healthId");
                String timeStamp = vaccineClient.getString("timeStamp");
                if(Long.parseLong(timeStamp)>Lasttimestamp){
                    Lasttimestamp = Long.parseLong(timeStamp);
                }
                HashMap  <String,String> map = new HashMap<String, String>();
                List<CommonPersonObject> householdlist = context.commonrepository("household").findByCaseIDs(entityId);
                if(householdlist.size()>0){
                    map.put("HoH_HID",healthid);
                    context.commonrepository("household").mergeDetails(entityId,map);
                }else{
                    map.put("Member_HID",healthid);
                    context.commonrepository("members").mergeDetails(entityId,map);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("missed log","----");
        context.applicationContext().getSharedPreferences("health_HHID", android.content.Context.MODE_PRIVATE).edit().putLong("lastTimeStampForHHID",Lasttimestamp).commit();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }
    public String  pullVaccineListFromServer() {
        FetchStatus dataStatus = nothingFetched;
        String anmId = allSharedPreferences.fetchRegisteredANM();
        String baseURL = configuration.dristhiBaseURL();
        long Lasttimestamp = context.applicationContext().getSharedPreferences("health_HHID", android.content.Context.MODE_PRIVATE).getLong("lastTimeStampForHHID",0);
        Log.v("responsecontentlasttime",""+Lasttimestamp);
        while (true) {
            String uri = format("{0}/{1}?anmIdentifier={2}&timeStamp={3}",
                    baseURL,
                    client_list,
                    anmId,
                    ""+Lasttimestamp);
            Log.v("pull-uri",uri);
            Response<String> response = httpAgent.fetch(uri);
//            return response.payload();
            if (response.isFailure()) {
                logError(format("health id pull failed."));
                return "";
            }else{
                return response.payload();
            }

        }
    }
}
