package org.ei.opensrp.vaccinator.woman;

import org.ei.opensrp.vaccinator.fragment.SmartRegisterActivity;
import org.ei.opensrp.vaccinator.fragment.SmartRegisterFragment;
import org.ei.opensrp.vaccinator.fragment.WomanSmartRegisterFragment;
import org.ei.opensrp.view.controller.FormController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 13-Oct-15.
 */
public class WomanSmartRegisterActivity extends SmartRegisterActivity {

    @Override
    protected SmartRegisterFragment getBaseFragment() {
        return new WomanSmartRegisterFragment(new FormController(this));
    }

    protected String[] buildFormNameList(){
        List<String> formNames = new ArrayList<String>();
        formNames.add("woman_enrollment");
        formNames.add("woman_followup");
        formNames.add("offsite_woman_followup");

        return formNames.toArray(new String[formNames.size()]);
    }

}
