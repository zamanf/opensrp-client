package org.ei.opensrp.vaccinator.child;

/*<<<<<<< HEAD
import android.os.Bundle;

import org.ei.opensrp.vaccinator.application.template.SmartRegisterActivity;
import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
=======*/
import android.os.Bundle;

import org.ei.opensrp.vaccinator.application.common.SmartClientRegisterFragment;
/*
>>>>>>> pk_vaccinator_merge
*/
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.template.SmartRegisterSecuredActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 13-Oct-15.
 */
/*<<<<<<< HEAD
public class ChildSmartRegisterActivity extends SmartRegisterActivity {
    //SAFWAN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChildSmartRegisterFragment.clientId = getIntent().getStringExtra("program_client_id");;
    }
=======*/
public class ChildSmartRegisterActivity extends SmartRegisterSecuredActivity {
    String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        id = getIntent().getStringExtra("program_client_id");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
        if(id != null){
            registerFragment.getSearchView().setText(id);
            registerFragment.onFilterManual(id);
        }
    }
/*
>>>>>>> pk_vaccinator_merge
*/
    @Override
    public SmartClientRegisterFragment getBaseFragment() {
        return new ChildSmartRegisterFragment(new FormController(this));
    }

    @Override
    public SecuredFragment getProfileFragment() {
        return null;
    }

    @Override
    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("child_enrollment");
        formNames.add("child_followup");
        formNames.add("offsite_child_followup");

        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onResumption() {

    }
}
