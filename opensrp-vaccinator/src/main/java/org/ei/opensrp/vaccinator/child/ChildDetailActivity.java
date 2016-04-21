package org.ei.opensrp.vaccinator.child;

import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.woman.DetailActivity;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.List;

import static util.Utils.addStatusTag;
import static util.Utils.addVaccineDetail;
import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.Utils.hasAnyEmptyValue;
import static util.Utils.nonEmptyValue;

public class ChildDetailActivity extends DetailActivity {
    @Override
    protected int layoutResId() {
        return R.layout.child_detail_activity;
    }

    @Override
    protected String pageTitle() {
        return "Child Details";
    }

    @Override
    protected String titleBarId() {
        return getEntityIdentifier();
    }

    @Override
    protected Class onBackActivity() {
        return ChildSmartRegisterActivity.class;
    }

    @Override
    protected int profilePicResId() {
        return R.id.child_profilepic;
    }

    @Override
    protected String bindType() {
        return "pkchild";
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
        String gender = getValue(client, "gender", true);
        if(gender.equalsIgnoreCase("male")){
            ((ImageView)findViewById(R.id.child_profilepic)).setImageResource(R.drawable.child_boy_infant);
        }
        else if(gender.equalsIgnoreCase("female")){
            ((ImageView)findViewById(R.id.child_profilepic)).setImageResource(R.drawable.child_girl_infant);
        }
        else if(gender.toLowerCase().contains("trans")){
            ((ImageView)findViewById(R.id.child_profilepic)).setImageResource(R.drawable.child_transgender_inflant);
        }

        //BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(R.id.child_detail_info_table1);

        //setting value in WOMAN basic information textviews
        TableRow tr = getDataRow(this, "Program ID", getEntityIdentifier(), null);
        dt.addView(tr);

        tr = getDataRow(this, "EPI Card Number", getValue(client, "epi_card_number", false), null);
        dt.addView(tr);

        tr = getDataRow(this, "Child`s Name", getValue(client, "first_name", true)+" "+getValue(client, "last_name", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Birthdate (Age)", convertDateFormat(getValue(client.getColumnmaps(), "dob", false), true) + " (" + Months.monthsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getMonths() + " months" + ")", null);
        dt.addView(tr);

        tr = getDataRow(this, "Gender", getValue(client, "gender", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Ethnicity", getValue(client, "ethnicity", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.child_detail_info_table2);

        tr = getDataRow(this, "Mother`s Name", getValue(client, "mother_name", true), null);
        dt2.addView(tr);

        tr = getDataRow(this, "Father`s Name", getValue(client, "father_name", true), null);
        dt2.addView(tr);

        tr = getDataRow(this, "Contact Number", getValue(client, "contact_phone_number", false), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Address", getValue(client, "address1", true)
                +", \nUC: "+ getValue(client, "union_council", true)
                +", \nTown: "+ getValue(client, "town", true)
                +", \nCity: "+ getValue(client, "city_village", true)
                +", \nProvince: "+ getValue(client, "province", true), null);
        dt2.addView(tr);

        String[] vl = new String[]{"bcg", "opv0", "penta1", "opv1","pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2"};

        //VACCINES INFORMATION
        TableLayout table = null;
        for (int i=0; i < vl.length; i++) {
            if (i <= 7) {
                table = (TableLayout) findViewById(R.id.child_vaccine_table1);
            } else {
                table = (TableLayout) findViewById(R.id.child_vaccine_table2);
            }

            String val = convertDateFormat(nonEmptyValue(client.getColumnmaps(), true, false, vl[i], vl[i] + "_retro"), true);
            List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(), vl[i], vl[i].toUpperCase(), StringUtil.humanize(vl[i]));
            addVaccineDetail(this, table, vl[i].length() <= 4 ? vl[i].toUpperCase() : StringUtil.humanize(vl[i]), val, al.size()>0?al.get(0):null, true);
        }

        int agey = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();

        if(!hasAnyEmptyValue(client.getColumnmaps(), vl)){
            addStatusTag(this, table, "Fully Immunized", true);
        }
        else if(agey >= 5 && hasAnyEmptyValue(client.getColumnmaps(), vl)){
            addStatusTag(this, table, "Partially Immunized", true);
        }
    }
}
