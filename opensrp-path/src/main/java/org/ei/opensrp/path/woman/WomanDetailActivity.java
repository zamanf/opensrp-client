package org.ei.opensrp.path.woman;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.path.domain.FormSubmissionWrapper;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.db.VaccineRepo;
import org.ei.opensrp.path.domain.VaccineWrapper;
import org.ei.opensrp.path.view.VaccinationActionListener;
import org.ei.opensrp.path.template.DetailActivity;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.VaccinateActionUtils;

import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;
import static util.VaccinatorUtils.addStatusTag;
import static util.VaccinatorUtils.addVaccineDetail;
import static util.VaccinatorUtils.generateSchedule;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 11-Nov-15.
 */
public class WomanDetailActivity extends DetailActivity implements VaccinationActionListener {

    TableLayout table;

    public FormSubmissionWrapper formSubmissionWrapper;

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

    public static void startDetailActivity(android.content.Context context, CommonPersonObjectClient clientobj, HashMap<String, String> overrideStringmap, String formName, Class<? extends DetailActivity> detailActivity) {

        client = clientobj;

        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            overrideStringmap = new HashMap<>();
        }

        String metaData = new FieldOverrides(new JSONObject(overrideStringmap).toString()).getJSONString();
        org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + metaData);

        String data = VaccinateActionUtils.formData(context, clientobj.entityId(), formName, metaData);
        FormSubmissionWrapper formSubmissionWrapper = new FormSubmissionWrapper(data, clientobj.entityId(), formName, metaData, "woman");

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_OBJECT, formSubmissionWrapper);
        Intent intent = new Intent(context, detailActivity);
        intent.putExtras(bundle);

        context.startActivity(intent);

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
        if(client == null){
            return "";
        }
        return nonEmptyValue(client.getColumnmaps(), true, false, "existing_program_client_id", "program_client_id");
    }

    @Override
    protected void generateView() {
        retrieveFormSubmissionWrapper();

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
        try {
            age = Years.yearsBetween(new DateTime(getValue(client.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        } catch (Exception e) {
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
                + ", \nUC: " + getValue(client.getColumnmaps(), "union_council", true)
                + ", \nTown: " + getValue(client.getColumnmaps(), "town", true)
                + ", \nCity: " + getValue(client, "city_village", true)
                + ", \nProvince: " + getValue(client, "province", true), null);
        dt2.addView(tr);


        //VACCINES INFORMATION
        table = (TableLayout) findViewById(R.id.woman_vaccine_table);
        List<Alert> al = Context.getInstance().alertService().findByEntityIdAndAlertNames(client.entityId(), "TT 1", "TT 2", "TT 3", "TT 4", "TT 5", "tt1", "tt2", "tt3", "tt4", "tt5");
        List<Map<String, Object>> sch = generateSchedule("woman", null, client.getColumnmaps(), al);
        String previousVaccine = "";
        for (Map<String, Object> m : sch) {

            VaccineWrapper vaccineWrapper = new VaccineWrapper();
            vaccineWrapper.setStatus(m.get("status").toString());
            vaccineWrapper.setVaccine((VaccineRepo.Vaccine) m.get("vaccine"));
            vaccineWrapper.setVaccineDate((DateTime) m.get("date"));
            vaccineWrapper.setAlert((Alert) m.get("alert"));
            vaccineWrapper.setPreviousVaccine(previousVaccine);
            vaccineWrapper.setCompact(true);

            vaccineWrapper.setPatientNumber(getValue(client.getColumnmaps(), "epi_card_number", false));
            vaccineWrapper.setPatientName(getValue(client.getColumnmaps(), "first_name", true));

            addVaccineDetail(this, table, vaccineWrapper);
            previousVaccine = vaccineWrapper.getVaccineAsString();
        }

        if (age < 0) {
            addStatusTag(this, table, "No DoB", true);
        }
        if (StringUtils.isNotBlank(getValue(client.getColumnmaps(), "tt5", false))) {
            addStatusTag(this, table, "Fully Immunized", true);
        } else if (age > 49 && StringUtils.isBlank(getValue(client.getColumnmaps(), "tt5", false))) {
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

    @Override
    public void onVaccinateToday(VaccineWrapper tag) {
        TableRow tableRow = findRow(tag);
        if (tableRow != null) {
            VaccinateActionUtils.vaccinateToday(tableRow, tag);
        }
    }

    @Override
    public void onVaccinateEarlier(VaccineWrapper tag) {
        TableRow tableRow = findRow(tag);
        if (tableRow != null) {
            VaccinateActionUtils.vaccinateEarlier(tableRow, tag);
        }
    }

    @Override
    public void onUndoVaccination(VaccineWrapper tag) {
        TableRow tableRow = findRow(tag);
        if (tableRow != null) {
            VaccinateActionUtils.undoVaccination(this, tableRow, tag);
        }
    }

    private TableRow findRow(VaccineWrapper tag) {
        return VaccinateActionUtils.findRow(table, tag.getVaccine().name());
    }

    public void retrieveFormSubmissionWrapper() {
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable(EXTRA_OBJECT);
            if (serializable != null && serializable instanceof FormSubmissionWrapper) {
                this.formSubmissionWrapper = (FormSubmissionWrapper) serializable;
            }
        }
    }

    public FormSubmissionWrapper getFormSubmissionWrapper() {
        return formSubmissionWrapper;
    }

    @Override
    public void finish() {
        if (formSubmissionWrapper != null && formSubmissionWrapper.updates() > 0) {
            final android.content.Context context = this;
            String data = formSubmissionWrapper.updateFormSubmission();
            if (data != null) {
                VaccinateActionUtils.saveFormSubmission(context, data, formSubmissionWrapper.getEntityId(), formSubmissionWrapper.getFormName(), formSubmissionWrapper.getOverrides());
            }
        }
        super.finish();
    }
}
