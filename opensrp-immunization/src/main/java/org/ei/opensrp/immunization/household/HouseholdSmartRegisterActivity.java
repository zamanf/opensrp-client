package org.ei.opensrp.immunization.household;

import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.template.DetailFragment;
import org.ei.opensrp.view.template.SmartRegisterSecuredActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Safwan on 4/22/2016.
 */
public class HouseholdSmartRegisterActivity extends SmartRegisterSecuredActivity {
    @Override
    public HouseholdSmartRegisterFragment makeBaseFragment() {
        return new HouseholdSmartRegisterFragment(new FormController(this));
    }

    @Override
    public DetailFragment getDetailFragment() {
        return new HouseholdDetailFragment();
    }

    @Override
    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("family_registration");
        formNames.add("new_member_registration");
        formNames.add("child_enrollment");
        formNames.add("woman_enrollment");

        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    public String postFormSubmissionRecordFilterField() {
        return "household_id";
    }

    @Override
    protected void onResumption() {

    }
}
