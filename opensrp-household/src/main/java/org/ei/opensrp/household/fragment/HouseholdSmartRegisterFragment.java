package org.ei.opensrp.household.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.cursoradapter.SmartRegisterCursorBuilder;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.household.application.common.HouseholdSearchOption;
import org.ei.opensrp.household.application.common.VaccinationServiceModeOption;
import org.ei.opensrp.household.household.HouseholdSmartRegisterActivity;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.repository.db.Client;
import org.ei.opensrp.household.household.HouseholdDetailFragment;
import org.ei.opensrp.household.household.HouseholdSmartClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

import java.util.HashMap;
import java.util.Map;

import static org.ei.opensrp.util.Utils.getValue;

/**
 * Created by Safwan on 4/25/2016.
 *
 */
public class HouseholdSmartRegisterFragment extends SmartClientRegisterGroupFragment {

    private CommonPersonObjectController controller;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private HouseholdSmartClientsProvider clientProvider;
    public static CommonPersonObjectClient client;


    public HouseholdSmartRegisterFragment(){
        super(null);
    }


    @SuppressLint("ValidFragment")
    public HouseholdSmartRegisterFragment(FormController householdFormController) {
        super(householdFormController);
    }

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        if(Utils.userRoles.contains("Vaccinator")){
            return new SmartRegisterPaginatedCursorAdapter(getActivity(),
                    new SmartRegisterCursorBuilder("event", null, (CursorSortOption) getDefaultOptionsProvider().sortOption())
                    , clientsProvider(), SmartRegisterCursorBuilder.DB.OPENSRP);
        } else {
            return new SmartRegisterPaginatedCursorAdapter(getActivity(),
                    new SmartRegisterCursorBuilder("pkhousehold", null, (CursorSortOption) getDefaultOptionsProvider().sortOption())
                    , clientsProvider(), SmartRegisterCursorBuilder.DB.DRISHTI);
        }

    }


    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public SearchFilterOption searchFilterOption() {
                return new HouseholdSearchOption("");
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new VaccinationServiceModeOption(null, "Household Register", new int[]{
                        R.string.household_profile , R.string.household_members, R.string.household_address, R.string.contactNumber, R.string.household_add_member
                        /*R.string.household_last_visit, R.string.household_due_date,*/
                }, new int[]{3,2,2,2,2});
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption() {
                return new CursorCommonObjectSort(getResources().getString(R.string.household_alphabetical_sort), "first_name_hhh");
            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.household_title);
            }
        };
    }//end of method

    //@Override
    protected String getRegisterLabel() {
        return getResources().getString(R.string.household_register_title);
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return new HouseholdSmartClientsProvider(getActivity(), clientActionHandler, context.alertService());
    }

    @Override
    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return "family_registration";
    }


    @Override
    protected String getOAFollowupForm(Client client, HashMap<String, String> overridemap) {
        return "";
    }

    @Override
    protected String getMemberRegistrationForm(boolean qrCode) {
        if(qrCode)
            return "new_member_registration";
        else
            return "new_member_registration_without_qr";
    }

    @Override
    protected String getMemberRegistrationFormWithoutQR(HashMap<String, String> overridemap) {
        return "new_member_registration_without_qr";
    }

    @Override
    protected Map<String, String> customFieldOverrides() {
        return null;
    }

    /*@Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new HouseholdSmartClientsProvider(
                    getActivity(), clientActionHandler, controller, context.alertService());
        }
        return clientProvider;
    }//end of method*/

    @Override
    protected void onInitialization() {
       /* if (controller == null) {
                controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("pkhousehold"),
                        context.allBeneficiaries(), context.listCache(),
                        context.personObjectClientsCache(), "first_name_hhh", "pkhousehold", "first_name_hhh",
                        CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails);
        }*/

       //todo context.formSubmissionRouter().getHandlerMap().put("new_member_registration", new HouseholdFollowupHandler(new HouseholdService(context.allTimelineEvents(), context.allCommonsRepositoryobjects("pkindividual"), context.alertService())));
    }//end of method

    @Override
    protected void onCreation() { }


    private class ClientActionHandler implements View.OnClickListener {
        private HouseholdSmartRegisterFragment householdSmartRegisterFragment;

        public ClientActionHandler() {
            this.householdSmartRegisterFragment = householdSmartRegisterFragment;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.household_profile_info_layout:
                    //SAFWAN
                    HouseholdDetailFragment.householdClient = (CommonPersonObjectClient) view.getTag();
                    ((HouseholdSmartRegisterActivity)getActivity()).showProfileView();
                    /*Intent intent = new Intent((HouseholdSmartRegisterActivity) getActivity(), HouseholdDetailFragment.class);
                    startActivity(intent);
                    getActivity().finish();*/
                    //((HouseholdSmartRegisterActivity)getActivity()).startRegistration();
                    break;
                case R.id.household_add_member:
                    Utils.providerDetails();
                    // change the below contains value according to your requirement
                    //if(!Utils.userRoles.contains("Vaccinator")) {
                        client = (CommonPersonObjectClient) view.getTag();
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                        LinearLayout ly = new LinearLayout(getActivity());
                        ly.setOrientation(LinearLayout.VERTICAL);
                        final RadioButton hasQRCode = new RadioButton(getActivity());
                        final RadioButton noQRCode = new RadioButton(getActivity());
                        RadioGroup rG = new RadioGroup(getActivity());
                        hasQRCode.setText("Yes");
                        noQRCode.setText("No");
                        final LinearLayout layout = new LinearLayout(getActivity());


                        layout.setOrientation(LinearLayout.HORIZONTAL);
                        TextView memberCodeQuestion = new TextView(getActivity());
                        memberCodeQuestion.setText("Have you ever been registered in any other OpenSRP program and assigned a QR code?");
                        memberCodeQuestion.setTextSize(20);
                        layout.addView(memberCodeQuestion);

                        rG.addView(hasQRCode);
                        rG.addView(noQRCode);
                        ly.addView(layout);
                        ly.addView(rG);

                        builder.setView(ly);

                        final AlertDialog alertDialog = builder.setPositiveButton("OK", null).setNegativeButton("Cancel", null).create();
                        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                            @Override
                            public void onShow(final DialogInterface dialog) {
                                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                b.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View view) {
                                        HashMap<String, String> map = new HashMap<>();
                                        map.putAll(followupOverrides(client));
                                        if (noQRCode.isChecked()) {
                                            startMemberRegistrationForm(false, map);
                                            //startFollowupForm("new_member_registration_without_qr", client, map, org.ei.opensrp.ByColumnAndByDetails.byDefault);
                                        } else if (hasQRCode.isChecked()) {
                                            startMemberRegistrationForm(true, map);
                                            //startRegistration();
                                        }
                                        dialog.dismiss();
                                    }
                                });

                            }
                        });
                        alertDialog.show();
                    //}

                    break;

            }
        }
    }//end of method

    private Map<String, String> followupOverrides(CommonPersonObjectClient client){
        Map<String, String> map = new HashMap<>();

        map.put("relationalid", client.getCaseId());
        map.put("existing_first_name_hhh", getValue(client.getColumnmaps(), "first_name_hhh", true));
        map.put("existing_last_name_hhh", getValue(client.getColumnmaps(), "last_name_hhh", true));
        map.put("existing_household_id", getValue(client.getColumnmaps(), "household_id", true));
        map.put("existing_union_councilname", getValue(client.getColumnmaps(), "union_councilname", true));
        map.put("existing_townname", getValue(client.getColumnmaps(), "townname", true));
        map.put("existing_city_villagename", getValue(client.getColumnmaps(), "city_village", true));
        map.put("existing_provincename", getValue(client.getColumnmaps(), "provincename", true));
        map.put("existing_landmark", getValue(client.getColumnmaps(), "landmark", true));
        map.put("existing_address1", getValue(client.getColumnmaps(), "address1", true));

        return map;
    }



}
