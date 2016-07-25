package org.ei.opensrp.vaccinator.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.application.common.BasicSearchOption;
import org.ei.opensrp.vaccinator.application.common.DateSort;
import org.ei.opensrp.vaccinator.application.common.StatusSort;
import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
import org.ei.opensrp.vaccinator.db.CESQLiteHelper;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Utils;
import util.barcode.Barcode;

import static util.Utils.getValue;

public abstract class SmartClientRegisterGroupFragment extends SmartRegisterFragment {
    private FormController formController1;

    CommonPersonObject householdMember;

    CommonPersonObject householdData;

    public CESQLiteHelper getClientEventDb() {
        return ceDb;
    }

    private CESQLiteHelper ceDb;

    public SmartClientRegisterGroupFragment(FormController formController) {
        super(formController);
        this.formController1 = formController;
    }

    @Override
    protected abstract SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() ;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ceDb = new CESQLiteHelper(activity);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                        //  new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"program_client_id",getResources().getString(R.string.child_id_sort))
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "first_name", getResources().getString(R.string.woman_alphabetical_sort)),
                        new DateSort(DateSort.ByColumnAndByDetails.byColumn, "Age", "dob"),
                        new StatusSort("Due Status"),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails, false, "program_client_id", getResources().getString(R.string.id_sort))
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.search_hint);
            }
        };
    }//end of method

    @Override
    protected abstract SmartRegisterClientsProvider clientsProvider() ;

    @Override
    protected abstract void onInitialization() ;

    @Override
    protected void onCreation() {

    }//end of method

    @Override
    protected void startRegistration() {
        Utils.providerDetails();
        // change the below contains value according to your requirement
        //if (!Utils.userRoles.contains("Vaccinator")) {
            Intent intent = new Intent(Barcode.BARCODE_INTENT);
            intent.putExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
            startActivityForResult(intent, Barcode.BARCODE_REQUEST_CODE);
        //}
    }//end of method

    protected abstract String getRegisterLabel();

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        updateSearchView();

        ((TextView)mView.findViewById(org.ei.opensrp.R.id.txt_title_label)).setText(getRegisterLabel());

        ((TextView)mView.findViewById(org.ei.opensrp.R.id.statusbar_today)).setText("Today: "+Utils.convertDateFormat(DateTime.now()));

        mView.findViewById(org.ei.opensrp.R.id.filter_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.service_mode_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.btn_report_month).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.village).setVisibility(View.GONE);

        ImageView imv = ((ImageView)mView.findViewById(org.ei.opensrp.R.id.register_client));
        imv.setImageResource(R.mipmap.qr_code);
        // create a matrix for the manipulation
        imv.setAdjustViewBounds(true);
        imv.setScaleType(ImageView.ScaleType.FIT_XY);
    }//end of method

    protected abstract String getRegistrationForm(HashMap<String, String> overridemap);

    protected abstract String getMemberRegistrationForm(HashMap<String, String> overridemap);

    protected abstract String getOAFollowupForm(Client client, HashMap<String, String> overridemap);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        org.ei.opensrp.util.Log.logDebug("Result Coode" + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            String qrcode = (String) extras.get(Barcode.SCAN_RESULT);

            onQRCodeSucessfullyScanned(qrcode);
        }
    }//end of the method

    private void startEnrollmentForm(HashMap<String, String> overrides){
        overrides.putAll(providerOverrides());
        formController1.startFormActivity(getRegistrationForm(overrides), null, new FieldOverrides(new JSONObject(overrides).toString()).getJSONString());
    }

    public void startFollowupForm(Client client, HashMap<String, String> overrides){
        overrides.putAll(providerOverrides());
        formController1.startFormActivity(getOAFollowupForm(client, overrides), client.getBaseEntityId(), new FieldOverrides(new JSONObject(overrides).toString()).getJSONString());
    }

    protected void startFollowupForm(String formName, SmartRegisterClient client, HashMap<String, String> overrideStringmap, ByColumnAndByDetails byColumnAndByDetails) {
        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            formController1.startFormActivity(formName, client.entityId(), null);
        } else {
            overrideStringmap.putAll(providerOverrides());

            String overrides = Utils.overridesToString(overrideStringmap, client, byColumnAndByDetails);
            FieldOverrides fieldOverrides = new FieldOverrides(overrides);
            org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides.getJSONString());
            formController1.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
        }
    }

    private void startNewMemberRegistrationForm( HashMap<String, String> overrides){
        overrides.putAll(providerOverrides());
        formController1.startFormActivity(getMemberRegistrationForm(overrides), null, new FieldOverrides(new JSONObject(overrides).toString()).getJSONString());
    }

    private void onQRCodeSucessfullyScanned(String qrCode) {
      /* #TODO:after reading the code , app first search for that id in database if he it is there , that client appears  on register only . if it doesnt then it shows two options
       */

        CommonPersonObjectClient client = null;
        SmartRegisterClients fc = getFilteredClients(qrCode);
        CommonPersonObject member = householdMember(qrCode);
        if(fc.size() > 0) {
            getSearchView().setText(qrCode);
        }
         else if(member != null){
            //SAFWAN
            getSearchView().setText(householdData.getColumnmaps().get("existing_household_id"));
            /*showMessageDialog("Member with scanned ID already exists under this Household Head", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });*/
            //SAFWAN
            Toast.makeText(getActivity(), "Member with scanned ID already exists under this Household Head",
                    Toast.LENGTH_LONG).show();
            return;
        }

        else {
            Client c = null;
            try{
                c = ceDb.getClient(qrCode);
            }
            catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            if(c != null && getRegisterLabel().toLowerCase().contains("woman")
                    && c.getBirthdate() != null && Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears() < 8){
                 showMessageDialog("Scanned ID already exists and is not a woman. Person is "+Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears()+" years only.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                return;
            }

            if(c != null && getRegisterLabel().toLowerCase().contains("child")
                    && c.getBirthdate() != null && Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears() >= 8){
                showMessageDialog("Scanned ID already exists and is not a child. Person is "+Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears()+" years", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                return;
            }

            client = HouseholdSmartRegisterFragment.client;

            HashMap<String,String> map = new HashMap<>();
            map.put("existing_program_client_id", qrCode);
            map.put("program_client_id", qrCode);

            if(client != null){

                //c.addRelationship("relation", client.getCaseId());

                map.put("relationalid",client.getCaseId());
                map.put("existing_first_name_hhh", getValue(client.getDetails(), "first_name_hhh", true));
                map.put("existing_last_name_hhh", getValue(client.getDetails(), "last_name_hhh", true));
                map.put("existing_household_id", getValue(client.getColumnmaps(), "existing_household_id", true));
                map.put("existing_address1", getValue(client.getDetails(), "adderss1", true));
                map.put("existing_union_councilname", getValue(client.getDetails(), "union_councilname", true));
                map.put("existing_townname", getValue(client.getDetails(), "townname", true));
                map.put("existing_city_villagename", getValue(client.getDetails(), "city_village", true));
                map.put("existing_provincename", getValue(client.getDetails(), "provincename", true));
                map.put("existing_landmark", getValue(client.getDetails(), "landmark", true));
            } else {
                map.put("existing_household_id", qrCode);
                map.put("household_id", qrCode);
            }
            Map<String, String> m = customFieldOverrides();
            if(m != null){
                map.putAll(m);
            }

            if (c != null){
                startFollowupForm(c, map);
            }
            else if (c == null && client == null) {
                startEnrollmentForm(map);
                HouseholdSmartRegisterFragment.client = null;
            } else {
                startNewMemberRegistrationForm(map);
                HouseholdSmartRegisterFragment.client = null;
            }
        }
    }

    protected abstract Map<String, String> customFieldOverrides();

    private SmartRegisterClients getFilteredClients(String filterString) {
        setCurrentSearchFilter(new BasicSearchOption(filterString));
        SmartRegisterClients filteredClients = getClientsAdapter().getListItemProvider()
                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
                        getCurrentSearchFilter(), getCurrentSortOption());
        return filteredClients;
    }//end of method

    public CommonPersonObject householdMember(String qrCode){
        String memberExistQuery = "select * from pkindividual where existing_program_client_id = " + qrCode;

        //CommonPersonObject memberObject = null;

        //String json = memberObject.getColumnmaps().get("details");

        List<CommonPersonObject> memberData = context.allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(memberExistQuery, new String[]{}, "pkindividual");
        if (memberData.size() < 1) {
            householdMember = null;
        } else {

            householdMember = memberData.get(0);
            String householdQuery = "select * from pkhousehold where existing_household_id = " + householdMember.getDetails().get("existing_household_id");
            householdData = context.allCommonsRepositoryobjects("pkhousehold").customQueryForCompleteRow(householdQuery, new String[]{}, "pkhousehold").get(0);
        }

        return householdMember;
    }


}