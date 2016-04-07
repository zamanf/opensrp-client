/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.fragment.SmartRegisterFragment;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class containing some static utility methods.
 */
public class Utils {
    private static final SimpleDateFormat UI_DF = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat UI_DTF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private static final SimpleDateFormat DB_DF = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DB_DTF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Utils() {};

    public static String convertDateFormat(String date, boolean suppressException){
        try{
            return UI_DF.format(DB_DF.parse(date));
        }
        catch (ParseException e) {
            e.printStackTrace();
            if(!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static String convertDateFormat(String date, String defaultV, boolean suppressException){
        try{
            return UI_DF.format(DB_DF.parse(date));
        }
        catch (ParseException e) {
            e.printStackTrace();
            if(!suppressException) throw new RuntimeException(e);
        }
        return StringUtils.isNotBlank(defaultV)?defaultV:"";
    }

    public static String convertDateFormat(DateTime date){
        return UI_DF.format(date.toDate());
    }

    public static String convertDateTimeFormat(String date, boolean suppressException){
        try{
            return UI_DTF.format(DB_DTF.parse(date.replace("T", " ")));
        }
        catch (ParseException e) {
            e.printStackTrace();
            if(!suppressException) throw new RuntimeException(e);
        }
        return "";
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, boolean humanize){
        v.setText(getValue(pc, field, humanize));
    }

    public static void fillValue(TextView v, CommonPersonObjectClient pc, String field, String defaultV, boolean humanize){
        String val = getValue(pc, field, humanize);
        if(StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)){
            val = defaultV;
        }
        v.setText(val);
    }

    public static HashMap<String, String> providerDetails(){
        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
        org.ei.opensrp.util.Log.logDebug("ANM DETAILS" + context.anmController().get());
        org.ei.opensrp.util.Log.logDebug("USER DETAILS" + context.allSettings().fetchUserInformation());
        org.ei.opensrp.util.Log.logDebug("TEAM DETAILS" + getPreference(context.applicationContext(), "team", "{}"));

        String locationJson = context.anmLocationController().get();
        LocationTree locationTree = EntityUtils.fromJson(locationJson, LocationTree.class);

        Map<String, TreeNode<String, Location>> locationMap = locationTree.getLocationsHierarchy();
        Map<String, String> locations = new HashMap<>();
        addToList(locations, locationMap, "country");
        addToList(locations, locationMap, "province");
        addToList(locations, locationMap, "city");
        addToList(locations, locationMap, "town");
        addToList(locations, locationMap, "uc");
        addToList(locations, locationMap, "vaccination center");

        HashMap<String, String> map = new HashMap<>();
        map.put("provider_uc", locations.get("uc"));
        map.put("provider_town", locations.get("town"));
        map.put("provider_city", locations.get("city"));
        map.put("provider_province", locations.get("province"));
        map.put("provider_location_id", locations.get("vaccination center"));
        map.put("provider_location_name", locations.get("vaccination center"));
        map.put("provider_id", context.anmService().fetchDetails().name());

        try {
            JSONObject tm = new JSONObject(getPreference(context.applicationContext(), "team", "{}"));
            map.put("provider_name", tm.getJSONObject("person").getString("display"));
            map.put("provider_identifier", tm.getString("identifier"));
            map.put("provider_team", tm.getJSONObject("team").getString("teamName"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return map;
    }

    private static void addToList(Map<String, String> locations, Map<String, TreeNode<String, Location>> locationMap, String locationTag) {
        for (Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {
            boolean tagFound = false;
            if (entry.getValue() != null) {
                Location l = entry.getValue().getNode();

                if(l.getTags() != null){
                    for (String s : l.getTags()) {
                        if (s.equalsIgnoreCase(locationTag)) {
                            locations.put(locationTag, l.getName());
                            tagFound = true;
                        }
                    }
                }
            }
            if (!tagFound) {
                if (entry.getValue().getChildren() != null) {
                    addToList(locations, entry.getValue().getChildren(), locationTag);
                }
            }
        }
    }

    public static void fillValue(TextView v, String value){
        v.setText(value);
    }

    public static String formatValue(String value, boolean humanize){
        if(value == null){
            value = "";
        }
        return humanize? StringUtil.humanize(value):value;
    }

    public static String formatValue(Object value, boolean humanize){
        if(value == null){
            value = "";
        }
        return humanize? StringUtil.humanize(value.toString()):value.toString();
    }
    public static String getValue(CommonPersonObjectClient pc, String field, boolean humanize){
        return formatValue(pc.getDetails().get(field), humanize);
    }

    public static String getValue(CommonPersonObjectClient pc, String field, String defaultV, boolean humanize){
        String val = formatValue(pc.getDetails().get(field), humanize);
        if(StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)){
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, String defaultV, boolean humanize){
        String val = formatValue(cm.get(field), humanize);
        if(StringUtils.isNotBlank(defaultV) && StringUtils.isBlank(val)){
            val = defaultV;
        }
        return val;
    }

    public static String getValue(Map<String, String> cm, String field, boolean humanize) {
        return formatValue(cm.get(field), humanize);
    }

    public static String nonEmptyValue(Map<String, String> cm, boolean asc, boolean humanize, String... fields){
        List<String> l = Arrays.asList(fields);
        if(!asc){
            Collections.reverse(l);
        }
        for (String f : l) {
            String v = getValue(cm, f, humanize);
            if (v != "") {
                return v;
            }
        }
        return "";
    }

    public static String overridesToString(Map<String, String> overrides, SmartRegisterClient client, SmartRegisterFragment.ByColumnAndByDetails byColumnAndByDetails){
        JSONObject overridejsonobject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : overrides.entrySet()) {
                switch (byColumnAndByDetails) {
                    case byDetails:
                        overridejsonobject.put(entry.getKey(), ((CommonPersonObjectClient) client).getDetails().get(entry.getValue()));
                        break;
                    case byColumn:
                        overridejsonobject.put(entry.getKey(), ((CommonPersonObjectClient) client).getColumnmaps().get(entry.getValue()));
                        break;
                    default:
                        overridejsonobject.put(entry.getKey(), entry.getValue());
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return overridejsonobject.toString();
    }

    private ArrayList<HashMap<String, String>> getWasted(String startDate, String endDate, String type){
        String sqlWasted = "select sum (total_wasted)as total_wasted from stock where `report` ='"+type+"' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    public static TableRow getDataRow(Context context, String label, String value, TableRow row){
        TableRow tr = row;
        if(row == null){
            tr = new TableRow(context);
            TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trlp);
            tr.setPadding(10, 5, 10, 5);
        }

        TextView l = new TextView(context);
        l.setText(label + ": ");
        l.setPadding(20, 2, 20, 2);
        l.setTextColor(Color.BLACK);
        l.setTextSize(14);
        l.setBackgroundColor(Color.WHITE);
        tr.addView(l);

        TextView v = new TextView(context);
        v.setText(value);
        v.setPadding(20, 2, 20, 2);
        v.setTextColor(Color.BLACK);
        v.setTextSize(14);
        v.setBackgroundColor(Color.WHITE);
        tr.addView(v);

        return tr;
    }

    public static TableRow getDataRow(Context context){
        TableRow tr = new TableRow(context);
        TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tr.setLayoutParams(trlp);
        tr.setPadding(0, 0, 0, 0);
        // tr.setBackgroundColor(Color.BLUE);
        return tr;
    }

    public static TableRow addToRow(Context context, String value, TableRow row){
        return addToRow(context, value, row, false);
    }

    public static TableRow addToRow(Context context, String value, TableRow row, boolean compact){
        TextView v = new TextView(context);
        v.setText(value);
        if(compact){
            v.setPadding(15, 4, 1, 1);
        }
        else {
            v.setPadding(2, 15, 2, 15);
        }
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT, 1
        );
        params.setMargins(0, 0, 1, 0);
        v.setLayoutParams(params);
        v.setTextColor(Color.BLACK);
        v.setTextSize(14);
        v.setBackgroundColor(Color.WHITE);
        row.addView(v);

        return row;
    }

    public static String getPreference(Context context, String key, String defaultVal){
        return context.getSharedPreferences("preferences", Context.MODE_PRIVATE).getString(key, defaultVal);
    }

    public static Gson getLongDateAwareGson(){
        Gson g = new GsonBuilder().registerTypeAdapter(DateTime.class, new JsonDeserializer<DateTime>() {
            @Override
            public DateTime deserialize(JsonElement e, Type t, JsonDeserializationContext jd) throws JsonParseException {
                return new DateTime(e.getAsLong());
            }
        }).create();
        return  g;
    }

    public static boolean writePreference(Context context, String name, String value){
        SharedPreferences pref = context.getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = pref.edit();
        ed.putString(name, value);
        return ed.commit();
    }

    public static boolean isConnectedToNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public static void setProfiePic(ImageView mImageView, String entityId){
        ProfileImage photo = ((ImageRepository) org.ei.opensrp.Context.getInstance().imageRepository()).findByEntityId(entityId, "dp");
        if(photo != null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(photo.getFilepath(), options);
            mImageView.setImageBitmap(bitmap);
        }
    }

    public static void setThumbnail(ImageView mImageView, String entityId){
        ProfileImage photo = ((ImageRepository) org.ei.opensrp.Context.getInstance().imageRepository()).findByEntityId(entityId, "dp");
        if(photo != null){
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(photo.getFilepath(), bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(photo.getFilepath(), bmOptions);
            mImageView.setImageBitmap(bitmap);
        }
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

}
