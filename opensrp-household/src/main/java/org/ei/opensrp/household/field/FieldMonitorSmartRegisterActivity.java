package org.ei.opensrp.household.field;

import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.template.SmartRegisterSecuredActivity;

import java.util.ArrayList;
import java.util.List;

public class FieldMonitorSmartRegisterActivity extends SmartRegisterSecuredActivity {

    @Override
    public SecuredNativeSmartRegisterFragment makeBaseFragment() {
        return new FieldMonitorRegisterFragment(new FormController(this));
    }

    @Override
    public SecuredFragment getProfileFragment() {
        return null;
    }

    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("vaccine_stock_position");

        return formNames.toArray(new String[formNames.size()]);
    }

    @Override
    protected void onResumption() {

    }
}
