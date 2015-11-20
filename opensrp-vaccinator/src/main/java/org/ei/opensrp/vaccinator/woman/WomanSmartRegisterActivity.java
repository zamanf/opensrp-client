package org.ei.opensrp.vaccinator.woman;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;

import org.ei.opensrp.vaccinator.child.ChildSearchOption;

import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.controller.VillageController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.DialogOptionMapper;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.HashMap;
import java.util.Map;

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

                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
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
                return getResources().getString(R.string.woman_search_hint);
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
            controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("pk_woman"),
                    context.allBeneficiaries(), context.listCache(),
                    context.personObjectClientsCache(), "first_name", "pk_woman", "program_client_id",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails );


          //  Log.d("Child count :", context.commonrepository("vaccine_child").toString() + "");

            //context.
         /*   controller = new CommonPersonObjectController(context.allCommonsRepositoryobjects("elco"),
                    context.allBeneficiaries(), context.listCache(),
                    context.personObjectClientsCache(),
                    "FWWOMFNAME","elco","FWELIGIBLE","1",
                    CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails,
                    "FWWOMFNAME", CommonPersonObjectController.ByColumnAndByDetails.byDetails,
                    new ElcoPSRFDueDateSort());
*/

        }
        dialogOptionMapper = new DialogOptionMapper();
    }//end of method

    @Override
    protected void onCreation() {
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
                /*case R.id.:
                   // showFragmentDialog(new EditDialogOptionModel(), view.getTag());
                    break;*/
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
    }//end of method

    private int getfilteredClients(String filterString){
        int i=0;
        setCurrentSearchFilter(new ChildSearchOption(filterString));
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
            String locationjson = context.anmLocationController().get();
                 //   Log.d("ANM LOCATION : ", locationjson);
            LocationTree locationTree = EntityUtils.fromJson(locationjson, LocationTree.class);
            //locationTree.
            Map<String,TreeNode<String, Location>> locationMap =
                    locationTree.getLocationsHierarchy();
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
                map.put("provider_location_name", "korangi");
                //map.put("","");
                setOverrides(map);

                //  map.put("provider_id", anmController.get());
                //  map.put("program_client_id",qrcode);
                //showFragmentDialog(new EditDialogOptionModel(getOverrides()));

             //   showFragmentDialog(new EditDialogOptionModel(map),null);
            }else {
                getSearchView().setText(qrcode);

            }


            //          controller.getClients();



        }


    }//end of the method


    public HashMap<String,String> getOverrides() {
        return overrides;
    }//end of method
    public void setOverrides(HashMap<String,String>  overrides ){

        this.overrides=overrides;
    }//end of method
}
