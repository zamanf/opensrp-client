package org.ei.opensrp.vaccinator.view;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.domain.VaccineWrapper;

@SuppressLint("ValidFragment")
public class UndoVaccinationDialogFragment extends DialogFragment {
    private final Context context;
    private final VaccineWrapper tag;
    public static final String DIALOG_TAG = "UndoVaccinationDialogFragment";

    private UndoVaccinationDialogFragment(Context context,
                                          VaccineWrapper tag) {
        this.context = context;
        this.tag = tag;
    }

    public static UndoVaccinationDialogFragment newInstance(
            Context context,
            VaccineWrapper tag) {
        return new UndoVaccinationDialogFragment(context, tag);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup dialogView = (ViewGroup) inflater.inflate(R.layout.undo_vaccination_dialog_view, container, false);
        TextView nameView = (TextView) dialogView.findViewById(R.id.name);
        nameView.setText(tag.getPatientName());
        TextView numberView = (TextView) dialogView.findViewById(R.id.number);
        numberView.setText(tag.getPatientNumber());

        TextView vaccineView = (TextView) dialogView.findViewById(R.id.vaccine);
        vaccineView.setText(tag.getVaccineAsString());

        Button vaccinateToday = (Button) dialogView.findViewById(R.id.yes_undo);
        vaccinateToday.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                Toast.makeText(context, "Yes", Toast.LENGTH_SHORT).show();
            }
        });

        Button cancel = (Button) dialogView.findViewById(R.id.no_go_back);
        cancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return dialogView;
    }
}
