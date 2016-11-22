package org.ei.opensrp.vaccinator.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.domain.VaccineWrapper;
import org.ei.opensrp.view.template.DetailActivity;
import org.joda.time.DateTime;

import java.util.Calendar;

import util.DetailFormUtils;

@SuppressLint("ValidFragment")
public class VaccinationDialogFragment extends DialogFragment {
    private final Context context;
    private final VaccineWrapper tag;
    private VaccinationDialogListener listener;
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

                String formatedDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                updateJson(formatedDate);

                tag.setUpdatedVaccineDate(new DateTime(calendar.getTime()));

                listener.onVaccinateEarlier(tag);

         }
        });

        final Button vaccinateToday = (Button) dialogView.findViewById(R.id.vaccinate_today);
        vaccinateToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

                Calendar calendar = Calendar.getInstance();

                String formatedDate = DateFormatUtils.format(calendar, "yyyy-MM-dd");
                updateJson(formatedDate);
                listener.onVaccinateToday(tag);

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

    private void updateJson(String formatedDate) {
        String parent = "";
        if(tag.getVaccine().category().equals("child")){
            parent = "Child_Vaccination_Followup";
        }else if(tag.getVaccine().category().equals("woman")){
            parent = "Woman_TT_Followup_Form";
        }
        DetailFormUtils.updateJson(DetailActivity.formSubmission, parent, tag.getVaccine().name(), formatedDate);
        DetailFormUtils.updateJson(DetailActivity.formSubmission, parent, tag.getVaccine().name()+"_dose_today", "1");
    }

    public interface VaccinationDialogListener {
        public void onVaccinateToday(VaccineWrapper tag);
        public void onVaccinateEarlier(VaccineWrapper tag);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (VaccinationDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


}
