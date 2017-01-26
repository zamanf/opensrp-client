package com.vijay.jsonwizard.widgets;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vijay.jsonwizard.utils.FormUtils.FONT_BOLD_PATH;
import static com.vijay.jsonwizard.utils.FormUtils.MATCH_PARENT;
import static com.vijay.jsonwizard.utils.FormUtils.WRAP_CONTENT;
import static com.vijay.jsonwizard.utils.FormUtils.getLayoutParams;
import static com.vijay.jsonwizard.utils.FormUtils.getTextViewWith;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 25/01/2017
 */
public class DatePickerFactory implements FormWidgetFactory {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject,
                                       CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        try {
            final MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                    R.layout.item_edit_text, null);
            editText.setHint(jsonObject.getString("hint"));
            editText.setFloatingLabelText(jsonObject.getString("hint"));
            editText.setId(ViewUtil.generateViewId());
            editText.setTag(R.id.key, jsonObject.getString("key"));
            if(jsonObject.has("v_required")) {
                JSONObject requiredObject = jsonObject.optJSONObject("v_required");
                String requiredValue = requiredObject.getString("value");
                if (!TextUtils.isEmpty(requiredValue)) {
                    if (Boolean.TRUE.toString().equalsIgnoreCase(requiredValue)) {
                        editText.addValidator(new RequiredValidator(requiredObject.getString("err")));
                    }
                }
            }

            if(jsonObject.has("default")) {
                editText.setText(DATE_FORMAT.format(getDate(jsonObject.getString("default")).getTime()));
            }

            final ContextThemeWrapper themedContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                themedContext = new ContextThemeWrapper(context,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
            } else {
                themedContext = new ContextThemeWrapper(context,
                        android.R.style.Theme_Light_NoTitleBar);
            }
            final DatePickerDialog datePickerDialog = new DatePickerDialog(themedContext,
                    jsonObject, editText);
            //datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {
                        datePickerDialog.setDate(getDate(editText.getText().toString()));
                        datePickerDialog.show();
                    }
                }
            });

            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editText.setText("");
                    return true;
                }
            });

            views.add(editText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return views;
    }

    /**
     * This method returns a {@link Calendar} object corresponding to a date matching the format
     * specified in {@code DATE_FORMAT} or a day in reference to today e.g today, today-1, today+10
     *
     * @param dayString The string to be converted to a date
     * @return  The calendar object corresponding to the day, or object corresponding to today's
     *          date if an error occurred
     */
    private static Calendar getDate(String dayString) {
        Calendar calendarDate = Calendar.getInstance();

        if (dayString != null && dayString.trim().length() > 0) {
            dayString = dayString.trim().toLowerCase();
            if (!dayString.equals("today")) {
                Pattern pattern = Pattern.compile("today\\s*([-\\+])\\s*(\\d+)");
                Matcher matcher = pattern.matcher(dayString);
                if (matcher.find()) {
                    int noDays = Integer.parseInt(matcher.group(2));
                    if (matcher.group(1).equals("-")) {
                        noDays = noDays * -1;
                    }

                    calendarDate.add(Calendar.DATE, noDays);
                } else {
                    try {
                        calendarDate.setTime(DATE_FORMAT.parse(dayString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return calendarDate;
    }

    public static class DatePickerDialog {
        private final DatePicker datePicker;
        private final MaterialEditText parentEditText;
        private final AlertDialog alertDialog;

        public DatePickerDialog(Context context, JSONObject formField, MaterialEditText parentEditText) {
            this.parentEditText = parentEditText;

            LayoutInflater inflater = LayoutInflater.from(context);
            View inflatedView = inflater.inflate(R.layout.dialog_date_picker, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(inflatedView);

            datePicker = (DatePicker) inflatedView.findViewById(R.id.date_picker);

            try {
                if (formField.has("min_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    datePicker.setMinDate(getDate(formField.getString("min_date"))
                            .getTimeInMillis());
                }

                if (formField.has("max_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    datePicker.setMaxDate(getDate(formField.getString("max_date"))
                            .getTimeInMillis());
                }

                if (formField.has("expanded") && formField.getBoolean("expanded") == true
                        && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    datePicker.setCalendarViewShown(true);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    datePicker.setCalendarViewShown(false);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            builder.setCancelable(false);
            builder.setPositiveButton(context.getResources().getString(R.string.label_ok),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Calendar calendarDate = Calendar.getInstance();
                    calendarDate.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    calendarDate.set(Calendar.MONTH, datePicker.getMonth());
                    calendarDate.set(Calendar.YEAR, datePicker.getYear());
                    DatePickerDialog.this.parentEditText.setText(DATE_FORMAT.format(calendarDate.getTime()));

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(context.getResources().getString(R.string.label_cancel),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            alertDialog = builder.create();
        }

        public void setDate(Calendar calendarDate) {
            if(calendarDate != null) {
                datePicker.updateDate(
                        calendarDate.get(Calendar.YEAR),
                        calendarDate.get(Calendar.MONTH),
                        calendarDate.get(Calendar.DAY_OF_MONTH));
            }
        }

        public void show() {
            alertDialog.show();
        }
    }
}
