package org.ei.opensrp.vaccinator.woman;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectFilterOption;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;



import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.VillageController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.Tree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import util.ClientlessOpenFormOption;
import util.barcode.Barcode;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 13-Oct-15.
 */
public class WomanSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {
    private DefaultOptionsProvider defaultOptionProvider;
    private NavBarOptionsProvider navBarOptionsProvider;

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private  HashMap<String,String> overrides;


    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new WomanServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new WomanDateSort(WomanDateSort.ByColumnAndByDetails.byDetails,"client_dob_confirm");

            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.woman_title);
            }
        };
    }//end of method


    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();
                String locationjson = context.anmLocationController().get();
                LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);
                //locationTree.
                Map<String,TreeNode<String, Location>> locationMap =
                        locationTree.getLocationsHierarchy();
                addChildToList(dialogOptionslist,locationMap);
                DialogOption[] dialogOptions = new DialogOption[dialogOptionslist.size()];
                for (int i = 0;i < dialogOptionslist.size();i++){
                    dialogOptions[i] = dialogOptionslist.get(i);
                }

                return  dialogOptions;
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return  new DialogOption[]{
                      //  new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"program_client_id",getResources().getString(R.string.child_id_sort))

                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),

                        new WomanDateSort(WomanDateSort.ByColumnAndByDetails.byDetails,"client_dob_confirm"),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,false,"first_name",getResources().getString(R.string.woman_alphabetical_sort)),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"program_client_id",getResources().getString(R.string.woman_id_sort))

