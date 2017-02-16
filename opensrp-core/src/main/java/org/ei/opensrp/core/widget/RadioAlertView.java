package org.ei.opensrp.core.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.core.R;
import org.ei.opensrp.core.utils.Utils;

import java.util.Map;

/**
 * Created by Maimoona on 1/12/2017.
 */

public class RadioAlertView {
    private final AlertDialog dialog;
    private final RadioGroup radioGroup;
    private final AlertDialog.Builder builder;
    private final Map<String, View.OnClickListener> radioListenerMap;

    private Button getPositiveButton(){
        return dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    }

    public RadioAlertView(@NonNull Context context, String label, String message, String goButtonText, String cancelButtonText, final Map<String, View.OnClickListener> radioLabelClickListenerMap) {
        builder = new AlertDialog.Builder(context);

        this.radioListenerMap = radioLabelClickListenerMap;

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(20, 10, 10, 5);

        TextView question = new TextView(context);
        question.setText(message);
        question.setTextColor(Utils.getColor(context, R.color.text_black));
        question.setTextSize(18);

        layout.addView(question);

        radioGroup = new RadioGroup(context);
        radioGroup.setPadding(10, 10, 10, 5);

        for (String radioText: radioLabelClickListenerMap.keySet()) {
            RadioButton rb = new RadioButton(context);
            rb.setText(radioText);
            radioGroup.addView(rb);
        }

        layout.addView(radioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                getPositiveButton().setEnabled(true);
            }
        });

        builder.setView(layout);

        builder.setPositiveButton(goButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int checked = radioGroup.getCheckedRadioButtonId();
                if (checked > -1){
                    String radio = ((RadioButton)radioGroup.findViewById(checked)).getText().toString();
                    radioListenerMap.get(radio).onClick(radioGroup.findViewById(checked));
                }
            }
        });

        builder.setNegativeButton(cancelButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.create();
    }

    public void show() {
        dialog.show();
        radioGroup.clearCheck();
        getPositiveButton().setEnabled(false);
    }
}
