package org.ei.opensrp.vaccinator.fragment;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.vaccinator.woman.DetailActivity;
import org.ei.opensrp.vaccinator.woman.WomanDetailActivity;
import org.ei.opensrp.vaccinator.woman.WomanFollowupHandler;
import org.ei.opensrp.vaccinator.woman.WomanService;
import org.ei.opensrp.vaccinator.woman.WomanSmartClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.json.JSONException;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static util.Utils.getObsValue;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 05-Jan-16.
 */
public class WomanSmartRegisterFragment extends SmartClientRegisterFragment {
    private CommonPersonObjectController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private WomanSmartClientsProvider clientProvider;

    public WomanSmartRegisterFragment(){
        super(null);
    }

    public WomanSmartRegisterFragment(FormController formController) {
        super(formController);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(clientsProvider(), "TT", new int[]{
                        R.string.woman_profile , R.string.age, R.string.epi_number,
                        R.string.woman_edd, R.string.woman_last_vaccine, R.string.woman_next_vaacine
                }, new int[]{3,1,1,2,2,2});
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "first_name", getResources().getString(R.string.woman_alphabetical_sort));
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.woman_title);
            }
        };
    }//end of method

    @Override
    protected String getRegisterLabel() {
        return getResources().getString(R.string.woman_register_title);
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new WomanSmartClientsProvider(
                    getActivity(), clientActionHandler, controller, context.alertService());
        }
        return clientProvider;
    }//end of method

    @Override
    protected void onInitialization() {
        if (controller == null) {
            controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("pkwoman"),
                    context.allBeneficiaries(), context.listCache(),
                    context.personObjectClientsCache(), "first_name", "pkwoman", "client_reg_date",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails);
        }

        context.formSubmissionRouter().getHandlerMap().put("woman_followup", new WomanFollowupHandler(new WomanService(context.allTimelineEvents(), context.allCommonsRepositoryobjects("pkwoman"), context.alertService())));
    }//end of method

    @Override
    protected void onCreation() { }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.woman_profile_info_layout:
                    DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(), WomanDetailActivity.class);
                    getActivity().finish();
                    break;
                case R.id.woman_next_visit_holder:
                    HashMap<String, String> map = new HashMap<>();
                    CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                    map.putAll(followupOverrides(client));
                    startFollowupForm("woman_followup", (SmartRegisterClient) view.getTag(), map, ByColumnAndByDetails.byDefault);
                    break;
            }
        }
    }//end of method

    private HashMap<String, String> getPreloadVaccineData(CommonPersonObjectClient client) {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_tt1", nonEmptyValue(client.getColumnmaps(), true, false, "tt1", "tt1_retro"));
        map.put("e_tt2", nonEmptyValue(client.getColumnmaps(), true, false, "tt2", "tt2_retro"));
        map.put("e_tt3", nonEmptyValue(client.getColumnmaps(), true, false, "tt3", "tt3_retro"));
        map.put("e_tt4", nonEmptyValue(client.getColumnmaps(), true, false, "tt4", "tt4_retro"));
        map.put("e_tt5", nonEmptyValue(client.getColumnmaps(), true, false, "tt5", "tt5_retro"));

        return map;
    }

    private HashMap<String, String> getPreloadVaccineData(Client client) throws JSONException, ParseException {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_tt1", getObsValue(getClientEventDb(), client, true, "tt1", "tt1_retro"));
        map.put("e_tt2", getObsValue(getClientEventDb(), client, true, "tt2", "tt2_retro"));
        map.put("e_tt3", getObsValue(getClientEventDb(), client, true, "tt3", "tt3_retro"));
        map.put("e_tt4", getObsValue(getClientEventDb(), client, true, "tt4", "tt4_retro"));
        map.put("e_tt5", getObsValue(getClientEventDb(), client, true, "tt5", "tt5_retro"));

        return map;
    }

    @Override
    protected String getRegistrationForm(HashMap<String, String> overrides) {
        return "woman_enrollment";
    }

    @Override
    protected String getOAFollowupForm(Client client, HashMap<String, String> overridemap) {
        overridemap.putAll(followupOverrides(client));
        return "offsite_woman_followup";
    }

    @Override
    protected Map<String, String> customFieldOverrides() {
        Map<String, String> m = new HashMap<>();
        m.put("gender", "female");
        return m;
    }

    private Map<String, String> followupOverrides(CommonPersonObjectClient client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_house_number", getValue(client.getDetails(), "house_number", true));
        map.put("existing_street", getValue(client.getDetails(), "street", true));
        map.put("existing_union_council", getValue(client.getDetails(), "union_council", true));
        map.put("existing_town", getValue(client.getDetails(), "town", true));
        map.put("existing_city_village", getValue(client.getDetails(), "city_village", true));
        map.put("existing_province", getValue(client.getDetails(), "province", true));
        map.put("existing_landmark", getValue(client.getDetails(), "landmark", true));

        map.put("existing_union_councilname", getValue(client.getDetails(), "union_council", true));
        map.put("existing_townname", getValue(client.getDetails(), "town", true));
        map.put("existing_city_villagename", getValue(client.getDetails(), "city_village", true));
        map.put("existing_provincename", getValue(client.getDetails(), "province", true));

        map.put("existing_first_name", getValue(client.getDetails(), "first_name", true));
        map.put("existing_last_name", getValue(client.getDetails(), "last_name", true));
        map.put("existing_gender", getValue(client.getDetails(), "gender", true));
        map.put("existing_father_name", getValue(client.getDetails(), "father_name", true));
        map.put("existing_husband_name", getValue(client.getDetails(), "husband_name", true));
        map.put("existing_birth_date", getValue(client.getColumnmaps(), "dob", false));
        map.put("existing_epi_card_number", getValue(client.getDetails(), "epi_card_number", false));
        map.put("existing_marriage", getValue(client.getDetails(), "marriage", true));
        map.put("existing_ethnicity", getValue(client.getDetails(), "ethnicity", true));
        map.put("existing_client_reg_date", getValue(client.getDetails(), "client_reg_date", false));
        map.put("existing_reminders_approval", getValue(client.getDetails(), "reminders_approval", false));
        map.put("existing_contact_phone_number", getValue(client.getDetails(), "contact_phone_number", false));

        map.putAll(getPreloadVaccineData(client));

        return map;
    }

    private Map<String, String> followupOverrides(Client client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_house_number", client.getAddress("usual_residence").getAddressField("house_number"));
        map.put("existing_street", client.getAddress("usual_residence").getAddressField("street"));
        map.put("existing_union_council", client.getAddress("usual_residence").getSubTown());
        map.put("existing_town", client.getAddress("usual_residence").getTown());
        map.put("existing_city_village", client.getAddress("usual_residence").getCityVillage());
        map.put("existing_province", client.getAddress("usual_residence").getStateProvince());
        map.put("existing_landmark", client.getAddress("usual_residence").getAddressField("landmark"));

        map.put("existing_union_councilname", client.getAddress("usual_residence").getSubTown());
        map.put("existing_townname", client.getAddress("usual_residence").getTown());
        map.put("existing_city_villagename", client.getAddress("usual_residence").getCityVillage());
        map.put("existing_provincename", client.getAddress("usual_residence").getStateProvince());

        map.put("existing_first_name", client.getFirstName());
        map.put("existing_last_name", client.getLastName());
        map.put("existing_gender", client.getGender());
        map.put("existing_birth_date", client.getBirthdate().toString("yyyy-MM-dd"));
        Object epi = client.getAttribute("EPI Card Number");
        map.put("existing_epi_card_number", epi == null ? "" : epi.toString());

        try{
            map.put("existing_father_name", getObsValue(getClientEventDb(), client, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("existing_husband_name", getObsValue(getClientEventDb(), client, true, "husband_name", "5617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("existing_marriage", getObsValue(getClientEventDb(), client, true, "marriage"));
            map.put("existing_ethnicity", getObsValue(getClientEventDb(), client, true, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("existing_client_reg_date", getObsValue(getClientEventDb(), client, false, "client_reg_date", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("existing_reminders_approval", getObsValue(getClientEventDb(), client, true, "reminders_approval", "163089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));
            map.put("existing_contact_phone_number", getObsValue(getClientEventDb(), client, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"));

            map.putAll(getPreloadVaccineData(client));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }


}