package org.ei.opensrp.vaccinator.fragment;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.vaccinator.db.Event;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static util.Utils.formatValue;
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

    private HashMap<String, String> getPreloadVaccineData(Event e) {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_tt1", formatValue(e.findObs(null, true, "tt1", "tt1_retro").getValue(), false));
        map.put("e_tt2", formatValue(e.findObs(null, true, "tt2", "tt2_retro").getValue(), false));
        map.put("e_tt3", formatValue(e.findObs(null, true, "tt3", "tt3_retro").getValue(), false));
        map.put("e_tt4", formatValue(e.findObs(null, true, "tt4", "tt4_retro").getValue(), false));
        map.put("e_tt5", formatValue(e.findObs(null, true, "tt5", "tt5_retro").getValue(), false));

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
        map.put("existing_address1", getValue(client.getDetails(), "address1", true));
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
        map.put("existing_birth_date", getValue(client.getDetails(), "dob", false));
        map.put("existing_calc_dob_confirm", getValue(client.getDetails(), "dob", false));
        map.put("existing_marriage", getValue(client.getDetails(), "marriage", true));
        map.put("existing_ethnicity", getValue(client.getDetails(), "ethnicity", true));
        map.put("existing_client_reg_date", getValue(client.getDetails(), "client_reg_date", false));
        map.put("existing_epi_card_number", getValue(client.getDetails(), "epi_card_number", false));
        map.put("existing_reminders_approval", getValue(client.getDetails(), "reminders_approval", false));
        map.put("existing_contact_phone_number", getValue(client.getDetails(), "contact_phone_number", false));

        map.putAll(getPreloadVaccineData(client));

        return map;
    }

    private Map<String, String> followupOverrides(Client client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_address1", client.getAddress("usual_residence").getAddressField("address1"));
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
        try{
            List<Event> el = getClientEventDb().getEvents(client.getBaseEntityId(), "Women TT enrollment", "eventDate DESC");

            if(el.size() > 0) {
                Event e = el.get(0);
                map.put("existing_father_name", formatValue(e.findObs(null, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));
                map.put("existing_husband_name", formatValue(e.findObs(null, true, "husband_name", "5617AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));
                map.put("existing_marriage", formatValue(e.findObs(null, true, "marriage").getValue(), true));
                map.put("existing_ethnicity", formatValue(e.findObs(null, true, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));
                map.put("existing_client_reg_date", formatValue(e.findObs(null, true, "client_reg_date", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));
                map.put("existing_epi_card_number", formatValue(e.findObs(null, true, "epi_card_number").getValue(), true));
                map.put("existing_reminders_approval", formatValue(e.findObs(null, true, "reminders_approval", "163089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), false));
                map.put("existing_contact_phone_number", formatValue(e.findObs(null, true, "contact_phone_number", "159635AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), false));

                map.putAll(getPreloadVaccineData(e));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return map;
    }
}