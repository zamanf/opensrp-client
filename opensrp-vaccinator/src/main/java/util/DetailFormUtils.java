package util;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.domain.VaccineWrapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.util.Set;

import static org.ei.opensrp.util.Utils.convertDateFormat;

/**
 * Created by keyman on 17/11/2016.
 */
public class DetailFormUtils {

    public static String formData(Context context, String entityId, String formName, String metaData) {
        return FormUtils.getInstance(context).generateXMLInputForFormWithEntityId(entityId, formName, metaData);
    }

    public static String convertFormDataToString(JSONObject jsonObject) throws JSONException {
        return XML.toString(jsonObject);
    }

    public static JSONObject convertFormDataToJson(String data) throws JSONException {
        return XML.toJSONObject(data);
    }

    public static void updateJson(JSONObject jsonObject, String parentField, String field, String value) {
        try {
            JSONObject parentJson = jsonObject.getJSONObject(parentField);
            JSONObject fieldJson = parentJson.getJSONObject(field);
            fieldJson.put("content", value);
        } catch (JSONException e) {
            Log.e(DetailFormUtils.class.getName(), "", e);
        }
    }

    public static void removeJson(JSONObject jsonObject, String parentField, String field) {
        try {
            JSONObject parentJson = jsonObject.getJSONObject(parentField);
            JSONObject fieldJson = parentJson.getJSONObject(field);
            fieldJson.remove("content");
        } catch (JSONException e) {
            Log.e(DetailFormUtils.class.getName(), "", e);
        }
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

    public static void vaccinateToday(Set<TableLayout> tables, VaccineWrapper tag) {
        TableRow tableRow = DetailFormUtils.findRow(tables, tag.getVaccine().name());
        if (tableRow != null) {
            vaccinateToday(tableRow, tag);
        }
    }

    public static void vaccinateToday(TableLayout table, VaccineWrapper tag) {
        TableRow tableRow = DetailFormUtils.findRow(table, tag.getVaccine().name());
        if (tableRow != null) {
            vaccinateToday(tableRow, tag);
        }

    }

    public static void vaccinateToday(TableRow tableRow, VaccineWrapper tag) {
        TextView textView = (TextView) tableRow.findViewById(R.id.date);
        textView.setText(R.string.done_today);

        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.VISIBLE);

        tableRow.setOnClickListener(null);
    }

    public static void vaccinateEarlier(Set<TableLayout> tables, VaccineWrapper tag) {
        TableRow tableRow = DetailFormUtils.findRow(tables, tag.getVaccine().name());
        if (tableRow != null) {
            vaccinateEarlier(tableRow, tag);
        }
    }

    public static void vaccinateEarlier(TableLayout table, VaccineWrapper tag) {
        TableRow tableRow = DetailFormUtils.findRow(table, tag.getVaccine().name());
        if (tableRow != null) {
            vaccinateEarlier(tableRow, tag);
        }
    }

    public static void vaccinateEarlier(TableRow tableRow, VaccineWrapper tag) {
        String vaccineDate = convertDateFormat(tag.getUpdatedVaccineDateAsString(), true);

        TextView textView = (TextView) tableRow.findViewById(R.id.date);
        textView.setText(vaccineDate);

        Button button = (Button) tableRow.findViewById(R.id.undo);
        button.setVisibility(View.VISIBLE);

        tableRow.setOnClickListener(null);
    }

}
