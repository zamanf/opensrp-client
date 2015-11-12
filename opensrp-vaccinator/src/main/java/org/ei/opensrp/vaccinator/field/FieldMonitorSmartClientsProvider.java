package org.ei.opensrp.vaccinator.field;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Created by muhammad.ahmed@ihsinformatics.com on 12-Nov-15.
 */
public class FieldMonitorSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final OnClickListener onClickListener;
    AlertService alertService;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;


    public FieldMonitorSmartClientsProvider(Context context,
                                            OnClickListener onClickListener,
                                            CommonPersonObjectController controller, AlertService alertService) {
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
