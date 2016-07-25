package org.ei.opensrp.vaccinator.household;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.adapter.HouseholdMemberAdapter;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.List;

import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getDataRowHousehold;
import static util.Utils.getValue;
import static util.Utils.setProfiePic;

/**
 * Created by Safwan on 4/21/2016.
 */

//SAFWAN
public class HouseholdDetailActivity extends SecuredFragment {


    public static CommonPersonObjectClient householdClient;

    private View mView;
    ArrayAdapter<CommonPersonObject> arrayAdapter;
    List<HouseholdMemberDetails> memberDetails = new ArrayList<HouseholdMemberDetails>();

    String sql;

    public static boolean existence;
    //Context context = getActivity().getBaseContext();
    //public static CommonPersonObjectClient householdClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_household_detail, container, false);
        mView = view;

        return view;

    }

    //SAFWAN
    public void initialize(){
        //onResumption();
        memberDetails.clear();
        TableLayout dt = (TableLayout) mView.findViewById(R.id.household_detail_info_table1);
        dt.removeAllViews();
        android.content.Context context = getActivity().getApplicationContext();

        //setting value in Household basic information textviews

        sql = "select * from pkindividual where relationalid = '" + householdClient.getCaseId() + "'";
        List<CommonPersonObject> individualList = Context.getInstance().allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(sql, new String[]{}, "pkindividual");
        sql = "select * from pkwoman";
        List<CommonPersonObject> womanList = Context.getInstance().allCommonsRepositoryobjects("pkwoman").customQueryForCompleteRow(sql, new String[]{}, "pkwoman");
        sql = "select * from pkchild";
        List<CommonPersonObject> childList = Context.getInstance().allCommonsRepositoryobjects("pkchild").customQueryForCompleteRow(sql, new String[]{}, "pkchild");


        TableRow tr = getDataRowHousehold(context, "Person ID", getValue(householdClient.getColumnmaps(), "person_id_hhh", true), null);
        dt.addView(tr);

        tr = getDataRowHousehold(context, "Name", StringUtils.capitalize(getValue(householdClient, "first_name_hhh", false)) + " " + StringUtils.capitalize(getValue(householdClient, "last_name_hhh", false)), null);
        dt.addView(tr);

        int age = Years.yearsBetween(new DateTime(getValue(householdClient, "calc_dob_confirm_hhh", false)), DateTime.now()).getYears();
        tr = getDataRowHousehold(context, "DOB (Age)", convertDateFormat(getValue(householdClient, "calc_dob_confirm_hhh", false), true) + " (" + age + " years)", null);
        dt.addView(tr);

        tr = getDataRowHousehold(context, "Gender", getValue(householdClient, "gender_hhh", true), null);
        dt.addView(tr);

        tr = getDataRowHousehold(context, "Ethnicity", getValue(householdClient, "ethnicity_hhh", true), null);
        dt.addView(tr);

        tr = getDataRowHousehold(context, "Contact", getValue(householdClient, "contact_phone_number_hhh", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) mView.findViewById(R.id.household_detail_info_table2);
        dt2.removeAllViews();
        tr = getDataRowHousehold(context, "Household ID", getValue(householdClient.getColumnmaps(), "existing_household_id", true), null);
        dt2.addView(tr);
        tr = getDataRowHousehold(context, "Number of Members", "" + individualList.size() + "", null);
        dt2.addView(tr);
        tr = getDataRowHousehold(context, "Source of Drinking Water", getValue(householdClient, "water_source", true), null);
        dt2.addView(tr);
        tr = getDataRowHousehold(context, "Latrine System", getValue(householdClient, "latrine_system", true), null);
        dt2.addView(tr);
        tr = getDataRowHousehold(context, "Address", getValue(householdClient, "address1", true)
                + ", \nUC: " + getValue(householdClient, "union_council", true)
                + ", \nTown: " + getValue(householdClient, "town", true)
                + ", \nCity: " + getValue(householdClient, "city_village", true)
                /*+ ", \nProvince: " + getValue(client, "province", true)*/, null);
        dt2.addView(tr);

        arrayAdapter = new ArrayAdapter<CommonPersonObject>
                (context, android.R.layout.simple_list_item_1, individualList);

        ListView list = (ListView) mView.findViewById(R.id.individualList);

        TextView addMemberText = (TextView) mView.findViewById(R.id.addMember);
        addMemberText.setText("Other Members (" + individualList.size() + ")");

        for (CommonPersonObject individual : individualList) {
            HouseholdMemberDetails member = new HouseholdMemberDetails();
            int memberAge = Years.yearsBetween(new DateTime(getValue(individual.getDetails(), "calc_dob_confirm", false)), DateTime.now()).getYears();
            //tr = getDataRow(this, "DOB (Age)", convertDateFormat(getValue(individual.getDetails(), "calc_dob_confirm", false), true) + " (" + age + " years)", null);

            if (memberAge < 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.child_boy_infant);
            else if (memberAge > 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.household_profile);
            else if (memberAge < 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.child_girl_infant);
            else if (memberAge > 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.pk_woman_icon);

            setProfiePic(mView.getContext(), (ImageView) mView.findViewById(R.id.household_profilepic), householdClient.entityId(), null);


            member.setMemberId(getValue(individual.getDetails(), "person_id", false));
            member.setMemberName(StringUtils.capitalize(getValue(individual.getDetails(), "first_name", false)) + " " + StringUtils.capitalize(getValue(individual.getDetails(), "last_name", false)));
            member.setMemberRelationWithHousehold(getValue(individual.getDetails(), "relationship", false));
            member.setMemberAge(convertDateFormat(getValue(individual.getDetails(), "calc_dob_confirm", false), true) + " (" + memberAge + " years)");


            if(!womanList.isEmpty()) {
                for (CommonPersonObject woman : womanList) {
                    if (woman.getDetails().get("existing_program_client_id").equals(individual.getColumnmaps().get("existing_program_client_id"))) {
                        member.setMemberExists(true);
                        member.setClient(woman);
                    }
                }
            }

            if(!childList.isEmpty()) {
                for (CommonPersonObject child : childList) {
                    if (child.getDetails().get("existing_program_client_id").equals(individual.getColumnmaps().get("existing_program_client_id"))) {
                        member.setMemberExists(true);
                        member.setClient(child);
                    }
                }
            }
            memberDetails.add(member);
        }
        list.setAdapter(new HouseholdMemberAdapter(this, context, memberDetails));
    }





    @Override
    protected void onCreation() {

    }

    @Override
    protected void onResumption() {
        Log.d("resume", "resume");
    }



    /*@Override
    protected int layoutResId() {
        return R.layout.household_detail_activity;
    }

    @Override
    protected String pageTitle() {
        return "Household Details";
    }

    @Override
    protected String titleBarId() {
        return getEntityIdentifier();
    }*/

   /* public static void startDetailActivity(android.content.Context context, CommonPersonObjectClient clientobj, Class<? extends DetailActivity> detailActivity) {
        householdClient = clientobj;
        context.startActivity(new Intent(context, detailActivity));
    }*/

    /*@Override
    protected void generateView() {*/
        /*TableLayout dt = (TableLayout) findViewById(R.id.household_detail_info_table1);

        //setting value in Household basic information textviews

        sql = "select * from pkindividual where relationalid = '" + householdClient.getCaseId() + "'";
        List<CommonPersonObject> individualList = Context.getInstance().allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(sql, new String[]{}, "pkindividual");
        sql = "select * from pkwoman";
        List<CommonPersonObject> womanList = Context.getInstance().allCommonsRepositoryobjects("pkwoman").customQueryForCompleteRow(sql, new String[]{}, "pkwoman");
        sql = "select * from pkchild";
        List<CommonPersonObject> childList = Context.getInstance().allCommonsRepositoryobjects("pkchild").customQueryForCompleteRow(sql, new String[]{}, "pkchild");

        TableRow tr = getDataRow(context, "Person ID", getValue(householdClient.getColumnmaps(), "person_id_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(context, "Name", StringUtil.humanizeAndDoUPPERCASE(getValue(householdClient, "first_name_hhh", false) + " " + getValue(householdClient, "last_name_hhh", true)), null);
        dt.addView(tr);

        int age = Years.yearsBetween(new DateTime(getValue(householdClient, "calc_dob_confirm_hhh", false)), DateTime.now()).getYears();
        tr = getDataRow(context, "DOB (Age)", convertDateFormat(getValue(householdClient, "calc_dob_confirm_hhh", false), true) + " (" + age + " years)", null);
        dt.addView(tr);

        tr = getDataRow(context, "Gender", getValue(householdClient, "gender_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(context, "Ethnicity", getValue(householdClient, "ethnicity_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(context, "Contact Phone Number", getValue(householdClient, "contact_phone_number_hhh", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.household_detail_info_table2);
        tr = getDataRow(context, "Household ID", getValue(householdClient.getColumnmaps(), "existing_household_id", true), null);
        dt2.addView(tr);
        tr = getDataRow(context, "Number of Members", "" + individualList.size() + "", null);
        dt2.addView(tr);
        tr = getDataRow(context, "Source of Drinking Water", getValue(householdClient, "water_source", true), null);
        dt2.addView(tr);
        tr = getDataRow(context, "Latrine System", getValue(householdClient, "latrine_system", true), null);
        dt2.addView(tr);
        tr = getDataRow(context, "Address", getValue(householdClient, "address1", true)
                + ", \nUC: " + getValue(householdClient, "union_council", true)
                + ", \nTown: " + getValue(householdClient, "town", true)
                + ", \nCity: " + getValue(householdClient, "city_village", true)
                /*+ ", \nProvince: " + getValue(client, "province", true), null);
        dt2.addView(tr);


        arrayAdapter = new ArrayAdapter<CommonPersonObject>
                (context, android.R.layout.simple_list_item_1, individualList);

        ListView list = (ListView) findViewById(R.id.individualList);

        TextView addMemberText = (TextView) findViewById(R.id.addMember);
        addMemberText.setText("Other Members (" + individualList.size() + ")");

        for (CommonPersonObject individual : individualList) {
            HouseholdMemberDetails member = new HouseholdMemberDetails();
            int memberAge = Years.yearsBetween(new DateTime(getValue(individual.getDetails(), "calc_dob_confirm", false)), DateTime.now()).getYears();
            //tr = getDataRow(this, "DOB (Age)", convertDateFormat(getValue(individual.getDetails(), "calc_dob_confirm", false), true) + " (" + age + " years)", null);

            if (memberAge < 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.child_boy_infant);
            else if (memberAge > 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.household_profile);
            else if (memberAge < 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.child_girl_infant);
            else if (memberAge > 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.pk_woman_icon);


            member.setMemberId(getValue(individual.getDetails(), "person_id", false));
            member.setMemberName(StringUtil.humanizeAndDoUPPERCASE(getValue(individual.getDetails(), "first_name", false) + " " + getValue(individual.getDetails(), "last_name", false)));
            member.setMemberRelationWithHousehold(getValue(individual.getDetails(), "relationship", false));
            member.setMemberAge(convertDateFormat(getValue(individual.getDetails(), "calc_dob_confirm", false), true) + " (" + memberAge + " years)");
            memberDetails.add(member);

            for (CommonPersonObject woman : womanList) {
                if (woman.getDetails().get("existing_program_client_id") != individual.getColumnmaps().get("existing_program_client_id")) {

                }
            }
        }
        list.setAdapter(new HouseholdMemberAdapter(context, memberDetails));*/



       /* Button addMember = (Button) findViewById(R.id.btnAddMember);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> map = new HashMap<>();
                //CommonPersonObjectClient client = (CommonPersonObjectClient) view.getTag();
                map.putAll(followupOverrides(client));
                map.putAll(Utils.providerDetails());
                parentSmartRegisterFragment.startFollowupForm("new_member_registration", client, map, SmartRegisterFragment.ByColumnAndByDetails.byDefault);
            }
        });*/
  //  }


/*    protected void startFollowupForm(String formName, SmartRegisterClient client, HashMap<String, String> overrideStringmap, SmartRegisterFragment.ByColumnAndByDetails byColumnAndByDetails) {
        formController1 = HouseholdSmartRegisterFragment.householdFormController;
        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            formController1.startFormActivity(formName, client.entityId(), null);
        } else {
            overrideStringmap.putAll(providerOverrides());

            String overrides = Utils.overridesToString(overrideStringmap, client, byColumnAndByDetails);
            FieldOverrides fieldOverrides = new FieldOverrides(overrides);
            org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides.getJSONString());
            formController1.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
        }
    }*/

//    protected Map<String, String> providerOverrides(){
//        return Utils.providerDetails();
//    }


    /*private Map<String, String> followupOverrides(CommonPersonObjectClient client){
        Map<String, String> map = new HashMap<>();
        map.put("existing_address1", getValue(client.getDetails(), "adderss1", true));
        map.put("existing_union_council", getValue(client.getDetails(), "union_council", true));
        map.put("existing_town", getValue(client.getDetails(), "town", true));
        map.put("existing_city_village", getValue(client.getDetails(), "city_village", true));
        map.put("existing_province", getValue(client.getDetails(), "province", true));
        map.put("existing_landmark", getValue(client.getDetails(), "landmark", true));

        map.put("existing_union_councilname", getValue(client.getDetails(), "union_council", true));
        map.put("existing_townname", getValue(client.getDetails(), "town", true));
        map.put("existing_city_villagename", getValue(client.getDetails(), "city_village", true));
        map.put("existing_provincename", getValue(client.getDetails(), "province", true));

        map.put("existing_first_name_hhh", getValue(client.getDetails(), "first_name", true));
        map.put("existing_last_name_hhh", getValue(client.getDetails(), "last_name", true));
        /*map.put("existing_gender", getValue(client.getDetails(), "gender", true));
        map.put("existing_mother_name", getValue(client.getDetails(), "mother_name", true));
        map.put("existing_father_name", getValue(client.getDetails(), "father_name", true));
        map.put("existing_birth_date", getValue(client.getDetails(), "dob", false));
        map.put("existing_ethnicity", getValue(client.getDetails(), "ethnicity", true));
        map.put("existing_client_reg_date", getValue(client.getDetails(), "client_reg_date", false));
        map.put("existing_epi_card_number", getValue(client.getDetails(), "epi_card_number", false));
        map.put("existing_child_was_suffering_from_a_disease_at_birth", getValue(client.getDetails(), "child_was_suffering_from_a_disease_at_birth", true));
        map.put("existing_reminders_approval", getValue(client.getDetails(), "reminders_approval", false));
        map.put("existing_contact_phone_number", getValue(client.getDetails(), "contact_phone_number", false));


        return map;
    }*/

   /* @Override
    protected Class onBackActivity() {
        return HouseholdSmartRegisterActivity.class;
    }

    @Override
    protected Integer profilePicContainerId() {
        return R.id.household_profilepic;
    }

    @Override
    protected Integer defaultProfilePicResId() {
        return R.drawable.household_profile;
    }

    @Override
    protected String bindType() {
        return "pkhousehold";
    }

    @Override
    protected boolean allowImageCapture() {
        return true;
    }

    public String getEntityIdentifier() {
        return nonEmptyValue(client.getColumnmaps(), true, false, "existing_household_id");
    }*/


    /*protected View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.household_detail_activity, container, false);
        mView = view;
        return view;
    }*/

    /*@Override
    protected void onCreation() {

    }

    // @Override
    protected void onResumption() {

    }*/
}
