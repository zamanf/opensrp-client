package org.ei.opensrp.vaccinator.child;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonObjectFilterOption;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ECClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.controller.VillageController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.ChildAgeSort;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.EditOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.LocationSelectorDialogFragment;
import org.ei.opensrp.view.dialog.OpenFormOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SmartRegisterDialogFragment;
import org.ei.opensrp.view.dialog.SortOption;
import org.json.JSONObject;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import util.ClientlessOpenFormOption;
import util.barcode.Barcode;
import util.barcode.BarcodeIntentResult;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {

    private DefaultOptionsProvider defaultOptionProvider;
    private NavBarOptionsProvider navBarOptionsProvider;

    private SmartRegisterClientsProvider clientProvider = null;
    private CommonPersonObjectController controller;
    private VillageController villageController;
    private DialogOptionMapper dialogOptionMapper;
//    private final FormController formController;
private  HashMap<String,String> overrides;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new ChildServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new ChildDateSort(ChildDateSort.ByColumnAndByDetails.byDetails,"first_name");

            }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.child_title);
            }
        };
    }

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
                //return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
//                        new HouseholdCensusDueDateSort(),

                       // new ChildAgeSort(),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,false,"first_name",getResources().getString(R.string.child_alphabetical_sort)),
                        new CommonObjectSort(CommonObjectSort.ByColumnAndByDetails.byDetails,true,"program_client_id",getResources().getString(R.string.child_id_sort))

//""
//                        new CommonObjectSort(true,false,true,"age")
                };
            }



            @Override
            public String searchHint() {
                return getResources().getString(R.string.child_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if (clientProvider == null) {
            clientProvider = new ChildSmartClientsProvider(
                    this,clientActionHandler , controller,context.alertService());
        }
        return clientProvider;
    }


    @Override
    protected void onCreation() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreation();
        setContentView(R.layout.smart_register_activity_customized);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        onInitialization();

        defaultOptionProvider = getDefaultOptionsProvider();
        navBarOptionsProvider = getNavBarOptionsProvider();

        setupViews();
    }


    @Override
    protected void onInitialization() {
             //   context.allCommonsRepositoryobjects(
        //context.
      // AllCommonsRepository commonRepo=context.allCommonsRepositoryobjects("child");
            //    new CommonPersonObjectController()
   /*         controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("vaccine_child"),
                    context.allBeneficiaries(), context.listCache(),
                    context.personObjectClientsCache(), "first_name", "vaccine_child","type","child",
                    CommonPersonObjectController.ByColumnAndByDetails.byColumn
                    , "child_reg_date",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails );*/
       controller= new CommonPersonObjectController(context.allCommonsRepositoryobjects("client"),
                context.allBeneficiaries(), context.listCache(),
                context.personObjectClientsCache(), "first_name", "client", "child_reg_date",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails );
              //Log.d("Child count :", context.commonrepository("vaccine_child").count() + "");

                //context.
         /*   controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("elco"),
                    context.allBeneficiaries(), context.listCache(),
                    context.personObjectClientsCache(),
                    "FWWOMFNAME","elco","FWELIGIBLE","1",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails,
                    "FWWOMFNAME", CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                    new ElcoPSRFDueDateSort());
*/
        String locationjson = context.anmLocationController().get();
        Log.d("ANM LOCATION : ", locationjson);

        LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);
