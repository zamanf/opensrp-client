package org.ei.opensrp.path.activity;

import org.ei.opensrp.path.fragment.FieldMonitorRegisterFragment;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;

import java.util.ArrayList;
import java.util.List;

public class FieldMonitorSmartRegisterActivity extends SmartRegisterSecuredActivity {

    @Override
    public SecuredNativeSmartRegisterFragment getBaseFragment() {
        return new FieldMonitorRegisterFragment(new FormController(this));
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