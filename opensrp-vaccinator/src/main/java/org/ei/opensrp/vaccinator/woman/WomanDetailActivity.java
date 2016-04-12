package org.ei.opensrp.vaccinator.woman;

import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.analytics.CountlyAnalytics;
import org.ei.opensrp.vaccinator.analytics.Events;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 11-Nov-15.
 */
public class WomanDetailActivity extends DetailActivity {

    @Override
    protected int layoutResId() {
        return R.layout.woman_detail_activity;
    }

    @Override
    protected String pageTitle() {
        return "Woman Details";
    }

    @Override
    protected String titleBarId() {
        return getEntityIdentifier();
    }

    @Override
    protected Class onBackActivity() {
        return WomanSmartRegisterActivity.class;
    }

    @Override
    protected int profilePicResId() {
        return R.id.woman_profilepic;
    }

    @Override
    protected String bindType() {
        return "pkwoman";
    }

    @Override
    protected boolean allowImageCapture() {
        return true;
    }

    public String getEntityIdentifier() {
        return nonEmptyValue(client.getDetails(), true, false, "existing_program_client_id", "program_client_id");
    }

    @Override
    protected void generateView() {
        //WOMAN BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(R.id.woman_detail_info_table1);

       //setting value in WOMAN basic information textviews
        TableRow tr = getDataRow(this, "Program ID", getEntityIdentifier(), null);
        dt.addView(tr);

        tr = getDataRow(this, "EPI Card Number", getValue(client, "epi_card_number", false), null);
        dt.addView(tr);

        tr = getDataRow(this, "Woman`s Name", getValue(client, "first_name", true), null);
        dt.addView(tr);

        int age = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        tr = getDataRow(this, "Birthdate (Age)", convertDateFormat(getValue(client.getColumnmaps(), "dob", false), true) + " (" + age + " years)", null);
        dt.addView(tr);

        tr = getDataRow(this, "Father`s Name", getValue(client, "father_name", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Husband`s Name", getValue(client, "husband_name", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.woman_detail_info_table2);
        tr = getDataRow(this, "Ethnicity", getValue(client, "ethnicity", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Married", getValue(client, "marriage", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Contact Number", getValue(client, "contact_phone_number", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Address", getValue(client, "house_number", true)+ " "+ getValue(client, "street", true)
                +", \nUC: "+ getValue(client, "union_council", true)
                +", \nTown: "+ getValue(client, "town", true)
                +", \nCity: "+ getValue(client, "city_village", true)
                +", \nProvince: "+ getValue(client, "province", true), null);
        dt2.addView(tr);


        //VACCINES INFORMATION
        TableLayout table = (TableLayout) findViewById(R.id.woman_vaccine_table);
        for (int i=1; i <= 5; i++){
            tr = new TableRow(this);
            TableRow.LayoutParams trlp = new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(trlp);
            tr.setPadding(10, 5, 10, 5);

            TextView label = new TextView(this);
            label.setText("TT "+i);
            label.setPadding(20, 5, 70, 5);
            label.setTextColor(Color.BLACK);
            label.setBackgroundColor(Color.WHITE);
            tr.addView(label);

            String val = convertDateFormat(nonEmptyValue(client.getColumnmaps(), true, false, "tt" + i, "tt" + i + "_retro"), true);
            if(val == "") {
                try{
                    List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(), "tt" + i, "TT " + i);
                    if(al.size() > 0){
                        val = "<due : "+convertDateFormat(al.get(0).startDate(), true)+">";
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            TextView v = new TextView(this);
            v.setText(val);
            v.setPadding(70, 5, 20, 5);
            v.setTextColor(Color.BLACK);
            v.setBackgroundColor(Color.WHITE);
            tr.addView(v);

            table.addView(tr);
        }

        TableLayout pt = (TableLayout) findViewById(R.id.woman_pregnancy_table);
        tr = getDataRow(this, "Pregnant", getValue(client.getColumnmaps(), "pregnant", true), null);
        pt.addView(tr);
        tr = getDataRow(this, "EDD", convertDateFormat(getValue(client, "final_edd", false), "N/A", true), null);
        pt.addView(tr);
        tr = getDataRow(this, "LMP", convertDateFormat(getValue(client, "final_lmp", false), "N/A", true), null);
        pt.addView(tr);
        tr = getDataRow(this, "GA (weeks)", getValue(client, "final_ga", "N/A", false), null);
        pt.addView(tr);
    }

    @Override
    protected void onStart(){
        super.onStart();
        HashMap<String,String> segments = new HashMap<String, String>();
        CountlyAnalytics.startAnalytics(this, Events.WOMAN_DETAIL_ACTIVITY, segments);
    }

    @Override
    protected void onStop(){
        super.onStop();
        CountlyAnalytics.stopAnalytics();
    }
}
