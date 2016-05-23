package org.ei.opensrp.steppingstones.children;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.steppingstones.R;
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
            convertView = (ViewGroup) inflater().inflate(R.layout.smart_register_ss_child_client, null);
            viewHolder = new ViewHolder();


            viewHolder.id = (TextView)convertView.findViewById(R.id.id);
            viewHolder.name = (TextView)convertView.findViewById(R.id.name);
            viewHolder.age = (TextView)convertView.findViewById(R.id.age);
            viewHolder.dob = (TextView)convertView.findViewById(R.id.dob);

        }else{

            viewHolder = (ViewHolder) convertView.getTag();
        }


        CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;
//
        viewHolder.id.setText(pc.getDetails().get("UID")!=null?pc.getDetails().get("UID"):"");
        viewHolder.name.setText(pc.getDetails().get("Name")!=null?pc.getDetails().get("Name"):"");
        viewHolder.age.setText(pc.getDetails().get("Age")!=null?pc.getDetails().get("Age"):"");
        viewHolder.dob.setText(pc.getDetails().get("DOB") != null ? pc.getDetails().get("DOB") : "");

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

    }
}