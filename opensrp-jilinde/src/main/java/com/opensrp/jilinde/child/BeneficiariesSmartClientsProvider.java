package com.opensrp.jilinde.child;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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

        profileinfolayout.setOnClickListener(onClickListener);
        profileinfolayout.setTag(smartRegisterClient);

        final CommonPersonObjectClient pc = (CommonPersonObjectClient) smartRegisterClient;

        name.setText(humanize(pc.getColumnmaps().get("name") != null ? pc.getColumnmaps().get("name") : ""));
        location.setText(humanize(pc.getColumnmaps().get("location") != null ? pc.getColumnmaps().get("location") : ""));

        String age = pc.getColumnmaps().get("age") != null ? pc.getColumnmaps().get("age") : "";
        String gender = pc.getColumnmaps().get("gender") != null ? pc.getColumnmaps().get("gender") : "";

        String ageGenderString = "";
        if (StringUtils.isNotBlank(age) && StringUtils.isNotBlank(gender)) {
            ageGenderString = age.trim() + ", " + gender.trim();
        } else if (StringUtils.isNotBlank(age)) {
            ageGenderString = age;
        } else if (StringUtils.isNotBlank(gender)) {
            ageGenderString = gender;
        }

        ageGender.setText(ageGenderString);

        contact.setText(pc.getColumnmaps().get("phone_no") != null ? pc.getColumnmaps().get("phone_no") : "");

        eDate.setText(pc.getColumnmaps().get("enrollment_date") != null ? pc.getColumnmaps().get("enrollment_date") : "");
        eSite.setText(humanize(pc.getColumnmaps().get("site") != null ? pc.getColumnmaps().get("site") : ""));

        //constructRiskFlagView(pc, itemView);

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


    private void constructRiskFlagView(CommonPersonObjectClient pc, View itemView) {
//        AllCommonsRepository allancRepository = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("mcaremother");
//        CommonPersonObject ancobject = allancRepository.findByCaseID(pc.entityId());
//        AllCommonsRepository allelcorep = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("elco");
//        CommonPersonObject elcoparent = allelcorep.findByCaseID(ancobject.getRelationalId());

        ImageView hrp = (ImageView) itemView.findViewById(R.id.hrp);
        ImageView hp = (ImageView) itemView.findViewById(R.id.hr);
        ImageView vg = (ImageView) itemView.findViewById(R.id.vg);

        vg.setVisibility(View.GONE);


        hrp.setVisibility(View.GONE);

        hp.setVisibility(View.GONE);


//        if(pc.getDetails().get("FWWOMAGE")!=null &&)

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