//""
//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.woman_search_hint) ;
            }
        };
    }//end of method

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new WomanSmartClientsProvider(
                    this,clientActionHandler , controller,context.alertService());
        }
        return clientProvider;
    }//end of method

    @Override
    protected void onInitialization() {
        if(controller==null) {
            controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("pkwoman"),
                    context.allBeneficiaries(), context.listCache(),
                    context.personObjectClientsCache(), "first_name", "pkwoman", "program_client_id",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails );

            }
        context.formSubmissionRouter().getHandlerMap().put("woman_followup_form",new WomanFollowupHandler(new WomanService(context.allTimelineEvents(), context.allCommonsRepositoryobjects("pkwoman"))));

        dialogOptionMapper = new DialogOptionMapper();
    }//end of method

    @Override
    protected void onCreation() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreation();
        setContentView( R.layout.smart_register_activity_customized);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        onInitialization();

        defaultOptionProvider = getDefaultOptionsProvider();
        navBarOptionsProvider = getNavBarOptionsProvider();

        setupViews();
    }//end of method

    @Override
    protected void startRegistration() {
        Intent intent = new Intent(Barcode.BARCODE_INTENT);
        intent.putExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        startActivityForResult(intent, Barcode.BARCODE_REQUEST_CODE);
    }//end of method

    @Override
    public void setupViews() {
        getDefaultOptionsProvider();
 //       getNavBarOptionsProvider();
        super.setupViews();

        setServiceModeViewDrawableRight(null);
        updateSearchView();
    }//end of method

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        updateSearchView();
    }//end of method




    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    // HouseHoldDetailActivity.householdclient = (CommonPersonObjectClient)view.getTag();
                    // Intent intent = new Intent(HouseHoldSmartRegisterActivity.this,HouseHoldDetailActivity.class);
                    //startActivity(intent);
                    // finish();
                    break;
                case R.id.woman_next_visit:
                    HashMap<String , String> map=new HashMap<String,String>();
                    map.put("provider_uc","korangi");
                    map.put("provider_town","korangi");
                    map.put("provider_city","karachi");
                    map.put("provider_province","sindh");
                  //  map.put("existing_program_client_id",view.getTag());
                    map.put("provider_location_id","korangi");
                    map.put("provider_location_name", "korangi");/*
                HashMap<String , String> map=new HashMap<String,String>();
                map.put("provider_uc",uc);
                map.put("provider_id","demotest");
                map.put("provider_town",town);
                map.put("provider_city",city);
                map.put("provider_province",province);
                map.put("existing_program_client_id",qrcode);
                map.put("provider_location_id",center);
                map.put("provider_location_name", center);*/
                    //map.put("","");
                    setOverrides(map);
                    startFollowupForms("woman_followup_form",(SmartRegisterClient)view.getTag(),map ,ByColumnAndByDetails.bydefault);

                  // showFragmentDialog(new EditDialogOptionModel(map), view.getTag());
                    break;
            }
        }

    }//end of method

    public void updateSearchView(){
        getSearchView().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(final CharSequence cs, int start, int before, int count) {
                (new AsyncTask() {
                    SmartRegisterClients filteredClients;

                    @Override
                    protected Object doInBackground(Object[] params) {
//                        currentSearchFilter =
                        setCurrentSearchFilter(new WomanSearchOption(cs.toString()));
                        filteredClients = getClientsAdapter().getListItemProvider()
                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
                                        getCurrentSearchFilter(), getCurrentSortOption());


                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
//                        clientsAdapter
//                                .refreshList(currentVillageFilter, currentServiceModeOption,
//                                        currentSearchFilter, currentSortOption);
                        getClientsAdapter().refreshClients(filteredClients);
                        getClientsAdapter().notifyDataSetChanged();
                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                        super.onPostExecute(o);
                    }
                }).execute();
//                currentSearchFilter = new HHSearchOption(cs.toString());
//                clientsAdapter
//                        .refreshList(currentVillageFilter, currentServiceModeOption,
//                                currentSearchFilter, currentSortOption);
//
//                searchCancelView.setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }//end of method

    private int getfilteredClients(String filterString){
        int i=0;
        setCurrentSearchFilter(new WomanSearchOption(filterString));
        SmartRegisterClients  filteredClients = getClientsAdapter().getListItemProvider()
                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
                        getCurrentSearchFilter(), getCurrentSortOption());
        i=filteredClients.size();

        return i;
    }//end of method

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {

            Bundle extras =data.getExtras();
            String qrcode= (String)    extras.get(Barcode.SCAN_RESULT);


        //    Toast.makeText(this, "QrCode is : " + qrcode, Toast.LENGTH_LONG).show();
       /*
       #TODO:after reading the code , app first search for that id in database if he it is there , that client appears  on register only . if it doesnt then it shows two options

       */
            //controller.getClients().
            org.ei.opensrp.util.Log.logDebug("ANM DETAILS"+context.anmController().get());
            String locationjson = context.anmLocationController().get();
            LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);
                 //   Log.d("ANM LOCATION : ", locationjson);


           /*String country= getLocationNameByAttribute(locationTree,"Country")!=null?getLocationNameByAttribute(locationTree,"Country"):"unkown";
            String province= getLocationNameByAttribute(locationTree,"province")!=null?getLocationNameByAttribute(locationTree,"province"):"unkown";
            String city= getLocationNameByAttribute(locationTree,"city")!=null?getLocationNameByAttribute(locationTree,"city"):"unkown";
            String town= getLocationNameByAttribute(locationTree,"town")!=null?getLocationNameByAttribute(locationTree,"town"):"unkown";
            String uc= getLocationNameByAttribute(locationTree,"uc")!=null?getLocationNameByAttribute(locationTree,"uc"):"unknown";

            String center= getLocationNameByAttribute(locationTree,"center")!=null?getLocationNameByAttribute(locationTree,"center"):"unknown";
*/
         /*   LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);
            //locationTree.
            Map<String,TreeNode<String, Location>> locationMap =
                    locationTree.getLocationsHierarchy();
            Collection<TreeNode<String,Location>> collection=locationMap.values();
           Iterator iterator=collection.iterator();
           while (iterator.hasNext()){
              Location location= (Location)iterator.next();
               location.getAttributes()

           }*/
           // for (String name: collection.){}
            //  locationMap.get("province")

              /*  for (String s : locationMap.keySet()){
                    TreeNode<String, Location> locations= locationMap.get(s);
                    for(locations.getChildren()){


                    }
                }*/

          /*   Log.d("location json label : ", locationMap.get("country").getLabel());
            Log.d("location json id: ", locationMap.get("country").getId());*/

            if(getfilteredClients(qrcode)<= 0){

                HashMap<String , String> map=new HashMap<String,String>();
                map.put("provider_uc","korangi");
                map.put("provider_town","korangi");
                map.put("provider_city","karachi");
                map.put("provider_province","sindh");
                map.put("existing_program_client_id",qrcode);
                map.put("provider_location_id","korangi");
                map.put("gender","female");
                map.put("provider_location_name", "korangi");/*
                HashMap<String , String> map=new HashMap<String,String>();
                map.put("provider_uc",uc);
                map.put("provider_id","demotest");
                map.put("provider_town",town);
                map.put("provider_city",city);
                map.put("provider_province",province);
                map.put("existing_program_client_id",qrcode);
                map.put("provider_location_id",center);
                map.put("provider_location_name", center);*/
                //map.put("","");
                setOverrides(map);

                //  map.put("provider_id", anmController.get());
                //  map.put("program_client_id",qrcode);
                //showFragmentDialog(new EditDialogOptionModel(getOverrides()));

               showFragmentDialog(new EditDialogOptionModel(map),null);
            }else {
                getSearchView().setText(qrcode);

            }


            //          controller.getClients();



        }


    }//end of the method


    private String getLocationNameByAttribute(   LocationTree locationTree  ,String tag){

           Log.d("ANM LOCATION : ", "in getLocationName Method");

        //locationTree.
        Map<String,TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();
        Collection<TreeNode<String,Location>> collection=locationMap.values();
        Iterator iterator=collection.iterator();
        while (iterator.hasNext()){
          TreeNode<String,Location> treeNode=(TreeNode < String, Location >)iterator.next();
            Location location= (Location)treeNode.getNode();

           // Location location= (Location)iterator.next();
            for (String s:  location.getTags())
            {

                if(s.equalsIgnoreCase(tag))
                {
                    Log.d("Amn Locations" ,location.getName() );
                  return   location.getName();
                }

                //location.getAttributes().get(s).toString().equalsIgnoreCase(attribute);

            }
        }
        Log.d("Amn Locations" ,"No location found");

        return null;
    }


    private class EditDialogOptionModel implements DialogOptionModel {
        private  HashMap<String,String> overrides1;

        public EditDialogOptionModel(HashMap<String,String> overrides1) {
            this.overrides1=overrides1;
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptions(this.overrides1);
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {

            //     Toast.makeText(ChildSmartRegisterActivity.this,option.name()+"", Toast.LENGTH_LONG).show();
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
    }

    private DialogOption[] getEditOptions( HashMap<String,String> overridemap ) {
     /*//  = new HashMap<String,String>();
        overridemap.put("existing_MWRA","MWRA");
        overridemap.put("existing_location", "existing_location");*/
        return new DialogOption[]{

                new ClientlessOpenFormOption("Enrollment", "woman_enrollment_form", formController,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.bydefault)
                //    new ClientlessOpenFormOption("Followup", "child_followup_fake_form", formController,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.byDetails)
        };
    }

    public HashMap<String,String> getOverrides() {
        return overrides;
    }//end of method
    public void setOverrides(HashMap<String,String>  overrides ){

        this.overrides=overrides;
    }//end of method

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist,Map<String,TreeNode<String, Location>> locationMap){
        for(Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if(entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist,entry.getValue().getChildren());

            }else{
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                Log.d("ANM Details", "location name :" + name);
                dialogOptionslist.add(new CommonObjectFilterOption(name.replace(" ", "_"), "location_name", CommonObjectFilterOption.ByColumnAndByDetails.byDetails, name));

            }
        }
    }

    public enum ByColumnAndByDetails{
        byColumn,byDetails,bydefault;
    }

    private void startFollowupForms(String formName,SmartRegisterClient client ,HashMap<String , String> overrideStringmap , ByColumnAndByDetails byColumnAndByDetails) {


        if(overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            formController.startFormActivity(formName, client.entityId(), null);
        }else{
            JSONObject overridejsonobject = new JSONObject();
            try {
                for (Map.Entry<String, String> entry : overrideStringmap.entrySet()) {
                    switch (byColumnAndByDetails){
                        case byDetails:
                            overridejsonobject.put(entry.getKey() , ((CommonPersonObjectClient)client).getDetails().get(entry.getValue()));
                            break;
                        case byColumn:
                            overridejsonobject.put(entry.getKey() , ((CommonPersonObjectClient)client).getColumnmaps().get(entry.getValue()));
                            break;
                        case bydefault:
                            overridejsonobject.put(entry.getKey() ,entry.getValue());
                            break;
                    }
                }
//                overridejsonobject.put("existing_MWRA", );
            }catch (Exception e){
                e.printStackTrace();
            }
            // org.ei.opensrp.util.Log.logDebug("overrides data is : " + overrideStringmap);
            FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());
            org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides.getJSONString());
            formController.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
        }
    }

}
