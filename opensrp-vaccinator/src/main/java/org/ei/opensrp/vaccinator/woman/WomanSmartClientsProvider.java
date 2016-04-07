package org.ei.opensrp.vaccinator.woman;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static util.Utils.convertDateFormat;
import static util.Utils.fillValue;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;
import static util.Utils.setProfiePic;

/**
 * Created by Ahmed on 19-Oct-15.
 */
public class WomanSmartClientsProvider implements SmartRegisterClientsProvider {


    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;
    AlertService alertService;
    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;

    protected CommonPersonObjectController controller;

    public WomanSmartClientsProvider(Context context, View.OnClickListener onClickListener,
                                     CommonPersonObjectController controller, AlertService alertService) {
        this.onClickListener = onClickListener;
        this.controller = controller;
        this.context = context;
        this.alertService = alertService;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT, (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }


    @Override
    public View getView(SmartRegisterClient client, View convertView, ViewGroup viewGroup) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        convertView = (ViewGroup) inflater().inflate(R.layout.smart_register_woman_client, null);
        fillValue((TextView)convertView.findViewById(R.id.woman_id), pc, "existing_program_client_id", false);
        fillValue((TextView) convertView.findViewById(R.id.woman_name), getValue(pc, "first_name", true));
        fillValue((TextView) convertView.findViewById(R.id.woman_husbandname), getValue(pc, "husband_name", true));
        fillValue((TextView) convertView.findViewById(R.id.woman_fathername), getValue(pc, "father_name", true));

        int age = Years.yearsBetween(new DateTime(getValue(pc.getColumnmaps(), "dob", false)), DateTime.now()).getYears();
        fillValue((TextView) convertView.findViewById(R.id.woman_age),  age+ " years");
        fillValue((TextView) convertView.findViewById(R.id.woman_epi_number), pc, "epi_card_number", false);
        String edd = convertDateFormat(getValue(pc, "final_edd", false),true);
        fillValue((TextView) convertView.findViewById(R.id.woman_edd), edd == ""?"N/A":edd);
        String ga = getValue(pc, "final_ga", false);
        ga = ga==""?"N/A":(ga+" weeks");
        fillValue((TextView) convertView.findViewById(R.id.woman_ga), ga);

        //convertView.setTag(viewHolder);

        String vaccineretro = getValue(pc.getColumnmaps(), "vaccines", false);
        String vaccine2 = getValue(pc.getColumnmaps(), "vaccines_2", false);

        fillValue((TextView) convertView.findViewById(R.id.woman_last_vaccine), vaccine2);
        String lastVaccine = convertDateFormat(nonEmptyValue(pc.getColumnmaps(), true, false, new String[]{"tt1", "tt2", "tt3", "tt4", "tt5"}), true);

        fillValue((TextView) convertView.findViewById(R.id.woman_last_visit_date), lastVaccine);

        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(), "TT 1", "TT 2", "TT 3", "TT 4", "TT 5", "tt1", "tt2", "tt3", "tt4", "tt5");

        if (alertlist_for_client.size() == 0) {
            if(StringUtils.isNotBlank(getValue(pc.getColumnmaps(), "tt5", false))){
                fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), "Fully Immunized");
                convertView.findViewById(R.id.woman_next_visit_holder).setBackgroundColor(context.getResources().getColor(R.color.alert_na));
            }
            else if(age > 49 && StringUtils.isBlank(getValue(pc.getColumnmaps(), "tt5", false))){
                fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), "Partially Immunized");
                convertView.findViewById(R.id.woman_next_visit_holder).setBackgroundColor(context.getResources().getColor(R.color.alert_na));
            }
            else {
                fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), "Waiting");
                convertView.findViewById(R.id.woman_next_visit_holder).setBackgroundColor(context.getResources().getColor(R.color.alert_na));
            }
        }
        else {
            for (int i = 0; i < alertlist_for_client.size(); i++) {
                fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), alertlist_for_client.get(i).expiryDate());
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                    String dueDate = alertlist_for_client.get(i).startDate();
                    String vaccine = alertlist_for_client.get(i).scheduleName();
                    fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), vaccine + " : " + convertDateFormat(dueDate, true));
                    convertView.findViewById(R.id.woman_next_visit_holder).setBackgroundColor(context.getResources().getColor(R.color.alert_normal));
                    convertView.findViewById(R.id.woman_next_visit_holder).setOnClickListener(onClickListener);
                    convertView.findViewById(R.id.woman_next_visit_holder).setTag(client);
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                    String dueDate = alertlist_for_client.get(i).startDate();
                    String vaccine = alertlist_for_client.get(i).scheduleName();
                    fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), vaccine + " : " + convertDateFormat(dueDate, true));
                    convertView.findViewById(R.id.woman_next_visit_holder).setBackgroundColor(context.getResources().getColor(R.color.alert_upcoming));
                    convertView.findViewById(R.id.woman_next_visit_holder).setOnClickListener(onClickListener);
                    convertView.findViewById(R.id.woman_next_visit_holder).setTag(client);
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                    String dueDate = alertlist_for_client.get(i).startDate();
                    String vaccine = alertlist_for_client.get(i).scheduleName();
                    fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), vaccine + " : " + convertDateFormat(dueDate, true));
                    convertView.findViewById(R.id.woman_next_visit_holder).setBackgroundColor(context.getResources().getColor(R.color.alert_urgent));
                    convertView.findViewById(R.id.woman_next_visit_holder).setOnClickListener(onClickListener);
                    convertView.findViewById(R.id.woman_next_visit_holder).setTag(client);
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                    String vaccine = alertlist_for_client.get(i).scheduleName();
                    fillValue((TextView) convertView.findViewById(R.id.woman_next_visit), vaccine + " : " + convertDateFormat(alertlist_for_client.get(i).expiryDate(), true));
                    convertView.findViewById(R.id.woman_next_visit_holder).setBackgroundColor(context.getResources().getColor(R.color.alert_expired));
                }
            }
        }
        setProfiePic((ImageView) convertView.findViewById(R.id.woman_profilepic), client.entityId());

        convertView.findViewById(R.id.woman_profile_info_layout).setTag(client);
        convertView.findViewById(R.id.woman_profile_info_layout).setOnClickListener(onClickListener);

        convertView.setLayoutParams(clientViewLayoutParams);
        return convertView;
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

}
