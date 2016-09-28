package org.ei.opensrp.vaccinator.child;

import org.ei.opensrp.vaccinator.application.common.SmartClientRegisterFragment;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.template.DetailFragment;
import org.ei.opensrp.view.template.SmartRegisterSecuredActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildSmartRegisterActivity extends SmartRegisterSecuredActivity {

    @Override
    public SmartClientRegisterFragment makeBaseFragment() {
        return new ChildSmartRegisterFragment(new FormController(this));
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
    public String postFormSubmissionRecordFilterField() {
        return "existing_program_client_id";
    }

    @Override
    protected void onResumption() {

    }

    @Override
    public DetailFragment getDetailFragment() {
        return new ChildDetailFragment();
    }
}
