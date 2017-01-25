package com.vijay.jsonwizard.widgets;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.vijay.jsonwizard.R;
import com.vijay.jsonwizard.interfaces.CommonListener;
import com.vijay.jsonwizard.interfaces.FormWidgetFactory;

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

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public List<View> getViewsFromJson(String stepName, Context context, JSONObject jsonObject,
                                       CommonListener listener) throws Exception {
        List<View> views = new ArrayList<>(1);

        views.add(getTextViewWith(context, 16, jsonObject.getString("hint"),
                jsonObject.getString("key"), jsonObject.getString("type"),
                getLayoutParams(MATCH_PARENT, WRAP_CONTENT, 0, 0, 0, 0), FONT_BOLD_PATH));

        DatePicker datePicker = (DatePicker) LayoutInflater.from(context).inflate(
                R.layout.item_date_picker, null);
        datePicker.setTag(R.id.key, jsonObject.getString("key"));

        Calendar defaultCalendar = Calendar.getInstance();

        if (jsonObject.has("default")) {
            defaultCalendar = getDate(jsonObject.getString("default"));
        }

        datePicker.updateDate(
                defaultCalendar.get(Calendar.YEAR),
                defaultCalendar.get(Calendar.MONTH),
                defaultCalendar.get(Calendar.DAY_OF_MONTH));

        if (jsonObject.has("min_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePicker.setMinDate(getDate(jsonObject.getString("min_date"))
                    .getTimeInMillis());
        }

        if (jsonObject.has("max_date") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePicker.setMaxDate(getDate(jsonObject.getString("max_date"))
                    .getTimeInMillis());
        }

        if (jsonObject.has("expanded") && jsonObject.getBoolean("expanded") == true
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePicker.setCalendarViewShown(true);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            datePicker.setCalendarViewShown(false);
        }

        views.add(datePicker);

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

        if (dayString != null) {
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
}
