package org.ei.opensrp.vaccinator.field;

import org.ei.opensrp.vaccinator.fragment.FieldMonitorRegisterFragment;
import org.ei.opensrp.vaccinator.fragment.SmartRegisterActivity;
import org.ei.opensrp.vaccinator.fragment.SmartRegisterFragment;
import org.ei.opensrp.view.controller.FormController;

import java.util.ArrayList;
import java.util.List;

public class FieldMonitorSmartRegisterActivity extends SmartRegisterActivity {
    @Override
    protected SmartRegisterFragment getBaseFragment() {
        return new FieldMonitorRegisterFragment(new FormController(this));
    }

    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("vaccine_stock_position");

        return formNames.toArray(new String[formNames.size()]);
    }
}
