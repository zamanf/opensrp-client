package com.opensrp.jilinde.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.opensrp.jilinde.LoginActivity;
import com.opensrp.jilinde.R;
import com.opensrp.jilinde.child.BeneficiariesServiceModeOption;
import com.opensrp.jilinde.child.BeneficiariesSmartClientsProvider;
import com.opensrp.jilinde.child.BeneficiariesDueDateSort;
import com.opensrp.jilinde.child.BeneficiariesSmartRegisterActivity;
import com.opensrp.jilinde.child.BeneficiaryDetailActivity;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.commonregistry.ControllerFilterMap;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorCommonObjectSort;
import org.ei.opensrp.cursoradapter.SecuredNativeSmartRegisterCursorAdapterFragment;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.cursoradapter.SmartRegisterQueryBuilder;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.util.StringUtil;
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
import org.opensrp.api.domain.Location;
import org.opensrp.api.util.EntityUtils;
import org.opensrp.api.util.LocationTree;
import org.opensrp.api.util.TreeNode;

import java.util.ArrayList;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Created by koros on 11/2/15.
 */
public class BeneficiariesSmartRegisterFragment extends SecuredNativeSmartRegisterCursorAdapterFragment {

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedAdapter(clientsProvider());
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {

            @Override
            public ServiceModeOption serviceMode() {
                return new BeneficiariesServiceModeOption(clientsProvider());
            }

            @Override
            public FilterOption villageFilter() {
                return new AllClientsFilter();
            }

            @Override
            public SortOption sortOption() {
                return new BeneficiariesDueDateSort();

            }

            @Override
            public String nameInShortFormForTitle() {
                return getResources().getString(R.string.jilinde_beneficiaries_title_in_short);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                ArrayList<DialogOption> dialogOptionslist = new ArrayList<DialogOption>();
                dialogOptionslist.add(new CursorCommonObjectFilterOption(getString(R.string.filter_by_all_label),""));

                DialogOption[] dialogOptions = new DialogOption[dialogOptionslist.size()];
                for (int i = 0;i < dialogOptionslist.size();i++){
                    dialogOptions[i] = dialogOptionslist.get(i);
                }

                return  dialogOptions;
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
//                        new ElcoPSRFDueDateSort(),
                       // new CursorCommonObjectSort(getString(R.string.due_status),sortByAlertmethod()),
                        new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.alphabetical_sort),sortByName()),
                        //new CursorCommonObjectSort(Context.getInstance().applicationContext().getString(R.string.child_details_fathers_name_label),sortByfather_name()),
                        //new CursorCommonObjectSort( Context.getInstance().applicationContext().getString(R.string.child_details_mothers_name_label),sortBymother_name()),

//                        new CommonObjectSort(true,false,true,"age")
                };
            }

            @Override
            public String searchHint() {
                return getString(R.string.str_ec_search_hint);
            }
        };
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {

        return null;
    }


    @Override
    protected void onInitialization() {

    }

    @Override
    protected void startRegistration() {
        ((BeneficiariesSmartRegisterActivity) this.getActivity()).startRegistration();
    }


    @Override
    protected void onCreation() {
    }
    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();
        initializeQueries();
        updateSearchView();
        try{
            LoginActivity.setLanguage();
        }catch (Exception e){

        }

    }

    @Override
    public void setupViews(View view) {
        super.setupViews(view);
        view.findViewById(R.id.btn_report_month).setVisibility(INVISIBLE);
        view.findViewById(R.id.service_mode_selection).setVisibility(INVISIBLE);

        ImageButton startregister = (ImageButton)view.findViewById(org.ei.opensrp.R.id.register_client);
//        startregister.setVisibility(View.GONE);
        clientsView.setVisibility(View.VISIBLE);
        clientsProgressView.setVisibility(View.INVISIBLE);
        setServiceModeViewDrawableRight(null);
        initializeQueries();
        updateSearchView();
    }

    private DialogOption[] getEditOptionsforChild() {
        return ((BeneficiariesSmartRegisterActivity)getActivity()).getEditOptionsforChild();
    }



    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.profile_info_layout:
                    BeneficiaryDetailActivity.ChildClient = (CommonPersonObjectClient)view.getTag();
                    Intent intent = new Intent(getActivity(),BeneficiaryDetailActivity.class);
                    startActivity(intent);
                    break;
