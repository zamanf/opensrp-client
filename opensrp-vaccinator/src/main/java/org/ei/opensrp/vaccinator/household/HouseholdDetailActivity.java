package org.ei.opensrp.vaccinator.household;

import android.widget.TableLayout;
import android.widget.TableRow;

import org.ei.opensrp.vaccinator.woman.DetailActivity;
import org.ei.opensrp.vaccinator.R;
import org.joda.time.DateTime;
import org.joda.time.Years;

import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;

/**
 * Created by Safwan on 4/21/2016.
 */
public class HouseholdDetailActivity extends DetailActivity {
    @Override
    protected int layoutResId() {
        return R.layout.household_detail_activity;
    }

    @Override
    protected String pageTitle() {
        return "Household Members' Details";
    }

    @Override
    protected String titleBarId() {
        return getEntityIdentifier();
    }

    @Override
    protected void generateView() {
        TableLayout dt = (TableLayout) findViewById(R.id.household_detail_info_table1);

        //setting value in WOMAN basic information textviews
        TableRow tr = getDataRow(this, "Person ID", getEntityIdentifier(), null);
        dt.addView(tr);

        tr = getDataRow(this, "Name", getValue(client, "first_name_hhh", false) + " " + getValue(client, "last_name_hhh", true), null);
        dt.addView(tr);

        /*int age = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        tr = getDataRow(this, "DOB (Age)", convertDateFormat(getValue(client.getColumnmaps(), "dob", false), true) + " (" + age + " years)", null);
        dt.addView(tr);*/

        tr = getDataRow(this, "Gender", getValue(client, "gender_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Ethnicity", getValue(client, "ethnicity_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Contact Phone Number", getValue(client, "contact_phone_number_hhh", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.household_detail_info_table2);
        tr = getDataRow(this, "Household ID", getValue(client, "existing_household_id", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Number of Members", getValue(client, "num_fam_member", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Source of Drinking Water", getValue(client, "water_source", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Latrine System", getValue(client, "latrine_system", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Address", getValue(client, "address1", true)
                +", \nUC: "+ getValue(client, "union_council", true)
                +", \nTown: "+ getValue(client, "town", true)
                +", \nCity: "+ getValue(client, "city_village", true)
                +", \nProvince: "+ getValue(client, "province", true), null);
        dt2.addView(tr);

    }

    @Override
    protected Class onBackActivity() {
        return HouseholdSmartRegisterActivity.class;
    }

    @Override
    protected int profilePicResId() {
        return R.id.household_profilepic;
    }

    @Override
    protected String bindType() {
        return "pkhousehold";
    }

    @Override
    protected boolean allowImageCapture() {
        return true;
    }

    public String getEntityIdentifier() {
        return nonEmptyValue(client.getDetails(), true, false, "existing_household_id", "household_id");
    }
}
