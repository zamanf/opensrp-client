package org.ei.opensrp.vaccinator.woman;

import android.os.Bundle;

import org.ei.opensrp.view.fragment.SecuredFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;


import org.ei.opensrp.vaccinator.application.common.SmartClientRegisterFragment;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.template.SmartRegisterSecuredActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 13-Oct-15.
 */

public class WomanSmartRegisterActivity extends SmartRegisterSecuredActivity {
    //SAFWAN
    String id;
    boolean firstTime = true;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("program_client_id");


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(firstTime && id != null) {
            firstTime = false;
            SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
            registerFragment.getSearchView().setText(id);
            registerFragment.onFilterManual(id);
        }
    }

    @Override
    public SmartClientRegisterFragment getBaseFragment() {
        return new WomanSmartRegisterFragment(new FormController(this));
    }

    @Override
    public SecuredFragment getProfileFragment() {
        return null;
    }

    public String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add("woman_enrollment");
        formNames.add("woman_followup");
        formNames.add("offsite_woman_followup");

        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onResumption() {

    }
}
