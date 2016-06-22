package org.ei.opensrp.vaccinator.fragment;

import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.controller.FormController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Safwan on 4/25/2016.
 */
public class HouseholdDetailFragment extends SmartRegisterFragment {
    private FormController householdFormController;

    public HouseholdDetailFragment(FormController formController) {
        super(formController);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void startRegistration() {

    }

    @Override
    protected String getRegisterLabel() {
        return "test reg lBE";
    }

    @Override
    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return null;
    }

    @Override
    protected String getOAFollowupForm(Client client, HashMap<String, String> overridemap) {
        return null;
    }

    @Override
    protected Map<String, String> customFieldOverrides() {
        return null;
    }

    //   private final ClientActionHandler clientActionHandler;

}
