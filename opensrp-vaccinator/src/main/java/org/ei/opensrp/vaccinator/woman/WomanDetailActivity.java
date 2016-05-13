package org.ei.opensrp.vaccinator.woman;

import android.widget.TableLayout;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.vaccinator.R;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.List;

import static util.Utils.addStatusTag;
import static util.Utils.addVaccineDetail;
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
        tr = getDataRow(this, "Birthdate (Age)", convertDateFormat(getValue(client.getColumnmaps(), "calc_dob_confirm_hhh", false), true) + " (" + age + " years)", null);
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
        tr = getDataRow(this, "Address", getValue(client, "address1", true)
                +", \nUC: "+ getValue(client, "union_council", true)
                +", \nTown: "+ getValue(client, "town", true)
                +", \nCity: "+ getValue(client, "city_village", true)
                +", \nProvince: "+ getValue(client, "province", true), null);
        dt2.addView(tr);


        //VACCINES INFORMATION
        TableLayout table = (TableLayout) findViewById(R.id.woman_vaccine_table);
        for (int i=1; i <= 5; i++){
            String val = convertDateFormat(nonEmptyValue(client.getColumnmaps(), true, false, "tt" + i, "tt" + i + "_retro"), true);
            List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(), "tt" + i, "TT " + i);
            addVaccineDetail(this, table, "TT "+i, val, al.size()>0?al.get(0):null, false);
        }

        if(StringUtils.isNotBlank(getValue(client.getColumnmaps(), "tt5", false))){
            addStatusTag(this, table, "Fully Immunized", true);
        }
        else if(age > 49 && StringUtils.isBlank(getValue(client.getColumnmaps(), "tt5", false))){
            addStatusTag(this, table, "Partially Immunized", true);
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
}