//      Location l=  locationTree.findLocation("Country");



        Map<String,TreeNode<String, Location>> locationMap =
                locationTree.getLocationsHierarchy();
        Log.d("location json label : ", locationMap.entrySet().toString());
     //   Log.d("location json id: ", locationMap.get("country").getId());

    //    Log.d("ANM LOCATION JSON : ", l.getName());
        context.formSubmissionRouter().getHandlerMap().put("child_followup_form",new ChildFollowupHandler(new ChildService(context.allBeneficiaries(),context.allTimelineEvents())));
        dialogOptionMapper = new DialogOptionMapper();

    }

    @Override
    protected void startRegistration() {
        //public static
        //final String    BARCODE_INTENT= "com.google.zxing.client.android.SCAN";
        Intent intent = new Intent(Barcode.BARCODE_INTENT);
        intent.putExtra(Barcode.SCAN_MODE, Barcode.QR_MODE);
        startActivityForResult(intent, Barcode.BARCODE_REQUEST_CODE);


        //FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());
        //formController.startFormActivity("child_enrollment_form",);
       // new OpenFormOption("Child Enrollment Form", "child_enrollment_form", formController,overridemap, OpenFormOption.ByColumnAndByDetails.byDetails);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
            if(resultCode==RESULT_OK)
            {

                Bundle extras =data.getExtras();
                String qrcode= (String)    extras.get(Barcode.SCAN_RESULT);


                Toast.makeText(this ,"QrCode is : "+qrcode, Toast.LENGTH_LONG).show();
       /*
       #TODO:after reading the code , app first search for that id in database if he it is there , that client appears  on register only . if it doesnt then it shows two options

       */
                //controller.getClients().
                String locationjson = context.anmLocationController().get();
              //  Log.d("ANM LOCATION : ", locationjson);
                LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);
                //locationTree.
                Map<String,TreeNode<String, Location>> locationMap =
                        locationTree.getLocationsHierarchy();
              //  locationMap.get("province")

                for (String s : locationMap.keySet()){
                    TreeNode<String, Location> locations= locationMap.get(s);
                   if(locations.getChildren()==null) {

                    Log.d("Location Label",locations.getLabel());
                   }
                }
            //    addChildToList(locationMap);

               // Log.d("location json : ", locationjson);

               // Log.d("location json label : ", locationMap.get("country").getLabel());
             //   Log.d("location json id: ", locationMap.get("country").getId());
             if(getfilteredClients(qrcode)<= 0){
                    HashMap<String , String> map=new HashMap<String,String>();
                   map.put("provider_uc","korangi");
                    map.put("provider_town","korangi");
                    map.put("provider_city","karachi");
                    map.put("provider_province","sindh");
                    map.put("existing_program_client_id",qrcode);
                    map.put("provider_location_id","korangi");
                    map.put("provider_location_name", "korangi");
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


    }





    private DialogOption[] getEditOptions( HashMap<String,String> overridemap ) {
     /*//  = new HashMap<String,String>();
        overridemap.put("existing_MWRA","MWRA");
        overridemap.put("existing_location", "existing_location");*/
        return new DialogOption[]{

                new ClientlessOpenFormOption("Enrollment", "child_enrollment_form", formController,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.bydefault),
            //    new ClientlessOpenFormOption("Followup", "child_followup_fake_form", formController,overridemap, ClientlessOpenFormOption.ByColumnAndByDetails.byDetails)
        };
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
    public HashMap<String,String> getOverrides() {
        return overrides;
    }
    public void setOverrides(HashMap<String,String>  overrides ){

        this.overrides=overrides;
    }




    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        updateSearchView();
    }

    private class ClientActionHandler implements View.OnClickListener {

        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.child_profilepic:

                    ChildDetailActivity.childclient=(CommonPersonObjectClient)view.getTag();
                    Intent intent =new Intent(ChildSmartRegisterActivity.this,ChildDetailActivity.class);
                    startActivity(intent);
                    finish();
                   // HouseHoldDetailActivity.householdclient = (CommonPersonObjectClient)view.getTag();
                   // Intent intent = new Intent(HouseHoldSmartRegisterActivity.this,HouseHoldDetailActivity.class);
                    //startActivity(intent);
                   // finish();
                    break;
                case R.id.child_next_visit_holder:
                //    formController.startFormActivity("child_followup_form",view.getTag());
                 //   view.getTag().
                 //   Log.d("child :", "next Visit Clicked !");
                    CommonPersonObjectClient client=(CommonPersonObjectClient)view.getTag();
                 //   Log.d("child :", client.getDetails().get("existing_program_client_id"));
                    HashMap<String , String > map=new HashMap<String ,String>();
                    map.put("provider_uc","korangi");
                    map.put("provider_town","korangi");
                    map.put("provider_city","karachi");
                    map.put("provider_province","sindh");
                    map.put("existing_program_client_id",client.getDetails().get("existing_program_client_id"));
                    map.put("provider_location_id","korangi");
                    map.put("provider_location_name", "korangi");//client
                    startFollowupForms("child_followup_form",(SmartRegisterClient)view.getTag(),map,ByColumnAndByDetails.bydefault);
                   // showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;
            }
        }

    }

    @Override
    public void setupViews() {
        getDefaultOptionsProvider();

        super.setupViews();

        setServiceModeViewDrawableRight(null);
        updateSearchView();


    }



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
                        setCurrentSearchFilter(new ChildSearchOption(cs.toString()));
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
    }

    public void addChildToList(ArrayList<DialogOption> dialogOptionslist,Map<String,TreeNode<String, Location>> locationMap){
        for(Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if(entry.getValue().getChildren() != null) {
                addChildToList(dialogOptionslist,entry.getValue().getChildren());

            }else{
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                Log.d("ANM Details", "location name :" + name);
                dialogOptionslist.add(new CommonObjectFilterOption(name.replace(" ","_"),"location_name", CommonObjectFilterOption.ByColumnAndByDetails.byDetails,name));

            }
        }
    }


    public void addChildToList(Map<String,TreeNode<String, Location>> locationMap){
        for(Map.Entry<String, TreeNode<String, Location>> entry : locationMap.entrySet()) {

            if(entry.getValue().getChildren() != null) {
                addChildToList(entry.getValue().getChildren());

            }else{
                StringUtil.humanize(entry.getValue().getLabel());
                String name = StringUtil.humanize(entry.getValue().getLabel());
                Log.d("ANM Details", "location name :" + name);

               // dialogOptionslist.add(new CommonObjectFilterOption(name.replace(" ","_"),"location_name", CommonObjectFilterOption.ByColumnAndByDetails.byDetails,name));

            }
        }
    }

    private int getfilteredClients(String filterString){
    int i=0;
        setCurrentSearchFilter(new ChildSearchOption(filterString));
        SmartRegisterClients  filteredClients = getClientsAdapter().getListItemProvider()
                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
                        getCurrentSearchFilter(), getCurrentSortOption());
        i=filteredClients.size();

    return i;
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
