package org.ei.opensrp.steppingstones.children;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.steppingstones.R;
import org.ei.opensrp.steppingstones.ss_children.ChildrenDetailActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by user on 2/12/15.
 */
public class ChildrenClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public ChildrenClientsProvider(Context context,
                                   View.OnClickListener onClickListener,
                                   CommonPersonObjectController controller) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(R.color.text_black);
    }



    @Override
    public View getView(SmartRegisterClient smartRegisterClient, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = (ViewGroup) inflater().inflate(org.ei.opensrp.steppingstones.R.layout.smart_register_children_client, null);
            viewHolder = new ViewHolder();
            viewHolder.profilelayout =  (LinearLayout)convertView.findViewById(org.ei.opensrp.steppingstones.R.id.profile_info_layout);
            viewHolder.gobhhid = (TextView)convertView.findViewById(org.ei.opensrp.steppingstones.R.id.gobhhid);
            viewHolder.jvitahhid = (TextView)convertView.findViewById(org.ei.opensrp.steppingstones.R.id.jvitahhid);
            viewHolder.village = (TextView)convertView.findViewById(org.ei.opensrp.steppingstones.R.id.village);
            viewHolder.profilepic =(ImageView)convertView.findViewById(org.ei.opensrp.steppingstones.R.id.profilepic);

            viewHolder.headofhouseholdname = (TextView)convertView.findViewById(org.ei.opensrp.steppingstones.R.id.householdheadname);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.profilepic.setImageDrawable(context.getResources().getDrawable(org.ei.opensrp.steppingstones.R.mipmap.household_profile_thumb));
        }

        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;



        if(pc.getDetails().get("profilepic")!=null){
            ChildrenDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), viewHolder.profilepic, org.ei.opensrp.steppingstones.R.mipmap.household_profile_thumb);
        }
        viewHolder.gobhhid.setText(pc.getDetails().get("UID")!=null?pc.getDetails().get("UID"):"");
        viewHolder.jvitahhid.setText(pc.getDetails().get("Name")!=null?pc.getDetails().get("Name"):"");
        viewHolder.village.setText(pc.getDetails().get("Name")!=null?pc.getDetails().get("Name"):"");
        viewHolder.headofhouseholdname.setText(pc.getDetails().get("Name")!=null?pc.getDetails().get("Name"):"");

        convertView.setLayoutParams(clientViewLayoutParams);
        return convertView;
    }

    @Override
    public SmartRegisterClients getClients() {
        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        // do nothing.
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    public LayoutInflater inflater() {
        return inflater;
    }

    class ViewHolder {

        TextView id ;
        TextView name ;
        TextView dob;
        TextView age;

        TextView gobhhid ;
        TextView jvitahhid ;
        TextView village;
        TextView headofhouseholdname;
        TextView no_of_mwra;
        TextView last_visit_date;
        TextView due_visit_date;
        ImageButton follow_up;
        LinearLayout profilelayout;
        ImageView profilepic;
        FrameLayout due_date_holder;


    }
}