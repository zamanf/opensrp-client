package org.ei.opensrp.vaccinator.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.domain.VaccineWrapper;
import org.joda.time.DateTime;

import java.util.Calendar;

@SuppressLint("ValidFragment")
public class VaccinationDialogFragment extends DialogFragment {
    private final Context context;
    private final VaccineWrapper tag;
    public static final String DIALOG_TAG = "VaccinationDialogFragment";

    private VaccinationDialogFragment(Context context,
                                      VaccineWrapper tag) {
        this.context = context;
        this.tag = tag;
    }

    public static VaccinationDialogFragment newInstance(
            Context context,
            VaccineWrapper tag) {
        return new VaccinationDialogFragment(context, tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.vaccination_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.name);
        nameView.setText(tag.getPatientName());
        TextView numberView = (TextView) dialogView.findViewById(R.id.number);
        numberView.setText(tag.getPatientNumber());

        final TextView vaccineView = (TextView) dialogView.findViewById(R.id.vaccine);
        vaccineView.setText(tag.getVaccineAsString());

        final DatePicker earlierDatePicker = (DatePicker) dialogView.findViewById(R.id.earlier_date_picker);

        final Button set = (Button) dialogView.findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dismiss();

                int day = earlierDatePicker.getDayOfMonth();
                int month = earlierDatePicker.getMonth();
                int year =  earlierDatePicker.getYear();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                tag.setUpdatedVaccineDate(new DateTime(calendar.getTimeInMillis()));
         }
        });

        final Button vaccinateToday = (Button) dialogView.findViewById(R.id.vaccinate_today);
        vaccinateToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                Calendar calendar = Calendar.getInstance();

                tag.setUpdatedVaccineDate(new DateTime(calendar.getTimeInMillis()));
            }
        });

        final Button vaccinateEarlier = (Button) dialogView.findViewById(R.id.vaccinate_earlier);
        vaccinateEarlier.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                vaccinateEarlier.setVisibility(View.GONE);
                earlierDatePicker.setVisibility(View.VISIBLE);
                set.setVisibility(View.VISIBLE);
            }
        });

        Button cancel = (Button) dialogView.findViewById(R.id.cancel);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        return dialogView;
    }
}
