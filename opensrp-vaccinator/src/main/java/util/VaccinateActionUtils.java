package util;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.domain.VaccineWrapper;
import org.ei.opensrp.vaccinator.view.VaccinationDialogFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;
import static org.ei.opensrp.util.Utils.convertDateFormat;

/**
 * Created by keyman on 17/11/2016.
 */
public class VaccinateActionUtils {

    public static String formData(Context context, String entityId, String formName, String metaData) {
        return FormUtils.getInstance(context).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
    }

    public static void updateJson(JSONObject jsonObject, String field, String value) {
        try {
               if (jsonObject.has(field)) {
                    JSONObject fieldJson = jsonObject.getJSONObject(field);
                    fieldJson.put("content", value);
            }
        } catch (JSONException e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }
    }

    public static JSONObject find(JSONObject jsonObject, String field) {
        try {
            if (jsonObject.has(field)) {
                return jsonObject.getJSONObject(field);

            }
        } catch (JSONException e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }

        return null;
    }



    public static TableRow findRow(Set<TableLayout> tables, String tag) {
        for (TableLayout table : tables) {
            View view = table.findViewWithTag(tag);
            if (view != null && view instanceof TableRow) {
                return (TableRow) view;
            }
        }
        return null;
    }

    public static TableRow findRow(TableLayout table, String tag) {
        View view = table.findViewWithTag(tag);
        if (view != null && view instanceof TableRow) {
            return (TableRow) view;
        }
        return null;
    }

    public static void vaccinateToday(TableRow tableRow, VaccineWrapper tag) {
        TextView textView = (TextView) tableRow.findViewById(R.id.date);
        textView.setText(R.string.done_today);

        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.VISIBLE);

        tableRow.setOnClickListener(null);
    }

    public static void vaccinateEarlier(TableRow tableRow, VaccineWrapper tag) {
        String vaccineDate = convertDateFormat(tag.getUpdatedVaccineDateAsString(), true);

        TextView textView = (TextView) tableRow.findViewById(R.id.date);
        textView.setText(vaccineDate);

        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.VISIBLE);

        tableRow.setOnClickListener(null);
    }

    public static void undoVaccination(final Context context, TableRow tableRow, final VaccineWrapper tag) {
        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.GONE);

        if (tag.getStatus().equalsIgnoreCase("due")) {
            tableRow.setOnClickListener(new TableRow.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(VaccinationDialogFragment.DIALOG_TAG);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    VaccinationDialogFragment vaccinationDialogFragment = VaccinationDialogFragment.newInstance(context, tag);
                    vaccinationDialogFragment.show(ft, VaccinationDialogFragment.DIALOG_TAG);

                }
            });
        }

    }

    public static void saveFormSubmission(Context appContext, final String formSubmission, String id, final String formName, JSONObject fieldOverrides) {
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(appContext);
            final FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            // Update Fts Tables
            CommonFtsObject commonFtsObject = context.commonFtsObject();
            if (commonFtsObject != null) {
                String[] ftsTables = commonFtsObject.getTables();
                for (String ftsTable : ftsTables) {
                    AllCommonsRepository allCommonsRepository = context.allCommonsRepositoryobjects(ftsTable);
                    boolean updated = allCommonsRepository.updateSearch(submission.entityId());
                    if (updated) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }
    }

    private static String getParams(FormSubmission submission) {
        return new Gson().toJson(
                create(INSTANCE_ID_PARAM, submission.instanceId())
                        .put(ENTITY_ID_PARAM, submission.entityId())
                        .put(FORM_NAME_PARAM, submission.formName())
                        .put(VERSION_PARAM, submission.version())
                        .put(SYNC_STATUS, PENDING.value())
                        .map());
    }

    public static JSONObject retrieveFieldOverides(String overrides) {
        try {
            //get the field overrides map
            if (overrides != null) {
                JSONObject json = new JSONObject(overrides);
                String overridesStr = json.getString("fieldOverrides");
                return new JSONObject(overridesStr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
