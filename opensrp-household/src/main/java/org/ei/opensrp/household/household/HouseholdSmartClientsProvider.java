package org.ei.opensrp.household.household;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;

import static org.ei.opensrp.util.Utils.fillValue;

import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.json.JSONException;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import static org.ei.opensrp.util.Utils.fillWithIdentifier;
import static org.ei.opensrp.util.Utils.getValue;
import static org.ei.opensrp.util.Utils.setProfiePic;
import static org.ei.opensrp.util.Utils.userRoles;

/**
 * Created by Safwan on 4/22/2016.
 */
public class HouseholdSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public HouseholdSmartClientsProvider(Context context, View.OnClickListener onClickListener
            , AlertService alertService) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }


    @Override
    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        String sql = "select * from pkindividual where relationalid = '" + pc.getCaseId() + "'";
        List<CommonPersonObject> individualList = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(sql, new String[]{}, "pkindividual");

        if (userRoles.contains("Vaccinator")) {
            valuesForVaccinator(pc, parentView, individualList);
        } else {
            valuesForLHW(pc, parentView, individualList);
        }


        LinearLayout memberAdd = (LinearLayout) parentView.findViewById(R.id.household_add_member);
        memberAdd.setBackgroundColor(context.getResources().getColor(R.color.alert_normal));

        setProfiePic(parentView.getContext(), (ImageView) parentView.findViewById(R.id.household_profilepic), client.entityId(), null);

        parentView.findViewById(R.id.household_profile_info_layout).setTag(client);
        parentView.findViewById(R.id.household_profile_info_layout).setOnClickListener(onClickListener);

        parentView.findViewById(R.id.household_add_member).setTag(client);
        parentView.findViewById(R.id.household_add_member).setOnClickListener(onClickListener);

        parentView.setLayoutParams(clientViewLayoutParams);



        return parentView;
    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, SearchFilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    /*@Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }*/

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public View inflateLayoutForAdapter() {
        return inflater().inflate(R.layout.smart_register_household_client, null);
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    public void valuesForVaccinator(CommonPersonObjectClient pc, View parentView, List<CommonPersonObject> individualList){
        try {
            fillWithIdentifier((TextView) parentView.findViewById(R.id.household_id), pc.getColumnmaps(), "Program Client ID", false);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        fillValue((TextView) parentView.findViewById(R.id.household_name), getValue(pc.getColumnmaps(), "first_name_hhh", true) + " " + getValue(pc.getColumnmaps(), "last_name_hhh", true));
        fillValue((TextView) parentView.findViewById(R.id.household_member_count), "" + individualList.size() + "");
        fillValue((TextView) parentView.findViewById(R.id.household_address), getValue(pc.getColumnmaps(), "address1", true) + ", " + getValue(pc.getColumnmaps(), "union_council", true) + ", " +
                getValue(pc.getColumnmaps(), "town", true) + ",\n " + getValue(pc.getColumnmaps(), "city_village", true) + ", " +
                getValue(pc.getColumnmaps(), "province", true));
        fillValue((TextView) parentView.findViewById(R.id.household_contact), getValue(pc.getColumnmaps(), "contact_phone_number_hhh", true));
    }

    public void valuesForLHW(CommonPersonObjectClient pc, View parentView, List<CommonPersonObject> individualList){
        fillValue((TextView) parentView.findViewById(R.id.household_id), pc.getColumnmaps(), "household_id", false);
        fillValue((TextView) parentView.findViewById(R.id.household_name), getValue(pc.getColumnmaps(), "first_name_hhh", true) + " " + getValue(pc.getColumnmaps(), "last_name_hhh", true));
        fillValue((TextView) parentView.findViewById(R.id.household_member_count), "" + individualList.size() + "");
        fillValue((TextView) parentView.findViewById(R.id.household_address), getValue(pc.getColumnmaps(), "address1", true) + ", " + getValue(pc.getColumnmaps(), "union_council", true) + ", " +
                getValue(pc.getColumnmaps(), "town", true) + ",\n " + getValue(pc.getColumnmaps(), "city_village", true) + ", " +
                getValue(pc.getColumnmaps(), "province", true));
        fillValue((TextView) parentView.findViewById(R.id.household_contact), getValue(pc.getColumnmaps(), "contact_phone_number_hhh", true));
    }
}
