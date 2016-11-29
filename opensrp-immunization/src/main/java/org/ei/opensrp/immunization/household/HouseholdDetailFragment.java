package org.ei.opensrp.immunization.household;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.ByColumnAndByDetails;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.ei.opensrp.view.template.DetailFragment;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.ei.opensrp.util.Utils.ageInYears;
import static org.ei.opensrp.util.Utils.convertDateFormat;
import static org.ei.opensrp.util.Utils.fillValue;
import static org.ei.opensrp.util.Utils.*;
import static org.ei.opensrp.util.Utils.getValue;
import static org.ei.opensrp.util.Utils.setProfiePic;


/**
 * Created by Safwan on 4/21/2016.
 */

//SAFWAN
public class HouseholdDetailFragment extends DetailFragment {
    ArrayAdapter<CommonPersonObject> arrayAdapter;
    List<HouseholdMemberDetails> memberDetails = new ArrayList<HouseholdMemberDetails>();

    @Override
    protected int layoutResId() {
        return R.layout.fragment_household_detail;
    }

    @Override
    protected String pageTitle() {
        return "Household Details";
    }

    @Override
    protected String titleBarId() {
        return client.getColumnmaps().get("existing_household_id");
    }

    @Override
    protected Integer profilePicContainerId() {
        return R.id.household_profilepic;
    }

    @Override
    protected Integer defaultProfilePicResId() {
        return client!=null&&client.getColumnmaps().get("gender")!=null&&client.getColumnmaps().get("gender").equalsIgnoreCase("female")?R.drawable.pk_woman_avtar:R.drawable.household_profile;
    }

    @Override
    protected String bindType() {
        return "pkhousehold";
    }

    @Override
    protected boolean allowImageCapture() {
        return false;
    }