//                case R.id.encc_reminder_due_date:
//                    CustomFontTextView enccreminderDueDate = (CustomFontTextView)view.findViewById(R.id.encc_reminder_due_date);
//                    Log.v("do as you will", (String) view.getTag(R.id.textforEnccRegister));
//                    showFragmentDialog(new EditDialogOptionModelForChild((String)view.getTag(R.id.textforEnccRegister),(String)view.getTag(R.id.AlertStatustextforEnccRegister)), view.getTag(R.id.clientobject));
//                    break;
            }
        }


    }

    private class EditDialogOptionModelForChild implements DialogOptionModel {

        public EditDialogOptionModelForChild() {
        }

        @Override
        public DialogOption[] getDialogOptions() {
            return getEditOptionsforChild();
        }

        @Override
        public void onDialogOptionSelection(DialogOption option, Object tag) {
            onEditSelection((EditOption) option, (SmartRegisterClient) tag);
        }
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
//                        setCurrentSearchFilter(new HHSearchOption(cs.toString()));
//                        filteredClients = getClientsAdapter().getListItemProvider()
//                                .updateClients(getCurrentVillageFilter(), getCurrentServiceModeOption(),
//                                        getCurrentSearchFilter(), getCurrentSortOption());
//
                        if(cs.toString().equalsIgnoreCase("")){
                            filters = "";
                        }else {
                            filters = "and name Like '%" + cs.toString() + "%' or site Like '%" + cs.toString() + "%'  or location Like '%" + cs.toString() + "%' ";
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
//                        clientsAdapter
//                                .refreshList(currentVillageFilter, currentServiceModeOption,
//                                        currentSearchFilter, currentSortOption);
//                        getClientsAdapter().refreshClients(filteredClients);
//                        getClientsAdapter().notifyDataSetChanged();
                        getSearchCancelView().setVisibility(isEmpty(cs) ? INVISIBLE : VISIBLE);
                        CountExecute();
                        filterandSortExecute();
                        super.onPostExecute(o);
                    }
                }).execute();




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
                dialogOptionslist.add(new CursorCommonObjectFilterOption(name,"and mcaremother.details like '%"+name +"%'"));

            }
        }
    }
    class pncControllerfiltermap extends ControllerFilterMap{

        @Override
        public boolean filtermapLogic(CommonPersonObject commonPersonObject) {
            boolean returnvalue = false;
            if(commonPersonObject.getDetails().get("FWWOMVALID") != null){
                if(commonPersonObject.getDetails().get("FWWOMVALID").equalsIgnoreCase("1")){
                    returnvalue = true;
                    if(commonPersonObject.getDetails().get("Is_PNC")!=null){
                        if(commonPersonObject.getDetails().get("Is_PNC").equalsIgnoreCase("1")){
                            returnvalue = true;
                        }

                    }else{
                        returnvalue = false;
                    }
                }
            }
            Log.v("the filter", "" + returnvalue);
            return returnvalue;
        }
    }
    public void initializeQueries(){
        CommonRepository commonRepository = context.commonrepository("beneficiaries");
        setTablename("beneficiaries");
        SmartRegisterQueryBuilder countqueryBUilder = new SmartRegisterQueryBuilder(childMainCountWithJoins());
        countSelect = countqueryBUilder.mainCondition(" beneficiaries.name is not null ");
        CountExecute();


        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder(childMainSelectWithJoins());
        mainSelect = queryBUilder.mainCondition(" beneficiaries.name is not null ");
        queryBUilder.addCondition(filters);
        Sortqueries = sortByName();
        currentquery  = queryBUilder.orderbyCondition(Sortqueries);


//        queryBUilder.queryForRegisterSortBasedOnRegisterAndAlert("household", new String[]{"relationalid" ,"details","FWHOHFNAME", "FWGOBHHID","FWJIVHHID"}, null, "FW CENSUS");
//        Cursor c = commonRepository.CustomQueryForAdapter(new String[]{"id as _id","relationalid","details"},"household",""+currentlimit,""+currentoffset);
        Cursor c = commonRepository.RawCustomQueryForAdapter(queryBUilder.Endquery(queryBUilder.addlimitandOffset(currentquery, 20, 0)));
        BeneficiariesSmartClientsProvider hhscp = new BeneficiariesSmartClientsProvider(getActivity(),clientActionHandler,context.alertService());
        clientAdapter = new SmartRegisterPaginatedCursorAdapter(getActivity(), c, hhscp, new CommonRepository("beneficiaries",new String []{ "name", "location", "age", "gender", "phone_no", "enrollment_date", "site" }));
        clientsView.setAdapter(clientAdapter);
//        setServiceModeViewDrawableRight(null);
//        updateSearchView();
        refresh();

    }

    public String childMainSelectWithJoins(){
        return "Select beneficiaries.id as _id, beneficiaries.relationalid, beneficiaries.name, beneficiaries.location, beneficiaries.age, beneficiaries.gender, beneficiaries.phone_no, beneficiaries.enrollment_date, beneficiaries.site \n" +
                "from beneficiaries\n";
    }
    public String childMainCountWithJoins() {
        return "Select Count(*) \n" +
                "from beneficiaries";
    }

    private String sortByName(){
        return " name ASC";
    }

}
