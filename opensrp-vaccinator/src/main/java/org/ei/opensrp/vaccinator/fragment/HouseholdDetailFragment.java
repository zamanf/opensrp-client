package org.ei.opensrp.vaccinator.fragment;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.vaccinator.household.HouseholdDetailActivity;
import org.ei.opensrp.vaccinator.household.HouseholdFollowupHandler;
import org.ei.opensrp.vaccinator.household.HouseholdService;
import org.ei.opensrp.vaccinator.household.HouseholdSmartClientsProvider;
import org.ei.opensrp.vaccinator.woman.DetailActivity;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.fragment.SecuredFragment;

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
