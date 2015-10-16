package org.ei.opensrp.vaccinator.child;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
import org.ei.opensrp.commonregistry.CommonPersonObjectClients;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by Ahmed on 13-Oct-15.
 */
public class ChildSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public ChildSmartClientsProvider(Context context,
                                     View.OnClickListener onClickListener,
                                     CommonPersonObjectController controller , AlertService alertService) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public View getView(SmartRegisterClient client, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = (ViewGroup) inflater().inflate(R.layout.smart_register_child_client, null);
            viewHolder = new ViewHolder();
            viewHolder.profilelayout =  (LinearLayout)convertView.findViewById(R.id.profile_info_layout);
            viewHolder.childId=(TextView)convertView.findViewById(R.id.child_id);
            viewHolder.childName=(TextView)convertView.findViewById(R.id.child_name);
            viewHolder.fatherName=(TextView)convertView.findViewById(R.id.child_father_name);
            viewHolder.childDOB=(TextView)convertView.findViewById(R.id.child_dob);
            viewHolder.profilepic=(ImageView)convertView.findViewById(R.id.child_profilepic);
            viewHolder.last_visit_date=(TextView)convertView.findViewById(R.id.child_last_visit_date);
            viewHolder.last_vaccine=(TextView)convertView.findViewById(R.id.child_last_vaccine);
            viewHolder.next_visit_date=(TextView)convertView.findViewById(R.id.child_next_visit);
            //viewHolder.profilepic.setImageResource();
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();

        }

        ViewGroup itemView = viewGroup;

        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if(pc.getDetails().get("profilepic")!=null){
           if( pc.getDetails().get("gender").equalsIgnoreCase("female")) {
               viewHolder.profilepic.setImageResource(org.ei.opensrp.R.drawable.child_girl_infant);
               // HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.drawable.child_boy_infant);
            }else{
               viewHolder.profilepic.setImageResource(org.ei.opensrp.R.drawable.child_boy_infant);
              // HouseHoldDetailActivity.setImagetoHolder((Activity) context, pc.getDetails().get("profilepic"), profilepic, R.drawable.child_girl_infant);

           }
        }

        viewHolder.childId.setText(pc.getDetails().get("program_client_id") != null ? pc.getDetails().get("program_client_id") : "");
        viewHolder. childName.setText(pc.getDetails().get("first_name") != null ? pc.getDetails().get("first_name") : "");
        viewHolder.fatherName.setText(pc.getDetails().get("father_name") != null ? pc.getDetails().get("father_name") : "");
        viewHolder. childDOB.setText(pc.getDetails().get("chid_dob_confirm") != null ? pc.getDetails().get("chid_dob_confirm") : "");

        viewHolder. profilepic.setOnClickListener(onClickListener);
        viewHolder. profilepic.setTag(client);

        ImageView lastVisit = (ImageView)itemView.findViewById(R.id.child_last_visit_date);
        ImageView nextVisit = (ImageView)itemView.findViewById(R.id.child_next_visit);

       // lastVisit.setOnClickListener(onClickListener);
     //   lastVisit.setTag(client);
        viewHolder.next_visit_date.setOnClickListener(onClickListener);
        viewHolder.next_visit_date.setTag(client);
        convertView.setLayoutParams(clientViewLayoutParams);
        return convertView;
    }

    @Override
    public SmartRegisterClients getClients() {
       if(controller.getClients()==null) {
           Log.d("Controller  Clients  :" , "is  Null ");
            return  new CommonPersonObjectClients();
       }



        return controller.getClients();
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return getClients().applyFilter(villageFilter, serviceModeOption, searchFilter, sortOption);
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {

    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }


    public LayoutInflater inflater() {
        return inflater;
    }

    class ViewHolder {

        TextView childId ;
        TextView childName ;
        TextView fatherName;
        TextView childDOB;
        TextView last_vaccine;
        TextView last_visit_date;
        TextView next_visit_date;
        ImageButton follow_up;
        LinearLayout profilelayout;
        ImageView profilepic;
        FrameLayout due_date_holder;
    }
}
