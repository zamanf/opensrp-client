package org.ei.opensrp.vaccinator.woman;

import org.ei.opensrp.vaccinator.fragment.HouseholdDetailFragment;
import org.ei.opensrp.vaccinator.fragment.SmartClientRegisterFragment;
import org.ei.opensrp.vaccinator.fragment.SmartRegisterActivity;
import org.ei.opensrp.vaccinator.fragment.SmartRegisterFragment;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class DetailMultiDimActivity extends SmartRegisterActivity {
    @Override
    protected SmartRegisterFragment getBaseFragment() {
        return new HouseholdDetailFragment(new FormController(this));
    }

    @Override
    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<>();
        formNames.add("new_member_registration");

        return formNames.toArray(new String[formNames.size()]);
    }
}
