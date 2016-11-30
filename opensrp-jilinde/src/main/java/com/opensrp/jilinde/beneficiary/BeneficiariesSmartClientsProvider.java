package com.opensrp.jilinde.beneficiary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.SmartRegisterCLientsProviderForCursorAdapter;

import com.opensrp.jilinde.R;

import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by user on 2/12/15.
 */
public class BeneficiariesSmartClientsProvider implements SmartRegisterCLientsProviderForCursorAdapter {

    private final LayoutInflater inflater;
    private final Context context;
    private final View.OnClickListener onClickListener;

    private final int txtColorBlack;
    private final AbsListView.LayoutParams clientViewLayoutParams;
    private final AlertService alertService;


    public BeneficiariesSmartClientsProvider(Context context,
                                             View.OnClickListener onClickListener,
                                             AlertService alertService) {
        this.onClickListener = onClickListener;
        this.alertService = alertService;
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        clientViewLayoutParams = new AbsListView.LayoutParams(MATCH_PARENT,
                (int) context.getResources().getDimension(org.ei.opensrp.R.dimen.list_item_height));
        txtColorBlack = context.getResources().getColor(org.ei.opensrp.R.color.text_black);
    }

    @Override
    public void getView(final SmartRegisterClient smartRegisterClient, View convertView) {
        View itemView = convertView;

        LinearLayout profileinfolayout = (LinearLayout) itemView.findViewById(R.id.profile_info_layout);

        TextView name = (TextView) itemView.findViewById(R.id.name);
        TextView location = (TextView) itemView.findViewById(R.id.location);
        TextView ageGender = (TextView) itemView.findViewById(R.id.age_gender);

        TextView contact  = (TextView) itemView.findViewById(R.id.contact);

        TextView eDate  = (TextView) itemView.findViewById(R.id.enrollment_date);
        TextView eSite  = (TextView) itemView.findViewById(R.id.enrollment_site);

        TextView cDate  = (TextView) itemView.findViewById(R.id.clinic_date);
        TextView cSite  = (TextView) itemView.findViewById(R.id.clinic_site);
        FrameLayout editBtn  = (FrameLayout) itemView.findViewById(R.id.follow_up);
        editBtn.setOnClickListener(onClickListener);
        editBtn.setTag(smartRegisterClient);



        ImageView profile = (ImageView) itemView.findViewById(R.id.profilepic);

        //profileinfolayout.setOnClickListener(onClickListener);
        //profileinfolayout.setTag(smartRegisterClient);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

        String gender = pc.getColumnmaps().get("gender") != null ? pc.getColumnmaps().get("gender") : "";
        String gChar= "";

        if(gender.equals("female")){
            gChar = "F";
            profile.setImageResource(R.mipmap.african_female);
        } else if(gender.equals("male")){
            gChar= "M";
            profile.setImageResource(R.mipmap.african_male);
        }

        name.setText(humanize(pc.getColumnmaps().get("name") != null ? pc.getColumnmaps().get("name") : ""));
        location.setText(humanize(pc.getColumnmaps().get("location") != null ? pc.getColumnmaps().get("location") : ""));

        String age = pc.getColumnmaps().get("age") != null ? pc.getColumnmaps().get("age") : "";

        String ageGenderString = "";
        if (StringUtils.isNotBlank(age) && StringUtils.isNotBlank(gender)) {
            ageGenderString = age.trim() + " " + gChar;
        } else if (StringUtils.isNotBlank(age)) {
            ageGenderString = age;
        } else if (StringUtils.isNotBlank(gender)) {
            ageGenderString = gChar;
        }

        ageGender.setText(ageGenderString);

        contact.setText(pc.getColumnmaps().get("phone_no") != null ? pc.getColumnmaps().get("phone_no") : "");

        eDate.setText(pc.getColumnmaps().get("enrollment_date") != null ? pc.getColumnmaps().get("enrollment_date") : "");
        eSite.setText(humanize(pc.getColumnmaps().get("site") != null ? pc.getColumnmaps().get("site") : ""));

        cDate.setText(pc.getColumnmaps().get("visit_date") != null ? pc.getColumnmaps().get("visit_date") : "");
        cSite.setText(humanize(pc.getColumnmaps().get("clinic_site") != null ? pc.getColumnmaps().get("clinic_site") : ""));

        itemView.setLayoutParams(clientViewLayoutParams);
    }


    private Long age(CommonPersonObjectClient ancclient) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date edd_date = format.parse(ancclient.getColumnmaps().get("child_dob") != null ? ancclient.getColumnmaps().get("child_dob") : "");
            Calendar thatDay = Calendar.getInstance();
            thatDay.setTime(edd_date);

            Calendar today = Calendar.getInstance();

            long diff = today.getTimeInMillis() - thatDay.getTimeInMillis();

            long days = diff / (24 * 60 * 60 * 1000);

            return days;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                                              FilterOption searchFilter, SortOption sortOption) {
        return null;
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

    @Override
    public View inflatelayoutForCursorAdapter() {
        return (ViewGroup) inflater().inflate(R.layout.smart_register_jilinde_client, null);
    }

    class alertTextandStatus {
        String alertText, alertstatus;

        public alertTextandStatus(String alertText, String alertstatus) {
            this.alertText = alertText;
            this.alertstatus = alertstatus;
        }

        public String getAlertText() {
            return alertText;
        }

        public void setAlertText(String alertText) {
            this.alertText = alertText;
        }

        public String getAlertstatus() {
            return alertstatus;
        }

        public void setAlertstatus(String alertstatus) {
            this.alertstatus = alertstatus;
        }
    }
}
