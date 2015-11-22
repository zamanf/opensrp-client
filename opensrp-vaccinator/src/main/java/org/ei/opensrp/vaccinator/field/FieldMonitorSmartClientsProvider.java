package org.ei.opensrp.vaccinator.field;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.DateUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.LocalDate;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 12-Nov-15.
 */
public class FieldMonitorSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final OnClickListener onClickListener;
    AlertService alertService;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    //private CommonRepository commonRepository=new CommonRepository();
    private org.ei.opensrp.Context context1;
    ByMonthANDByDAILY byMonthlyAndByDaily;
    public enum  ByMonthANDByDAILY{ ByMonth , ByDaily;}


    protected CommonPersonObjectController controller;


    public FieldMonitorSmartClientsProvider(Context context,
                                            OnClickListener onClickListener,CommonPersonObjectController controller,
                                            AlertService alertService , ByMonthANDByDAILY byMonthlyAndByDaily) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.byMonthlyAndByDaily=byMonthlyAndByDaily;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }


    @Override
    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
        ViewHolder viewHolder;

        if(parentView==null){
            parentView = (ViewGroup) inflater().inflate(R.layout.smart_register_field_client, null);


            viewHolder = new ViewHolder();

            viewHolder.daymonthTextView=(TextView)parentView.findViewById(R.id.field_daymonth);
            viewHolder.daymonthLayout=(LinearLayout)parentView.findViewById(R.id.field_daymonth_layout);

            viewHolder.monthTargetTextView=(TextView)parentView.findViewById(R.id.field_month_target);
            viewHolder.monthTargetLayout=(LinearLayout)parentView.findViewById(R.id.field_month_target_layout);

            viewHolder.monthreceivedTextView=(TextView)parentView.findViewById(R.id.field_vaccine_recieved);
            viewHolder.monthreceivedLayout=(LinearLayout)parentView.findViewById(R.id.field_vaccine_recieved_layout);


            viewHolder.monthusedTextView=(TextView)parentView.findViewById(R.id.field_vaccine_used);
            viewHolder.monthusedLayout=(LinearLayout)parentView.findViewById(R.id.field_vaccine_used_layout);

            viewHolder.monthwastedTextView=(TextView)parentView.findViewById(R.id.field_vaccine_wasted);
            viewHolder.monthwastedLayout=(LinearLayout)parentView.findViewById(R.id.field_vaccine_wasted_layout);



        }else{
            viewHolder = (ViewHolder) parentView.getTag();

        }
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;


        if(ByMonthANDByDAILY.ByMonth.equals(byMonthlyAndByDaily)){


            String date_entered =pc.getDetails().get("date_formatted");
            String sql ="select * from pkchild where date like  ?";
            List<CommonPersonObject> used=context1.allCommonsRepositoryobjects("field").customQuery(sql, new String[]{date_entered}, "pkchild");
            int totalUsed=0;
            for (CommonPersonObject o:used) {
                totalUsed+=  Integer.parseInt(o.getColumnmaps().get("total_used"));

            }
           //LocalDate localTime= DateUtil.getLocalDate(month);
            ///localTime.
            viewHolder.daymonthTextView.setText(date_entered);

            viewHolder.monthTargetTextView.setText(pc.getDetails().get("Target_assigned_for_vaccination_at_each_month")!=null?pc.getDetails().get("Target_assigned_for_vaccination_at_each_month"):"Not Defined");
            viewHolder.monthreceivedTextView.setText(pc.getDetails().get("Target_assigned_for_vaccination_at_each_month")!=null?pc.getDetails().get("Target_assigned_for_vaccination_at_each_month"):"Not Defined");

            int bcgBalanceInHand=Integer.parseInt(pc.getDetails().get("bcg_balance_in_hand")!=null ?pc.getDetails().get("bcg_balance_in_hand"):"0");
            int bcgReceived=Integer.parseInt(pc.getDetails().get("bcg_received")!=null ?pc.getDetails().get("bcg_received"):"0");

            int opv_balance_in_hand=Integer.parseInt(pc.getDetails().get("opv_balance_in_hand")!=null ?pc.getDetails().get("opv_balance_in_hand"):"0");
            int opv_received=Integer.parseInt(pc.getDetails().get("opv_received")!=null ?pc.getDetails().get("opv_received"):"0");


            int ipv_balance_in_hand=Integer.parseInt(pc.getDetails().get("ipv_balance_in_hand")!=null ?pc.getDetails().get("ipv_balance_in_hand"):"0");
            int ipv_received=Integer.parseInt(pc.getDetails().get("ipv_received")!=null ?pc.getDetails().get("ipv_received"):"0");

            int pcv_balance_in_hand=Integer.parseInt(pc.getDetails().get("pcv_balance_in_hand")!=null ?pc.getDetails().get("pcv_balance_in_hand"):"0");
            int pcv_received=Integer.parseInt(pc.getDetails().get("pcv_received")!=null ?pc.getDetails().get("pcv_received"):"0");

            int penta_balance_in_hand=Integer.parseInt(pc.getDetails().get("penta_balance_in_hand")!=null ?pc.getDetails().get("penta_balance_in_hand"):"0");
            int penta_received=Integer.parseInt(pc.getDetails().get("penta_received")!=null ?pc.getDetails().get("penta_received"):"0");

            int measles_balance_in_hand=Integer.parseInt(pc.getDetails().get("measles_balance_in_hand")!=null ?pc.getDetails().get("measles_balance_in_hand"):"0");
            int measles_received=Integer.parseInt(pc.getDetails().get("measles_received")!=null ?pc.getDetails().get("measles_received"):"0");


            int tt_balance_in_hand=Integer.parseInt(pc.getDetails().get("tt_balance_in_hand")!=null ?pc.getDetails().get("tt_balance_in_hand"):"0");
            int tt_received=Integer.parseInt(pc.getDetails().get("tt_received")!=null ?pc.getDetails().get("tt_received"):"0");

            int dilutants_balance_in_hand=Integer.parseInt(pc.getDetails().get("dilutants_balance_in_hand")!=null ?pc.getDetails().get("dilutants_balance_in_hand"):"0");
            int dilutants_received=Integer.parseInt(pc.getDetails().get("dilutants_received")!=null ?pc.getDetails().get("dilutants_received"):"0");

            int syringes_balance_in_hand=Integer.parseInt(pc.getDetails().get("syringes_balance_in_hand")!=null ?pc.getDetails().get("syringes_balance_in_hand"):"0");
            int syringes_received=Integer.parseInt(pc.getDetails().get("syringes_received")!=null ?pc.getDetails().get("syringes_received"):"0");


            int safety_boxes_balance_in_hand=Integer.parseInt(pc.getDetails().get("safety_boxes_balance_in_hand")!=null ?pc.getDetails().get("safety_boxes_balance_in_hand"):"0");
            int safety_boxes_received=Integer.parseInt(pc.getDetails().get("safety_boxes_received")!=null ?pc.getDetails().get("safety_boxes_received"):"0");


            int balanceInHand=bcgBalanceInHand+opv_balance_in_hand+ipv_balance_in_hand+pcv_balance_in_hand+penta_balance_in_hand+measles_balance_in_hand+tt_balance_in_hand+dilutants_balance_in_hand+
                    syringes_balance_in_hand+safety_boxes_balance_in_hand;

            int Received=bcgReceived+opv_received+ipv_received+pcv_received+penta_received+measles_received+tt_received+
                    dilutants_received+syringes_received+safety_boxes_received;

            viewHolder.monthreceivedTextView.setText(Received);
            viewHolder.monthusedTextView.setText(balanceInHand);
            viewHolder.monthusedTextView.setText(totalUsed);



        }else{


        }

        viewHolder.daymonthTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });



        parentView.setLayoutParams(clientViewLayoutParams);
        return parentView;
    }

    @Override
    public SmartRegisterClients getClients() {
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
        TextView daymonthTextView;
        TextView monthTargetTextView;
        TextView monthreceivedTextView;
        TextView monthusedTextView;
        TextView monthwastedTextView;

        LinearLayout daymonthLayout;
        LinearLayout monthTargetLayout;
        LinearLayout monthreceivedLayout;
        LinearLayout monthusedLayout;
        LinearLayout monthwastedLayout;


    }
}
