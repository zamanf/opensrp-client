package org.ei.opensrp.vaccinator.woman;

import android.content.Intent;
import android.os.Bundle;

import org.ei.opensrp.vaccinator.application.template.SmartRegisterActivity;
import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
import org.ei.opensrp.view.controller.FormController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 13-Oct-15.
 */
public class WomanSmartRegisterActivity extends SmartRegisterActivity {
    //SAFWAN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WomanSmartRegisterFragment.clientId = getIntent().getStringExtra("program_client_id");;
    }

    @Override
    protected SmartRegisterFragment getBaseFragment() {
        return new WomanSmartRegisterFragment(new FormController(this));
    }

    public String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add("woman_enrollment");
        formNames.add("woman_followup");
        formNames.add("offsite_woman_followup");

        return formNames.toArray(new String[formNames.size()]);
    }

}
