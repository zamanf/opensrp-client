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
import org.ei.opensrp.vaccinator.household.HouseholdSmartClientsProvider;
import org.ei.opensrp.vaccinator.woman.DetailActivity;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Safwan on 4/25/2016.
 */
public class HouseholdSmartRegisterFragment extends SmartClientRegisterFragment {

    private CommonPersonObjectController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private HouseholdSmartClientsProvider clientProvider;

    public HouseholdSmartRegisterFragment(){
        super(null);
    }

    public HouseholdSmartRegisterFragment(FormController formController) {
        super(formController);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(clientsProvider(), "Household", new int[]{
                        R.string.household_profile , R.string.household_members, R.string.household_address,
                        /*R.string.household_last_visit, R.string.household_due_date,*/
                }, new int[]{3,2,2});
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "first_name_hhh", getResources().getString(R.string.household_search_hint));
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.household_title);
            }
        };
    }//end of method

    @Override
    protected String getRegisterLabel() {
        return getResources().getString(R.string.household_register_title);
    }

    @Override
    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return "family_registration";
    }

    @Override
    protected String getOAFollowupForm(Client client, HashMap<String, String> overridemap) {
        return null;
    }

    @Override
    protected Map<String, String> customFieldOverrides() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new HouseholdSmartClientsProvider(
                    getActivity(), clientActionHandler, controller, context.alertService());
        }
        return clientProvider;
    }//end of method

    @Override
    protected void onInitialization() {
        if (controller == null) {
                controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("pkhousehold"),
                        context.allBeneficiaries(), context.listCache(),
                        context.personObjectClientsCache(), "first_name_hhh", "pkhousehold", "date",
                        CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails);
        }

        //context.formSubmissionRouter().getHandlerMap().put("woman_followup", new WomanFollowupHandler(new WomanService(context.allTimelineEvents(), context.allCommonsRepositoryobjects("pkwoman"), context.alertService())));
    }//end of method

    @Override
    protected void onCreation() { }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.household_profile_info_layout:
                    DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(), HouseholdDetailActivity.class);
                    getActivity().finish();
                    break;
                /*case R.id.woman_next_visit_holder:
                        HashMap<String, String> map = new HashMap<>();
                        CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                        map.putAll(followupOverrides(client));
                        startFollowupForm("woman_followup", (SmartRegisterClient) view.getTag(), map, ByColumnAndByDetails.byDefault);
                    break;*/
            }
        }
    }//end of method
}