    @Override
    protected void generateView() {
        memberDetails.clear();
        //todo
        ((TextView)  currentView.findViewById(org.ei.opensrp.R.id.details_id_label)).setText(getValue(client.getColumnmaps(), "household_id", true));

        TableLayout dt = (TableLayout) currentView.findViewById(R.id.household_detail_info_table1);
        dt.removeAllViews();
        android.content.Context context = getActivity().getApplicationContext();

        //setting value in Household basic information textviews
        String sql = "select * from pkindividual where relationalid = '" + client.getCaseId() + "'";
        List<CommonPersonObject> individualList = Context.getInstance().allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(sql, new String[]{}, "pkindividual");

        TableRow tr = getEvenWidthDataRow(context, "Household Member ID", getValue(client.getColumnmaps(), "household_member_id", true)+" ("+individualList.size()+" other members)", null);
        dt.addView(tr);

        tr = getEvenWidthDataRow(context, "Program Client ID", getValue(client.getColumnmaps(), "program_client_id", true), null);
        dt.addView(tr);

        tr = getEvenWidthDataRow(context, "Name",
                StringUtils.capitalize(getValue(client.getColumnmaps(), "first_name", true)) +
                        " " + StringUtils.capitalize(getValue(client.getColumnmaps(), "last_name", false))
                        +" ("+getValue(client.getColumnmaps(), "gender", true)+")", null);
        dt.addView(tr);

        int age = ageInYears(client, "dob", ByColumnAndByDetails.byColumn, true);
        tr = getEvenWidthDataRow(context, "DoB (Age)", convertDateFormat(age<0?"No DoB":getValue(client.getColumnmaps(), "dob", false), true)+" ("+age+ " years)", null);
        dt.addView(tr);

        tr = getEvenWidthDataRow(context, "Ethnicity", getValue(client.getColumnmaps(), "ethnicity", true), null);
        dt.addView(tr);

        tr = getEvenWidthDataRow(context, "Contact", getValue(client.getColumnmaps(), "contact_phone_number", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) currentView.findViewById(R.id.household_detail_info_table2);
        dt2.removeAllViews();
        tr = getEvenWidthDataRow(context, "Household ID", getValue(client.getColumnmaps(), "household_id", true), null);
        dt2.addView(tr);
        tr = getEvenWidthDataRow(context, "Drinking Water Source", getValue(client.getColumnmaps(), "water_source", true), null);
        dt2.addView(tr);
        tr = getEvenWidthDataRow(context, "Latrine System", getValue(client.getColumnmaps(), "latrine_system", true), null);
        dt2.addView(tr);
        tr = getEvenWidthDataRow(context, "Address",
                getValue(client.getColumnmaps(), "address1", true) + ", \n" +
                        getValue(client.getColumnmaps(), "union_council", true).replace("Uc", "UC") + ", " +
                        getValue(client.getColumnmaps(), "town", true) + ",\n" +
                        getValue(client.getColumnmaps(), "city_village", true) + ", " +
                        getValue(client.getColumnmaps(), "province", true), null);
        dt2.addView(tr);

        setProfiePic(currentView.getContext(), (ImageView) currentView.findViewById(R.id.household_profilepic), client.entityId(), null);

        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, individualList);

        ListView list = (ListView) currentView.findViewById(R.id.individualList);

        TextView addMemberText = (TextView) currentView.findViewById(R.id.addMember);
        addMemberText.setText("Other Members (" + individualList.size() + ")");

        for (CommonPersonObject individual : individualList) {
            HouseholdMemberDetails member = new HouseholdMemberDetails();
            int memberAge = ageInYears(individual, "dob", ByColumnAndByDetails.byColumn, true);

            String gender = getValue(individual.getColumnmaps(), "gender", false);
            if (memberAge < 7 && gender.equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.child_boy_infant);
            else if (memberAge < 7 && gender.equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.child_girl_infant);
            else if (memberAge >= 7 && gender.equalsIgnoreCase("male")) {
                member.setMemberImageId(R.drawable.household_profile);
            }
            else if (memberAge >= 7 && gender.equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.pk_woman_icon);

            //refactor
            String programId = getValue(individual.getColumnmaps(), "program_client_id", false);
            member.setProgramId(programId);
            member.setMemberName(StringUtils.capitalize(getValue(individual.getColumnmaps(), "first_name", false)) + " " + StringUtils.capitalize(getValue(individual.getColumnmaps(), "last_name", false)));
            member.setMemberRelationWithHousehold(StringUtil.humanize(getValue(individual.getColumnmaps(), "relationship", true)));
            member.setMemberAge(convertDateFormat(getValue(individual.getColumnmaps(), "dob", true), true) + " (" + memberAge + " yr)");
            member.setMemberGender(getValue(individual.getColumnmaps(), "gender", false));
            member.setContact(getValue(individual.getColumnmaps(), "contact_phone_number", false));
            member.setClient(individual);

            member.setCantBeEnrolled(true);
            if (memberAge <= 5) {
                member.setCantBeEnrolled(false);
            }
            else if (memberAge >= 15 && memberAge <= 49 && gender.equalsIgnoreCase("female")) {
                member.setCantBeEnrolled(false);
            }

            List<CommonPersonObject> l = Context.getInstance().commonrepository("pkwoman").customQueryForCompleteRow("SELECT * FROM pkwoman WHERE id='"+individual.getCaseId()+"' OR program_client_id='" + programId + "'", null, "pkwoman");
            l.addAll(Context.getInstance().commonrepository("pkchild").customQueryForCompleteRow("SELECT * FROM pkchild WHERE id='"+individual.getCaseId()+"' OR  program_client_id='" + programId + "'", null, "pkchild"));

            if (l.size() > 0) {
                if (StringUtils.isBlank(programId)){
                    member.setProgramId(l.get(0).getColumnmaps().get("program_client_id"));
                }
                member.setMemberExists(true);
                member.setCantBeEnrolled(true);
                //member.getClient().getColumnmaps().putAll(l.get(0).getColumnmaps());
               //todo member.getClient().getDetails().putAll(l.get(0).getDetails());
            }
            memberDetails.add(member);
        }
        list.setAdapter(new HouseholdMemberAdapter(this, context, memberDetails));
    }
}
