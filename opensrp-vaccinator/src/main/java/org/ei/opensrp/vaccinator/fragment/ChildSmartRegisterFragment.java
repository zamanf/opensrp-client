package org.ei.opensrp.vaccinator.fragment;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.analytics.CountlyAnalytics;
import org.ei.opensrp.vaccinator.analytics.Events;
import org.ei.opensrp.vaccinator.child.ChildDetailActivity;
import org.ei.opensrp.vaccinator.child.ChildFollowupHandler;
import org.ei.opensrp.vaccinator.child.ChildService;
import org.ei.opensrp.vaccinator.child.ChildSmartClientsProvider;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.vaccinator.db.Event;
import org.ei.opensrp.vaccinator.woman.DetailActivity;
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
 * Created by Safwan on 2/12/2016.
 */
public class ChildSmartRegisterFragment extends SmartClientRegisterFragment {
    private SmartRegisterClientsProvider clientProvider = null;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private CommonPersonObjectController controller;

    public ChildSmartRegisterFragment(){   super(null);   }

    public ChildSmartRegisterFragment(FormController formController) {
        super(formController);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(clientsProvider(), "Vaccine", new int[]{
                        R.string.child_profile , R.string.birthdate_age, R.string.epi_number,
                        R.string.child_last_vaccine, R.string.child_next_vaacine
                }, new int[]{3,1,1,3,2});
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
                return Context.getInstance().getStringResource(R.string.child_title);
            }
        };
    }

    @Override
    protected String getRegisterLabel() {
        return getResources().getString(R.string.child_register_title);
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new ChildSmartClientsProvider(getActivity(), clientActionHandler, controller, context.alertService());
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {
        if (controller == null) {
            controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("pkchild"),
                    context.allBeneficiaries(), context.listCache(),
                    context.personObjectClientsCache(), "first_name", "pkchild", "client_reg_date",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails);

        }
        context.formSubmissionRouter().getHandlerMap().put("child_followup", new ChildFollowupHandler(new ChildService(context.allBeneficiaries(), context.allTimelineEvents(), context.allCommonsRepositoryobjects("pkchild"), context.alertService())));
    }

    @Override
    protected void onCreation() { }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.child_profile_info_layout:
                    DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(), ChildDetailActivity.class);
                    getActivity().finish();
                    break;
                case R.id.child_next_visit_holder:
                    HashMap<String, String> map = new HashMap<>();
                    CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                    map.putAll(followupOverrides(client));
                    map.putAll(providerOverrides());
                    startFollowupForm("child_followup", (SmartRegisterClient) view.getTag(), map, SmartRegisterFragment.ByColumnAndByDetails.byDefault);
                    break;
            }
        }
    }

    private HashMap<String, String> getPreloadVaccineData(CommonPersonObjectClient client) {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_bcg", nonEmptyValue(client.getColumnmaps(), true, false, "bcg", "bcg_retro"));
        map.put("e_opv0", nonEmptyValue(client.getColumnmaps(), true, false, "opv0", "opv0_retro"));
        map.put("e_penta1", nonEmptyValue(client.getColumnmaps(), true, false, "penta1", "penta1_retro"));
        map.put("e_opv1", nonEmptyValue(client.getColumnmaps(), true, false, "opv1", "opv1_retro"));
        map.put("e_pcv1", nonEmptyValue(client.getColumnmaps(), true, false, "pcv1", "pcv1_retro"));
        map.put("e_penta2", nonEmptyValue(client.getColumnmaps(), true, false, "penta2", "penta2_retro"));
        map.put("e_opv2", nonEmptyValue(client.getColumnmaps(), true, false, "opv2", "opv2_retro"));
        map.put("e_pcv2", nonEmptyValue(client.getColumnmaps(), true, false, "pcv2", "pcv2_retro"));
        map.put("e_penta3", nonEmptyValue(client.getColumnmaps(), true, false, "penta3", "penta3_retro"));
        map.put("e_opv3", nonEmptyValue(client.getColumnmaps(), true, false, "opv3", "opv3_retro"));
        map.put("e_pcv3", nonEmptyValue(client.getColumnmaps(), true, false, "pcv3", "pcv3_retro"));
        map.put("e_measles1", nonEmptyValue(client.getColumnmaps(), true, false, "measles1", "measles1_retro"));
        map.put("e_measles2", nonEmptyValue(client.getColumnmaps(), true, false, "measles2", "measles2_retro"));
        map.put("e_ipv", nonEmptyValue(client.getColumnmaps(), true, false, "ipv", "ipv_retro"));
        return map;
    }

    private HashMap<String, String> getPreloadVaccineData(Event e) {
        HashMap<String, String> map = new HashMap<>();
        map.put("e_bcg", formatValue(e.findObs(null, true, "bcg", "bcg_retro").getValue(), false));
        map.put("e_opv0", formatValue(e.findObs(null, true, "opv0", "opv0_retro").getValue(), false));
        map.put("e_penta1", formatValue(e.findObs(null, true, "penta1", "penta1_retro").getValue(), false));
        map.put("e_opv1", formatValue(e.findObs(null, true, "opv1", "opv1_retro").getValue(), false));
        map.put("e_pcv1", formatValue(e.findObs(null, true, "pcv1", "pcv1_retro").getValue(), false));
        map.put("e_penta2", formatValue(e.findObs(null, true, "penta2", "penta2_retro").getValue(), false));
        map.put("e_opv2", formatValue(e.findObs(null, true, "opv2", "opv2_retro").getValue(), false));
        map.put("e_pcv2", formatValue(e.findObs(null, true, "pcv2", "pcv2_retro").getValue(), false));
        map.put("e_penta3", formatValue(e.findObs(null, true, "penta3", "penta3_retro").getValue(), false));
        map.put("e_opv3", formatValue(e.findObs(null, true, "opv3", "opv3_retro").getValue(), false));
        map.put("e_pcv3", formatValue(e.findObs(null, true, "pcv3", "pcv3_retro").getValue(), false));
        map.put("e_measles1", formatValue(e.findObs(null, true, "measles1", "measles1_retro").getValue(), false));
        map.put("e_measles2", formatValue(e.findObs(null, true, "measles2", "measles2_retro").getValue(), false));
        map.put("e_ipv", formatValue(e.findObs(null, true, "ipv", "ipv_retro").getValue(), false));

        return map;
    }

    @Override
    protected String getRegistrationForm(HashMap<String, String> overrides) {
        return "child_enrollment";
    }

    @Override
    protected String getOAFollowupForm(Client client, HashMap<String, String> overridemap) {
        overridemap.putAll(followupOverrides(client));
        return "offsite_child_followup";
    }

    @Override
    protected Map<String, String> customFieldOverrides() {
        Map<String, String> m = new HashMap<>();
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
        map.put("existing_mother_name", getValue(client.getDetails(), "mother_name", true));
        map.put("existing_father_name", getValue(client.getDetails(), "father_name", true));
        map.put("existing_birth_date", getValue(client.getDetails(), "dob", false));
        map.put("existing_ethnicity", getValue(client.getDetails(), "ethnicity", true));
        map.put("existing_client_reg_date", getValue(client.getDetails(), "client_reg_date", false));
        map.put("existing_epi_card_number", getValue(client.getDetails(), "epi_card_number", false));
        map.put("existing_child_was_suffering_from_a_disease_at_birth", getValue(client.getDetails(), "child_was_suffering_from_a_disease_at_birth", true));
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

        try{
            List<Event> el = getClientEventDb().getEvents(client.getBaseEntityId(), "Child Vaccination enrollment", "eventDate DESC");

            if(el.size() > 0) {
                Event e = el.get(0);
                map.put("existing_father_name", formatValue(e.findObs(null, true, "father_name", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));
                map.put("existing_mother_name", formatValue(e.findObs(null, true, "mother_name", "1593AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));

                map.put("existing_marriage", formatValue(e.findObs(null, true, "marriage").getValue(), true));
                map.put("existing_ethnicity", formatValue(e.findObs(null, true, "ethnicity", "163153AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));
                map.put("existing_client_reg_date", formatValue(e.findObs(null, true, "client_reg_date", "1594AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), true));
                map.put("existing_epi_card_number", formatValue(e.findObs(null, true, "epi_card_number").getValue(), true));
                map.put("existing_child_was_suffering_from_a_disease_at_birth", formatValue(e.findObs(null, true, "existing_child_was_suffering_from_a_disease_at_birth", "159926AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA").getValue(), false));
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

    @Override
    protected void onStart(){
        super.onStart();
        HashMap<String,String> segments = new HashMap<String, String>();
        CountlyAnalytics.startAnalytics(this, Events.CHILD_REGISTER, segments);
    }

    @Override
    protected void onStop(){
        super.onStop();
        CountlyAnalytics.stopAnalytics();
    }
}
