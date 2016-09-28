package org.ei.opensrp.household.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.household.application.common.HouseholdSearchOption;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.repository.db.CESQLiteHelper;
import org.ei.opensrp.repository.db.Client;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.ei.opensrp.immunization.util.VaccinatorUtils;
import org.ei.opensrp.util.barcode.Barcode;
import org.ei.opensrp.util.barcode.BarcodeIntentIntegrator;
import org.ei.opensrp.util.barcode.BarcodeIntentResult;

import static org.ei.opensrp.util.Utils.getValue;

public abstract class SmartClientRegisterGroupFragment extends SecuredNativeSmartRegisterFragment {
   // private FormController formController1;

    //CommonPersonObject householdMember;

   // CommonPersonObject householdData;

   /* public CESQLiteHelper getClientEventDb() {
        return ceDb;
    }*/

    private CESQLiteHelper ceDb;

    public SmartClientRegisterGroupFragment(FormController formController) {
        super(formController);
       // this.formController1 = formController;
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
                        new CursorCommonObjectSort(getResources().getString(R.string.household_alphabetical_sort), "first_name_hhh"),
                        //new DateSort("Age", "calc_dob_hhh"),
                       // new StatusSort("Due Status"),
                        new CursorCommonObjectSort(getResources().getString(R.string.id_sort), "existing_household_id")
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
    // change the below contains value according to your requirement
        //if (!Utils.userRoles.contains("Vaccinator")) {
        BarcodeIntentIntegrator integ = new BarcodeIntentIntegrator(this);
        integ.addExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        integ.initiateScan();
        //}
    }//end of method

    @Override
    protected void onResumption() {
        super.onResumption();

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

    protected abstract String getMemberRegistrationFormWithoutQR(HashMap<String, String> overridemap);

    protected abstract String getRegistrationForm(HashMap<String, String> overridemap);

    protected abstract String getOAFollowupForm(Client client, HashMap<String, String> overridemap);

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("", "REQUEST COODE " + requestCode);
        Log.i("", "Result Coode " + resultCode);
        if(requestCode == BarcodeIntentIntegrator.REQUEST_CODE) {
            BarcodeIntentResult res = BarcodeIntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if(StringUtils.isNotBlank(res.getContents())) {
                onQRCodeSucessfullyScanned(res.getContents());
            }
            else Log.i("", "NO RESULT FOR QR CODE");
        }
    }//end of the method

    public void addMember(){

    }

    protected abstract String getMemberRegistrationForm(boolean qrCode);

