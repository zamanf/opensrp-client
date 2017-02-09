package com.vijay.jsonwizard.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by vijay on 24-05-2015.
 */
public class FormUtils {
    public static final String FONT_BOLD_PATH = "fonts/Roboto-Bold.ttf";
    public static final String FONT_REGULAR_PATH = "fonts/Roboto-Regular.ttf";
    public static final int MATCH_PARENT = -1;
    public static final int WRAP_CONTENT = -2;
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyy-MM-dd'T'HH:mm:ssZ");

    public static LinearLayout.LayoutParams getLayoutParams(int width, int height, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.setMargins(left, top, right, bottom);
        return layoutParams;
    }

    public static TextView getTextViewWith(Context context, int textSizeInSp, String text,
                                           String key, String type, String openMrsEntityParent,
                                           String openMrsEntity, String openMrsEntityId,
                                           LinearLayout.LayoutParams layoutParams, String fontPath) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTag(R.id.key, key);
        textView.setTag(R.id.type, type);
        textView.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
        textView.setTag(R.id.openmrs_entity, openMrsEntity);
        textView.setTag(R.id.openmrs_entity_id, openMrsEntityId);
        textView.setId(ViewUtil.generateViewId());
        textView.setTextSize(textSizeInSp);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public static int dpToPixels(Context context, float dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static void updateStartProperties(PropertyManager propertyManager, JSONObject form)
            throws Exception {
        if (form.has("meta")) {
            if (form.getJSONObject("meta").has("start")) {
                Calendar calendar = Calendar.getInstance();
                JSONObject start = form.getJSONObject("meta").getJSONObject("start");
                String value = DATE_TIME_FORMAT.format(calendar.getTime());
                if (value == null) value = "";
                start.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.DEVICE_ID_PROPERTY)) {
                JSONObject deviceId = form.getJSONObject("meta").getJSONObject(PropertyManager.DEVICE_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.DEVICE_ID_PROPERTY);
                if (value == null) value = "";
                deviceId.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.SUBSCRIBER_ID_PROPERTY)) {
                JSONObject subscriberId = form.getJSONObject("meta").getJSONObject(PropertyManager.SUBSCRIBER_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SUBSCRIBER_ID_PROPERTY);
                if (value == null) value = "";
                subscriberId.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.SIM_SERIAL_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject("meta").getJSONObject(PropertyManager.SIM_SERIAL_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SIM_SERIAL_PROPERTY);
                if (value == null) value = "";
                simSerial.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.PHONE_NUMBER_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject("meta").getJSONObject(PropertyManager.PHONE_NUMBER_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.PHONE_NUMBER_PROPERTY);
                if (value == null) value = "";
                simSerial.put("value", value);
            }
        }
    }

    public static void updateEndProperties(PropertyManager propertyManager, JSONObject form)
            throws Exception {
        if (form.has("meta")) {
            if (form.getJSONObject("meta").has("end")) {
                Calendar calendar = Calendar.getInstance();
                JSONObject end = form.getJSONObject("meta").getJSONObject("end");
                String value = DATE_TIME_FORMAT.format(calendar.getTime());
                if (value == null) value = "";
                end.put("value", value);
            }

            if (form.getJSONObject("meta").has("today")) {
                Calendar calendar = Calendar.getInstance();
                JSONObject today = form.getJSONObject("meta").getJSONObject("today");
                String value = DATE_FORMAT.format(calendar.getTime());
                if (value == null) value = "";
                today.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.DEVICE_ID_PROPERTY)) {
                JSONObject deviceId = form.getJSONObject("meta").getJSONObject(PropertyManager.DEVICE_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.DEVICE_ID_PROPERTY);
                if (value == null) value = "";
                deviceId.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.SUBSCRIBER_ID_PROPERTY)) {
                JSONObject subscriberId = form.getJSONObject("meta").getJSONObject(PropertyManager.SUBSCRIBER_ID_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SUBSCRIBER_ID_PROPERTY);
                if (value == null) value = "";
                subscriberId.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.SIM_SERIAL_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject("meta").getJSONObject(PropertyManager.SIM_SERIAL_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.SIM_SERIAL_PROPERTY);
                if (value == null) value = "";
                simSerial.put("value", value);
            }

            if (form.getJSONObject("meta").has(PropertyManager.PHONE_NUMBER_PROPERTY)) {
                JSONObject simSerial = form.getJSONObject("meta").getJSONObject(PropertyManager.PHONE_NUMBER_PROPERTY);
                String value = propertyManager.getSingularProperty(
                        PropertyManager.PHONE_NUMBER_PROPERTY);
                if (value == null) value = "";
                simSerial.put("value", value);
            }
        }
    }
}
