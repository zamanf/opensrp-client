package com.vijay.jsonwizard.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.RegexpValidator;
import com.rey.material.util.ViewUtil;
import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.customviews.GenericTextWatcher;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;
import com.vijay.jsonwizard.validators.edittext.RequiredValidator;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS;

/**
 * @author Jason Rogena - jrogena@ona.io
 * @since 25/01/2017
 */
public class DatePickerFactory implements FormWidgetFactory {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final String DATE_FORMAT_REGEX = "(^(((0[1-9]|1[0-9]|2[0-8])[-](0[1-9]|1[012]))|((29|30|31)[-](0[13578]|1[02]))|((29|30)[-](0[4,6,9]|11)))[-](19|[2-9][0-9])\\d\\d$)|(^29[-]02[-](19|[2-9][0-9])(00|04|08|12|16|20|24|28|32|36|40|44|48|52|56|60|64|68|72|76|80|84|88|92|96)$)";

    @Override
    public List<View> getViewsFromJson(String stepName, final Context context, JSONObject jsonObject,
                                       CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);
        try {
            String openMrsEntityParent = jsonObject.getString("openmrs_entity_parent");
            String openMrsEntity = jsonObject.getString("openmrs_entity");
            String openMrsEntityId = jsonObject.getString("openmrs_entity_id");

            final MaterialEditText editText = (MaterialEditText) LayoutInflater.from(context).inflate(
                    R.layout.item_edit_text, null);
            editText.setHint(jsonObject.getString("hint"));
            editText.setFloatingLabelText(jsonObject.getString("hint"));
            editText.setId(ViewUtil.generateViewId());
            editText.setTag(R.id.key, jsonObject.getString("key"));
            editText.setTag(R.id.openmrs_entity_parent, openMrsEntityParent);
            editText.setTag(R.id.openmrs_entity, openMrsEntity);
            editText.setTag(R.id.openmrs_entity_id, openMrsEntityId);
            if(jsonObject.has("v_required")) {
                JSONObject requiredObject = jsonObject.optJSONObject("v_required");
                String requiredValue = requiredObject.getString("value");
                if (!TextUtils.isEmpty(requiredValue)) {
                    if (Boolean.TRUE.toString().equalsIgnoreCase(requiredValue)) {
                        editText.addValidator(new RequiredValidator(requiredObject.getString("err")));
                    }
                }
            }

            if (!TextUtils.isEmpty(jsonObject.optString("value"))) {
                editText.setText(jsonObject.optString("value"));
                if (jsonObject.has("read_only")) {
                    boolean readOnly = jsonObject.getBoolean("read_only");
                    editText.setEnabled(!readOnly);
                }
            } else if(jsonObject.has("default")) {
                editText.setText(DATE_FORMAT.format(getDate(jsonObject.getString("default")).getTime()));
            }

            editText.addValidator(new RegexpValidator(
                    context.getResources().getString(R.string.badly_formed_date),
                    DATE_FORMAT_REGEX));

            Calendar date = getDate(editText.getText().toString());
            final android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog
                    (context, new android.app.DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar calendarDate = Calendar.getInstance();
                            calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            calendarDate.set(Calendar.MONTH, monthOfYear);
                            calendarDate.set(Calendar.YEAR, year);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB
                                    && calendarDate.getTimeInMillis() >= view.getMinDate()
                                    && calendarDate.getTimeInMillis() <= view.getMaxDate()) {
                                editText.setText(DATE_FORMAT.format(calendarDate.getTime()));
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                editText.setText("");
                            }
                        }
                    }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get
                            (Calendar.DAY_OF_MONTH));

            datePickerDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    InputMethodManager inputManager = (InputMethodManager) context
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(),
                            HIDE_NOT_ALWAYS);
                }
            });

            if (jsonObject.has("min_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Calendar minDate = getDate(jsonObject.getString("min_date"));
                minDate.set(Calendar.HOUR_OF_DAY, 0);
                minDate.set(Calendar.MINUTE, 0);
                minDate.set(Calendar.SECOND, 0);
                minDate.set(Calendar.MILLISECOND, 0);
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
            }

            if (jsonObject.has("max_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Calendar maxDate = getDate(jsonObject.getString("max_date"));
                maxDate.set(Calendar.HOUR_OF_DAY, 23);
                maxDate.set(Calendar.MINUTE, 59);
                maxDate.set(Calendar.SECOND, 59);
                maxDate.set(Calendar.MILLISECOND, 999);
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
            }

            if (jsonObject.has("expanded") && jsonObject.getBoolean("expanded") == true
                    && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePickerDialog.getDatePicker().setCalendarViewShown(true);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                datePickerDialog.getDatePicker().setCalendarViewShown(false);
            }

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus) {
                        showDatePickerDialog(context, datePickerDialog, editText);
                    }
                }
            });

            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDialog(context, datePickerDialog, editText);
                }
            });

            editText.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    editText.setText("");
                    return true;
                }
            });

            editText.addTextChangedListener(new GenericTextWatcher(stepName, editText));

            views.add(editText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return views;
    }

    private static void showDatePickerDialog(Context context,
                                             android.app.DatePickerDialog datePickerDialog,
                                             MaterialEditText editText) {
        Calendar date = getDate(editText.getText().toString());
        datePickerDialog.updateDate(date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setTitle("");
        datePickerDialog.show();
    }

    /**
     * This method returns a {@link Calendar} object at mid-day corresponding to a date matching
     * the format specified in {@code DATE_FORMAT} or a day in reference to today e.g today,
     * today-1, today+10
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

        //set time to mid-day
        calendarDate.set(Calendar.HOUR_OF_DAY, 12);
        calendarDate.set(Calendar.MINUTE, 0);
        calendarDate.set(Calendar.SECOND, 0);
        calendarDate.set(Calendar.MILLISECOND, 0);

        return calendarDate;
    }
}
