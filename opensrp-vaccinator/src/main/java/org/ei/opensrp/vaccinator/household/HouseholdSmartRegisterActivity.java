package org.ei.opensrp.vaccinator.household;

import org.ei.opensrp.vaccinator.fragment.HouseholdSmartRegisterFragment;
import org.ei.opensrp.vaccinator.fragment.SmartClientRegisterGroupFragment;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.ei.opensrp.view.template.SmartRegisterSecuredActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Safwan on 4/22/2016.
 */
public class HouseholdSmartRegisterActivity extends SmartRegisterSecuredActivity {
    @Override
    public SmartClientRegisterGroupFragment getBaseFragment() {
        return new HouseholdSmartRegisterFragment(new FormController(this));
    }

    @Override
    public SecuredFragment getProfileFragment() {
        return new HouseholdDetailFragment();
    }

    @Override
    protected String[] buildFormNameList() {
        List<String> formNames = new ArrayList<String>();
        formNames.add("family_registration");
        formNames.add("new_member_registration");
        formNames.add("new_member_registration_without_qr");
        //SAFWAN
        /*formNames.add("woman_followup");
        formNames.add("child_followup");*/


        return formNames.toArray(new String[formNames.size()]);
    }

    //SAFWAN
    public void showProfileView() {
        HouseholdDetailFragment profile = (HouseholdDetailFragment) findFragmentByPosition(1);
        profile.initialize();
        mPager.setCurrentItem(1, false);
    }

    @Override
    protected void onResumption() {

    }
}
