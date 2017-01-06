package org.ei.opensrp.path.activity;

import org.ei.opensrp.path.fragment.SmartClientRegisterFragment;
import org.ei.opensrp.path.fragment.ChildSmartRegisterFragment;
import org.ei.opensrp.view.controller.FormController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildSmartRegisterActivity extends SmartRegisterSecuredActivity {

    @Override
    public SmartClientRegisterFragment getBaseFragment() {
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
    protected void onResumption() {

    }
}
