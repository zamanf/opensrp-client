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

package org.ei.opensrp.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.drishti.dto.AlertStatus;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.repository.db.CESQLiteHelper;
import org.ei.opensrp.repository.db.Client;
import org.ei.opensrp.repository.db.Obs;
import org.ei.opensrp.repository.db.VaccineRepo;
import org.ei.opensrp.repository.db.VaccineRepo.Vaccine;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.util.Utils.*;

/**
 * Class containing some static utility methods.
 */
public class VaccinatorUtils {
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

    public static String getObsValue(CESQLiteHelper cedb, Client client, boolean humanize, String... fields) throws JSONException, ParseException {
        List<Obs> ol = cedb.getObs(client.getBaseEntityId(), null, "eventDate DESC", fields);
        if(ol == null || ol.size() == 0){
            return "";
        }
        return formatValue(ol.get(0).getValue(), humanize);
    }

    public static ArrayList<HashMap<String, String>> getWasted(String startDate, String endDate, String type){
        String sqlWasted = "select sum (total_wasted)as total_wasted from stock where `report` ='"+type+"' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    public static int getWasted(String startDate, String endDate, String type, String... variables){
        List<CommonPersonObject> cl = org.ei.opensrp.Context.getInstance().commonrepository("stock").customQueryForCompleteRow("SELECT * FROM stock WHERE `report` ='" + type + "' and `date` between '" + startDate + "' and '" + endDate + "'", null, "stock");
        int total = 0;
        for (CommonPersonObject c : cl) {
            for (String v : variables){
                String val = getValue(c.getDetails(), v, "0", false);
                total += IntegerUtil.tryParse(val, 0);
            }
        }
        return total;
    }

    public static ArrayList<HashMap<String, String>> getUsed(String startDate, String endDate, String table, String... vaccines){
        String q = "SELECT * FROM (";
        for (String v: vaccines) {
            q += " (select count(*) "+v+" from "+table+" where "+v+" between '" + startDate + "' and '" + endDate + "') "+v+" , ";
        }
        q = q.trim().substring(0, q.trim().lastIndexOf(","));
        q += " ) e ";

        Log.i("DD", q);
        return org.ei.opensrp.Context.getInstance().commonrepository(table).rawQuery(q);
    }

    public static int getTotalUsed(String startDate, String endDate, String table, String... vaccines) {
        int totalUsed = 0;

        for (HashMap<String, String> v : getUsed(startDate, endDate, table, vaccines)) {
            for (String k : v.keySet()) {
                totalUsed += Integer.parseInt(v.get(k) == null ? "0" : v.get(k));
            }
        }
        Log.i("", "TOTAL USED: " + totalUsed);

        return totalUsed;
    }

    public static void addStatusTag(Context context, TableLayout table, String tag, boolean hrLine){
        TableRow tr = new TableRow(context);
        if(hrLine) {
            tr.setBackgroundColor(Color.LTGRAY);
            tr.setPadding(1, 1, 1, 1);
            table.addView(tr);
        }
        tr = addToRow(context, Html.fromHtml("<b>"+tag+"</b>"), new TableRow(context), true, 1);
        tr.setPadding(15, 5, 0, 0);
        table.addView(tr);
    }
    public static void addVaccineDetail(Context context, TableLayout table, String status, Vaccine vaccine, DateTime vaccineDate, Alert alert, boolean compact) {
        addVaccineDetail(context, table, status, vaccine.display(), vaccineDate != null ? vaccineDate.toString("yyyy-MM-dd") : "", alert, compact);
    }