    private void startEnrollmentForm(HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getRegistrationForm(overrides), "", overrides);
    }

    private void startOffSiteFollowupForm(Client client, HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getOAFollowupForm(client, overrides), client.getBaseEntityId(), overrides);
    }

    /*protected void startFollowupForm(String formName, SmartRegisterClient client, HashMap<String, String> overrideStringmap, org.ei.opensrp.ByColumnAndByDetails byColumnAndByDetails) {
        if (overrideStringmap == null) {
            org.ei.opensrp.org.ei.opensrp.immunization.util.Log.logDebug("overrides data is null");
            formController1.startFormActivity(formName, client.entityId(), null);
        } else {
            overrideStringmap.putAll(providerOverrides());

            String overrides = Utils.overridesToString(overrideStringmap, client, byColumnAndByDetails);
            FieldOverrides fieldOverrides = new FieldOverrides(overrides);
            org.ei.opensrp.org.ei.opensrp.immunization.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides.getJSONString());
            formController1.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
        }
    }*/

    public void startMemberRegistrationForm(boolean qrCode, HashMap<String, String> overrides){
        overrides.putAll(VaccinatorUtils.providerDetails());
        if(qrCode)
            startRegistration();
        else
            startForm(getMemberRegistrationFormWithoutQR(overrides), HouseholdSmartRegisterFragment.client.entityId(), overrides);

    }

    private void startNewMemberRegistrationForm(HashMap<String, String> overrides, CommonPersonObjectClient client){
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getMemberRegistrationForm(true), client.getCaseId(), overrides);
    }

    private void onQRCodeSucessfullyScanned(String qrCode) {
        CommonPersonObjectClient client = null;
        SmartRegisterClients fc = getFilteredClients(qrCode);

        CommonPersonObject child = child(qrCode);
        CommonPersonObject woman = woman(qrCode);

        CommonPersonObject member = householdMember(qrCode);
        if(fc.size() > 0) {
            getSearchView().setText(qrCode);
        }
        else if(member != null){
            //SAFWAN
            //TODO:
            //getSearchView().setText(member.getColumnmaps().get("existing_household_id"));
            /*showMessageDialog("Member with scanned ID already exists under this Household Head", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });*/
            //SAFWAN
            Toast.makeText(getActivity(), "Member with scanned ID already exists under this Household Head",
                    Toast.LENGTH_LONG).show();
            getSearchView().setText(member.getDetails().get("existing_household_id"));
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
            //TODO: Remove or edit
            if(c != null && getDefaultOptionsProvider().nameInShortFormForTitle().toLowerCase().contains("woman")
                    && c.getBirthdate() != null && Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears() < 8){
                 showMessageDialog("Scanned ID already exists and is not a woman. Person is "+Years.yearsBetween(c.getBirthdate(), DateTime.now()).getYears()+" years only.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                return;
            }

            if(c != null && getDefaultOptionsProvider().nameInShortFormForTitle().toLowerCase().contains("child")
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
            //map.put("existing_household_id", qrCode);
            map.put("household_id", qrCode);

            if(client != null){

                //c.addRelationship("relation", client.getCaseId());

                map.put("relationalid",client.getCaseId());
                map.put("existing_first_name_hhh", getValue(client.getColumnmaps(), "first_name_hhh", true));
                map.put("existing_last_name_hhh", getValue(client.getColumnmaps(), "last_name_hhh", true));
                map.put("existing_household_id", getValue(client.getColumnmaps(), "household_id", true));
                map.put("existing_address1", getValue(client.getColumnmaps(), "address1", true));
                map.put("existing_union_councilname", getValue(client.getColumnmaps(), "union_councilname", true));
                map.put("existing_townname", getValue(client.getColumnmaps(), "townname", true));
                map.put("existing_city_villagename", getValue(client.getColumnmaps(), "city_village", true));
                map.put("existing_provincename", getValue(client.getColumnmaps(), "provincename", true));
                map.put("existing_landmark", getValue(client.getColumnmaps(), "landmark", true));
                if(child != null){
                    map.put("first_name", getValue(child.getColumnmaps(), "first_name", true));
                    map.put("last_name", getValue(child.getColumnmaps(), "last_name", true));
                    map.put("member_birthdate", getValue(child.getColumnmaps(), "dob", true));
                    map.put("gender", getValue(child.getColumnmaps(), "gender", true));
                }

                if(woman != null){
                    map.put("first_name", getValue(woman.getColumnmaps(), "first_name", true));
                    map.put("last_name", getValue(woman.getColumnmaps(), "last_name", true));
                    map.put("member_birthdate", getValue(woman.getColumnmaps(), "dob", true));
                    map.put("gender", getValue(woman.getColumnmaps(), "gender", true));
                }


            } else {
                //map.put("existing_household_id", qrCode);
                map.put("household_id", qrCode);
            }
            Map<String, String> m = customFieldOverrides();
            if(m != null){
                map.putAll(m);
            }

            if (c != null){
                //startFollowupForm(c, map);
            }
            else if (c == null && client == null) {
                startEnrollmentForm(map);
                HouseholdSmartRegisterFragment.client = null;
            } else {
                startNewMemberRegistrationForm(map, client);
                HouseholdSmartRegisterFragment.client = null;
            }
        }
    }

    protected abstract Map<String, String> customFieldOverrides();

    private SmartRegisterClients getFilteredClients(String filterString) {
        setCurrentSearchFilter(new HouseholdSearchOption(filterString));
        onFilterManual(filterString);
        return getClientsAdapter().currentPageList();
    }//end of method

    public CommonPersonObject householdMember(String qrCode){
        String memberExistQuery = "select * from pkindividual where existing_program_client_id = " + qrCode;

        //CommonPersonObject memberObject = null;

        //String json = memberObject.getColumnmaps().get("details");

        List<CommonPersonObject> memberData = context.allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(memberExistQuery, new String[]{}, "pkindividual");
        CommonPersonObject householdMember;
        if (memberData.size() == 0) {
            return null;
        } else {
            householdMember = memberData.get(0);
            String householdId = memberData.get(0).getDetails().get("household_id");
            String householdQuery = "select * from pkhousehold where household_id = " + householdId;
            //CommonPersonObject householdData = context.allCommonsRepositoryobjects("pkhousehold").customQueryForCompleteRow(householdQuery, new String[]{}, "pkhousehold").get(0);
         //todo: create new search option
            setCurrentSearchFilter(new HouseholdSearchOption(householdId));
            onFilterManual(householdId);
        }

        return householdMember;
    }

    public CommonPersonObject woman(String qrCode){
        String womanExistQuery = "select * from pkwoman where program_client_id = " + qrCode;
        List<CommonPersonObject> memberData = context.allCommonsRepositoryobjects("pkwoman").customQueryForCompleteRow(womanExistQuery, new String[]{}, "pkwoman");
        CommonPersonObject woman;
        if (memberData.size() == 0) {
            return null;
        } else {
            woman = memberData.get(0);
        }

        return woman;
    }

    public CommonPersonObject child(String qrCode){
        String childExistQuery = "select * from pkchild where program_client_id = " + qrCode;
        List<CommonPersonObject> memberData = context.allCommonsRepositoryobjects("pkchild").customQueryForCompleteRow(childExistQuery, new String[]{}, "pkchild");
        CommonPersonObject child;
        if (memberData.size() == 0) {
            return null;
        } else {
            child = memberData.get(0);
        }

        return child;
    }


}