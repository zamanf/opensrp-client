package org.ei.opensrp.vaccinator.woman;

import android.widget.TableLayout;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.db.VaccineRepo;
import org.ei.opensrp.view.template.DetailActivity;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.List;
import java.util.Map;

import static org.ei.opensrp.util.Utils.convertDateFormat;
import static org.ei.opensrp.util.Utils.getDataRow;
import static org.ei.opensrp.util.Utils.getValue;
import static org.ei.opensrp.util.Utils.nonEmptyValue;
import static util.VaccinatorUtils.addStatusTag;
import static util.VaccinatorUtils.addVaccineDetail;
import static util.VaccinatorUtils.generateSchedule;

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
    protected Integer profilePicContainerId() {
        return R.id.woman_profilepic;
    }

    @Override
    protected Integer defaultProfilePicResId() {
        return R.drawable.pk_woman_avtar;
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
        return nonEmptyValue(client.getColumnmaps(), true, false, "existing_program_client_id", "program_client_id");
    }

    @Override
    protected void generateView() {
        //WOMAN BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(R.id.woman_detail_info_table1);

       //setting value in WOMAN basic information textviews
        TableRow tr = getDataRow(this, "Program ID", getEntityIdentifier(), null);
        dt.addView(tr);

        tr = getDataRow(this, "EPI Card Number", getValue(client.getColumnmaps(), "epi_card_number", false), null);
        dt.addView(tr);

        tr = getDataRow(this, "Woman's Name", getValue(client.getColumnmaps(), "first_name", true), null);
        dt.addView(tr);

        int age = -1;
        try{
            age = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        tr = getDataRow(this, "Birthdate (Age)", convertDateFormat(getValue(client.getColumnmaps(), "dob", false), "No DoB", true) + " (" + age + " years)", null);
        dt.addView(tr);

        tr = getDataRow(this, "Father's Name", getValue(client.getColumnmaps(), "father_name", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Husband's Name", getValue(client.getColumnmaps(), "husband_name", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.woman_detail_info_table2);
        tr = getDataRow(this, "Ethnicity", getValue(client, "ethnicity", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Married", getValue(client.getColumnmaps(), "marriage", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Contact Number", getValue(client.getColumnmaps(), "contact_phone_number", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Address", getValue(client.getColumnmaps(), "address1", true)
                +", \nUC: "+ getValue(client.getColumnmaps(), "union_council", true)
                +", \nTown: "+ getValue(client.getColumnmaps(), "town", true)
                +", \nCity: "+ getValue(client, "city_village", true)
                +", \nProvince: "+ getValue(client, "province", true), null);
        dt2.addView(tr);


        //VACCINES INFORMATION
        TableLayout table = (TableLayout) findViewById(R.id.woman_vaccine_table);
        List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(), "TT 1", "TT 2", "TT 3", "TT 4", "TT 5", "tt1", "tt2", "tt3", "tt4", "tt5");
        List<Map<String, Object>> sch = generateSchedule("woman", null, client.getColumnmaps(), al);
        String previousVaccine = "";
        for (Map<String, Object> m : sch){
            addVaccineDetail(this, table, m.get("status").toString(), (VaccineRepo.Vaccine) m.get("vaccine"), (DateTime) m.get("date"), (Alert) m.get("alert"), previousVaccine, true);
            previousVaccine = ((VaccineRepo.Vaccine) m.get("vaccine")).display();
        }

        if(age < 0){
            addStatusTag(this, table, "No DoB", true);
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
        tr = getDataRow(this, "EDD", convertDateFormat(getValue(client.getColumnmaps(), "final_edd", false), "N/A", true), null);
        pt.addView(tr);
        tr = getDataRow(this, "LMP", convertDateFormat(getValue(client, "final_lmp", false), "N/A", true), null);
        pt.addView(tr);
        tr = getDataRow(this, "GA (weeks)", getValue(client.getColumnmaps(), "final_ga", "N/A", false), null);
        pt.addView(tr);
    }
}