    public static void addVaccineDetail(Context context, TableLayout table, String status, String vaccine, String vaccineDate, Alert alert, boolean compact){
        TableRow tr = new TableRow(context);
        TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tr.setLayoutParams(trlp);
        if(compact){
            tr.setPadding(10, 1, 10, 1);
        }
        else{
            tr.setPadding(10, 5, 10, 5);
        }

        TextView label = new TextView(context);
        label.setText(vaccine);
        label.setPadding(20, 5, 70, 5);
        label.setTextColor(Color.BLACK);
        label.setBackgroundColor(Color.WHITE);
        tr.addView(label);

        String color = "#ffffff";
        if(status.equalsIgnoreCase("due")) {
            if(alert != null){
                color = getColorValue(context, alert.status());
                vaccineDate = "due: "+convertDateFormat(vaccineDate, true)+"";
            }
            else if(StringUtils.isNotBlank(vaccineDate)){
                color = getColorValue(context, AlertStatus.inProcess);
                vaccineDate = "due: "+convertDateFormat(vaccineDate, true)+"";
            }
        }
        else if(status.equalsIgnoreCase("done")){
            color = "#31B404";
            vaccineDate = convertDateFormat(vaccineDate, true);
        }
        else if(status.equalsIgnoreCase("expired")){
            color = getColorValue(context, AlertStatus.inProcess);
            vaccineDate = "exp: "+convertDateFormat(vaccineDate, true)+"";
        }

        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setVerticalGravity(Gravity.CENTER_VERTICAL);
        l.setGravity(Gravity.CENTER_VERTICAL);

        Button s = new Button(context);
        TableRow.LayoutParams blp = new TableRow.LayoutParams(15, 15);
        blp.setMargins(30, 6, 5, 5);
        s.setLayoutParams(blp);
        s.setGravity(Gravity.CENTER_VERTICAL);
        s.setBackgroundColor(StringUtils.isBlank(color)?Color.WHITE:Color.parseColor(color));
        l.addView(s);

        TextView v = new TextView(context);
        v.setText(vaccineDate);
        v.setPadding(10, 4, 20, 5);
        v.setTextColor(Color.BLACK);
        v.setBackgroundColor(Color.WHITE);
        l.addView(v);

        tr.addView(l);

        table.addView(tr);
    }

    private static DateTime getReceivedDate(Map<String, String> received, Vaccine v){
        if (received.get(v.name()) != null){
            return new DateTime(received.get(v.name()));
        }
        else if(received.get(v.name()+"_retro") != null){
            return new DateTime(received.get(v.name()+"_retro"));
        }
        return null;
    }

    public static List<Map<String, Object>> generateSchedule(String category, DateTime milestoneDate, Map<String, String> received, List<Alert> alerts){
        ArrayList<Vaccine> vl = VaccineRepo.getVaccines(category);
        List<Map<String, Object>> schedule = new ArrayList();
        for (Vaccine v: vl) {
            Map<String, Object> m = new HashMap<>();
            DateTime recDate = getReceivedDate(received, v);
            if (recDate != null) {
                m = createVaccineMap("done", null, recDate, v);
            }
            else if(milestoneDate != null && milestoneDate.plusDays(v.expiryDays()).isBefore(DateTime.now())){
                m = createVaccineMap("expired", null, milestoneDate.plusDays(v.expiryDays()), v);
            }
            else if (alerts.size() > 0) {
                for (Alert a : alerts) {
                    if (a.scheduleName().replaceAll(" ", "").equalsIgnoreCase(v.name())
                            || a.visitCode().replaceAll(" ", "").equalsIgnoreCase(v.name())) {
                        m = createVaccineMap("due", a, new DateTime(a.startDate()), v);
                    }
                }
            }

            if (m.isEmpty()) {
                if (v.prerequisite() != null) {
                    DateTime prereq = getReceivedDate(received, v.prerequisite());
                    if (prereq != null) {
                        prereq = prereq.plusDays(v.prerequisiteGapDays());
                        m = createVaccineMap("due", null, prereq, v);
                    }
                    else {
                        m = createVaccineMap("due", null, null, v);
                    }
                } else if(milestoneDate != null){
                    m = createVaccineMap("due", null, milestoneDate.plusDays(v.milestoneGapDays()), v);
                }
                else {
                    m = createVaccineMap("na", null, null, v);
                }
            }

            schedule.add(m);
        }
        return schedule;
    }

    private static Map<String, Object> createVaccineMap(String status, Alert a, DateTime date, Vaccine v){
        Map<String, Object> m = new HashMap<>();
        m.put("status", status);
        m.put("alert", a);
        m.put("date", date);
        m.put("vaccine", v);

        return m;
    }

    public static Map<String, Object> nextVaccineDue(List<Map<String, Object>> schedule, Date lastVisit){
        Map<String, Object> v = null;
        for (Map<String, Object> m: schedule) {
            if(m != null && m.get("status") != null && m.get("status").toString().equalsIgnoreCase("due")){
                if (v == null) {
                    v = m;
                } else if (m.get("date") != null && v.get("date") != null
                        && ((DateTime) m.get("date")).isBefore((DateTime) v.get("date"))
                        && (lastVisit == null
                            || lastVisit.before(((DateTime) m.get("date")).toDate()))) {
                    v = m;
                }
            }
        }
        return v;
    }
}
