package org.ei.opensrp.vaccinator.household;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.util.StringUtil;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.adapter.HouseholdMemberAdapter;
import org.ei.opensrp.vaccinator.application.template.DetailActivity;
import org.joda.time.DateTime;
import org.joda.time.Years;

import java.util.ArrayList;
import java.util.List;

import static util.Utils.convertDateFormat;
import static util.Utils.getDataRow;
import static util.Utils.getValue;
import static util.Utils.nonEmptyValue;

/**
 * Created by Safwan on 4/21/2016.
 */
public class HouseholdDetailActivity extends DetailActivity {

    String sql;
   // private FormController formController1;

   /* public HouseholdDetailActivity(FormController formController){
        super();
        this.formController1 = formController;
    }*/

    ArrayAdapter<CommonPersonObject> arrayAdapter;
    List<HouseholdMemberDetails> memberDetails = new ArrayList<HouseholdMemberDetails>();

    @Override
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
    }

    @Override
    protected void generateView() {
        TableLayout dt = (TableLayout) findViewById(R.id.household_detail_info_table1);

        //setting value in Household basic information textviews

        sql = "select * from pkindividual where relationalid = '" + client.getCaseId() + "'";
        List<CommonPersonObject> individualList = Context.getInstance().allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(sql, new String[]{}, "pkindividual");

        TableRow tr = getDataRow(this, "Person ID", getValue(client.getColumnmaps(), "person_id_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Name", StringUtil.humanizeAndDoUPPERCASE(getValue(client, "first_name_hhh", false) + " " + getValue(client, "last_name_hhh", true)), null);
        dt.addView(tr);

        int age = Years.yearsBetween(new DateTime(getValue(client, "calc_dob_confirm_hhh", false)), DateTime.now()).getYears();
        tr = getDataRow(this, "DOB (Age)", convertDateFormat(getValue(client, "calc_dob_confirm_hhh", false), true) + " (" + age + " years)", null);
        dt.addView(tr);

        tr = getDataRow(this, "Gender", getValue(client, "gender_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Ethnicity", getValue(client, "ethnicity_hhh", true), null);
        dt.addView(tr);

        tr = getDataRow(this, "Contact Phone Number", getValue(client, "contact_phone_number_hhh", true), null);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) findViewById(R.id.household_detail_info_table2);
        tr = getDataRow(this, "Household ID", getValue(client.getColumnmaps(), "existing_household_id", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Number of Members", "" + individualList.size() + "", null);
        dt2.addView(tr);
        tr = getDataRow(this, "Source of Drinking Water", getValue(client, "water_source", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Latrine System", getValue(client, "latrine_system", true), null);
        dt2.addView(tr);
        tr = getDataRow(this, "Address", getValue(client, "address1", true)
                + ", \nUC: " + getValue(client, "union_council", true)
                + ", \nTown: " + getValue(client, "town", true)
                + ", \nCity: " + getValue(client, "city_village", true)
                /*+ ", \nProvince: " + getValue(client, "province", true)*/, null);
        dt2.addView(tr);


        arrayAdapter = new ArrayAdapter<CommonPersonObject>
                (this, android.R.layout.simple_list_item_1, individualList);

        ListView list = (ListView) findViewById(R.id.individualList);

        TextView addMemberText = (TextView) findViewById(R.id.addMember);
        addMemberText.setText("Other Members (" + individualList.size() + ")");

        for (CommonPersonObject individual : individualList) {
            HouseholdMemberDetails member = new HouseholdMemberDetails();
            int memberAge = Years.yearsBetween(new DateTime(getValue(individual.getDetails(), "calc_dob_confirm", false)), DateTime.now()).getYears();
            //tr = getDataRow(this, "DOB (Age)", convertDateFormat(getValue(individual.getDetails(), "calc_dob_confirm", false), true) + " (" + age + " years)", null);

            if(memberAge < 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.child_boy_infant);
            else if(memberAge > 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("male"))
                member.setMemberImageId(R.drawable.household_profile);
            else if(memberAge < 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.child_girl_infant);
            else if(memberAge > 10 && getValue(individual.getDetails(), "gender", false).equalsIgnoreCase("female"))
                member.setMemberImageId(R.drawable.pk_woman_icon);


            member.setMemberId(getValue(individual.getDetails(), "person_id", false));
            member.setMemberName(StringUtil.humanizeAndDoUPPERCASE(getValue(individual.getDetails(), "first_name", false) + " " + getValue(individual.getDetails(), "last_name", false)));
            member.setMemberRelationWithHousehold(getValue(individual.getDetails(), "relationship", false));
            member.setMemberAge(convertDateFormat(getValue(individual.getDetails(), "calc_dob_confirm", false), true) + " (" + memberAge + " years)");
            memberDetails.add(member);
        }
        list.setAdapter(new HouseholdMemberAdapter(HouseholdDetailActivity.this, memberDetails));

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
    }




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

    @Override
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
    }
}
