package org.ei.opensrp.vaccinator.child;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

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
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Years;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static util.Utils.convertDateFormat;
import static util.Utils.fillValue;
import static util.Utils.getValue;
import static util.Utils.hasAnyEmptyValue;
import static util.Utils.nonEmptyValue;
import static util.Utils.setProfiePic;

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

    public ChildSmartClientsProvider(Context context, View.OnClickListener onClickListener,
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

        convertView = (ViewGroup) inflater().inflate(R.layout.smart_register_child_client, null);
        fillValue((TextView) convertView.findViewById(R.id.child_id), pc, "existing_program_client_id", false);
        fillValue((TextView) convertView.findViewById(R.id.child_name), getValue(pc, "first_name", true)+" "+getValue(pc, "last_name", true));
        fillValue((TextView) convertView.findViewById(R.id.child_mothername), getValue(pc, "mother_name", true));
        fillValue((TextView) convertView.findViewById(R.id.child_fathername), getValue(pc, "father_name", true));
        String gender = getValue(pc, "gender", true);
        if(gender.equalsIgnoreCase("male")){
            ((ImageView)convertView.findViewById(R.id.child_profilepic)).setImageResource(R.drawable.child_boy_infant);
        }
        else if(gender.equalsIgnoreCase("female")){
            ((ImageView)convertView.findViewById(R.id.child_profilepic)).setImageResource(R.drawable.child_girl_infant);
        }
        else if(gender.toLowerCase().contains("trans")){
            ((ImageView)convertView.findViewById(R.id.child_profilepic)).setImageResource(R.drawable.child_transgender_inflant);
        }

        int agey = Years.yearsBetween(new DateTime(getValue(pc.getColumnmaps(), "dob", false)), DateTime.now()).getYears();

        fillValue((TextView) convertView.findViewById(R.id.child_birthdate), convertDateFormat(getValue(pc.getColumnmaps(), "dob", false), true));
        fillValue((TextView) convertView.findViewById(R.id.child_age),
                Months.monthsBetween(new DateTime(getValue(pc.getColumnmaps(), "dob", false)), DateTime.now()).getMonths() + " months");
        fillValue((TextView) convertView.findViewById(R.id.child_epi_number), pc, "epi_card_number", false);

        String vaccineretro = getValue(pc.getColumnmaps(), "vaccines", false);
        String vaccine2 = getValue(pc.getColumnmaps(), "vaccines_2", false);

        fillValue((TextView) convertView.findViewById(R.id.child_last_vaccine), StringUtil.humanizeAndUppercase(vaccine2, "Penta", "Measles").replaceAll(" ", ", "));

        String[] vaccineList = new String[]{"bcg", "opv0",
                "penta1", "opv1", "pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2"};
        String lastVaccine = convertDateFormat(nonEmptyValue(pc.getColumnmaps(), false, false, vaccineList), true);

        fillValue((TextView) convertView.findViewById(R.id.child_last_visit_date), lastVaccine);

        List<Alert> alertlist_for_client = alertService.findByEntityIdAndAlertNames(pc.entityId(),
                "BCG", "OPV 0", "Penta 1", "OPV 1","PCV 1", "Penta 2", "OPV 2", "PCV 2",
                "Penta 3", "OPV 3", "PCV 3", "IPV", "Measles 1", "Measles2",
                "bcg", "opv0", "penta1", "opv1","pcv1", "penta2", "opv2", "pcv2",
                "penta3", "opv3", "pcv3", "ipv", "measles1", "measles2");

        if(!hasAnyEmptyValue(pc.getColumnmaps(), vaccineList)){
            deactivateNextVaccine("Fully Immunized", "", R.color.alert_na, convertView);
        }
        else if(agey >= 5 && hasAnyEmptyValue(pc.getColumnmaps(), vaccineList)){
            deactivateNextVaccine("Partially Immunized", "", R.color.alert_na, convertView);
        }
        else if (alertlist_for_client.size() == 0) {
            deactivateNextVaccine("Waiting", "", R.color.alert_na, convertView);
        }
        else {
            int supercede = 0;
            for (int i = 0; i < alertlist_for_client.size(); i++) {
                String vaccine = alertlist_for_client.get(i).scheduleName().replaceAll(" ", "");
                String dueDate = alertlist_for_client.get(i).startDate();
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal") && supercede <= 2) {
                    supercede = 2;
                    activateNextVaccine(dueDate, vaccine, Color.WHITE, R.color.alert_normal, onClickListener, client, convertView);
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming") && supercede <= 1) {
                    supercede = 1;

                    int d = Days.daysBetween(new DateTime(dueDate), DateTime.now()).getDays();
                    if(d <= 4){
                        activateNextVaccine(dueDate, vaccine, Color.BLACK, R.color.alert_upcoming, onClickListener, client, convertView);
                    }
                    deactivateNextVaccine(vaccine, dueDate, R.color.alert_upcoming, convertView);
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent") && supercede <= 3) {
                    supercede = 3;

                    activateNextVaccine(dueDate, vaccine, Color.WHITE, R.color.alert_urgent, onClickListener, client, convertView);
                }
                if (alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired") && supercede == 0) {
                    deactivateNextVaccine(vaccine + " Expired", alertlist_for_client.get(i).expiryDate(), R.color.alert_expired, convertView);
                }
            }
        }

        setProfiePic(convertView.getContext(), (ImageView) convertView.findViewById(R.id.child_profilepic), client.entityId(), false);

        convertView.findViewById(R.id.child_profile_info_layout).setTag(client);
        convertView.findViewById(R.id.child_profile_info_layout).setOnClickListener(onClickListener);

        convertView.setLayoutParams(clientViewLayoutParams);
        return convertView;
    }

    private void deactivateNextVaccine(String vaccineViewText, String vaccineDateText, int color, View convertView){
        fillValue((TextView) convertView.findViewById(R.id.child_next_visit_vaccine), vaccineViewText);
        ((TextView) convertView.findViewById(R.id.child_next_visit_date)).setText(convertDateFormat(vaccineDateText, true));
        convertView.findViewById(R.id.child_next_visit_holder).setBackgroundColor(context.getResources().getColor(color));
    }

    private void activateNextVaccine(String dueDate, String vaccine, int foreColor, int backColor, View.OnClickListener onClickListener,
                                     SmartRegisterClient client, View convertView){
        fillValue((TextView) convertView.findViewById(R.id.child_next_visit_vaccine), vaccine);
        fillValue((TextView) convertView.findViewById(R.id.child_next_visit_date), convertDateFormat(dueDate, true));
        ((TextView) convertView.findViewById(R.id.child_next_visit_vaccine)).setTextColor(foreColor);
        ((TextView) convertView.findViewById(R.id.child_next_visit_date)).setTextColor(foreColor);

        convertView.findViewById(R.id.child_next_visit_holder).setBackgroundColor(context.getResources().getColor(backColor));
        convertView.findViewById(R.id.child_next_visit_holder).setOnClickListener(onClickListener);
        convertView.findViewById(R.id.child_next_visit_holder).setTag(client);
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