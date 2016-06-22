package org.ei.opensrp.vaccinator.household;

import org.ei.opensrp.vaccinator.application.template.SmartRegisterActivity;
import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
import org.ei.opensrp.vaccinator.fragment.HouseholdSmartRegisterFragment;
import org.ei.opensrp.view.controller.FormController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Safwan on 4/22/2016.
 */
public class HouseholdSmartRegisterActivity extends SmartRegisterActivity {
    @Override
    protected SmartRegisterFragment getBaseFragment() {
        return new HouseholdSmartRegisterFragment(new FormController(this));
    }

    @Override
    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("family_registration");
        formNames.add("new_member_registration");
        formNames.add("new_member_registration_without_qr");

        return formNames.toArray(new String[formNames.size()]);
    }
}
