package org.ei.opensrp.vaccinator.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.child.ChildSmartRegisterActivity;
import org.ei.opensrp.vaccinator.household.HouseholdMemberDetails;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.controller.ANMController;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.controller.NavigationController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.util.Utils.getValue;


/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdMemberAdapter extends ArrayAdapter<HouseholdMemberDetails> implements Serializable {

    private final Context context;
    private final List<HouseholdMemberDetails> list;
    private final Fragment fragment;
    private FormController formController;
    protected ANMController anmController;
    protected NavigationController navigationController;


    TextView memberId;
    TextView memberName;
    TextView memberAge;
    TextView memberRelation;
    TextView memberGender;
    ImageView memberImage;
    Button btnFollowup;
    Button btnEnrollment;
    CommonPersonObjectClient client;

    public HouseholdMemberAdapter(Fragment fragment, Context context, List<HouseholdMemberDetails> list){
        super(context, R.layout.list_individual, list);
        this.fragment = fragment;
        this.context = context;
        this.list = list;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.list_individual, parent, false);


        memberId = (TextView) row.findViewById(R.id.memberId);
        memberId.setText(list.get(position).memberId);

        memberName = (TextView) row.findViewById(R.id.memberName);
        memberName.setText(list.get(position).memberName);

        memberAge = (TextView) row.findViewById(R.id.memberDate);
        memberAge.setText(list.get(position).memberAge);

        memberRelation = (TextView) row.findViewById(R.id.relationship);
        memberRelation.setText(list.get(position).memberRelationWithHousehold);

        memberImage =(ImageView) row.findViewById(R.id.individual_profilepic);
        memberImage.setImageResource(list.get(position).memberImageId);

        memberGender = (TextView) row.findViewById(R.id.gender);
        memberGender.setText(list.get(position).memberGender);

        btnFollowup = (Button) row.findViewById(R.id.followup);
        btnEnrollment = (Button) row.findViewById(R.id.enrollment);


        if(list.get(position).isMemberExists()) {
            btnFollowup.setVisibility(View.VISIBLE);
            btnEnrollment.setVisibility(View.GONE);
        } else {
            btnFollowup.setVisibility(View.GONE);
            if(list.get(position).isCantBeEnrolled() == false)
                btnEnrollment.setVisibility(View.VISIBLE);
            else
                btnEnrollment.setVisibility(View.GONE);
        }


        btnFollowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonPersonObject person;
                HashMap<String, String> map = new HashMap<>();
                person = list.get(position).getClient();
                map.putAll(followupOverrides(person));

                client = new CommonPersonObjectClient(person.getCaseId(),person.getDetails(),person.getDetails().get("first_name"));

                client.setColumnmaps(person.getColumnmaps());
                client.setCaseId(person.getCaseId());

                final Intent intent;
                if(client.getColumnmaps().containsKey("tt1")){
                    intent = new Intent(fragment.getActivity(), WomanSmartRegisterActivity.class);
                } else {
                    intent = new Intent(fragment.getActivity(), ChildSmartRegisterActivity.class);
                }

                intent.putExtra("program_client_id", client.getColumnmaps().get("program_client_id").toString());

                fragment.getActivity().startActivity(intent);
                fragment.getActivity().finish();
            }
        });


        btnEnrollment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CommonPersonObject person;
                HashMap<String, String> map = new HashMap<>();
                String name[] = list.get(position).getMemberName().split(" ");
                String sql = "select * from pkindividual where relationalid = '" + HouseholdDetailFragment.householdClient.getCaseId() + "' and first_name = '" + name[0] + "' and last_name = '" + name[1] + "'";
                List<CommonPersonObject> individualList = org.ei.opensrp.Context.getInstance().allCommonsRepositoryobjects("pkindividual").customQueryForCompleteRow(sql, new String[]{}, "pkindividual");*/


                String fullAgeText = memberAge.getText().toString();
                String[] ageArray = fullAgeText.split("\\(");
                ageArray = ageArray[1].split(" ");
                int age = Integer.parseInt(ageArray[0]);

                Intent intent = null;
                if (age < 10)
                    intent = new Intent(fragment.getActivity(), ChildSmartRegisterActivity.class);
                else if (age > 10 && memberGender.getText().toString().equalsIgnoreCase("female"))
                    intent = new Intent(fragment.getActivity(), WomanSmartRegisterActivity.class);

                fragment.getActivity().startActivity(intent);
                fragment.getActivity().finish();

            }
        });

        return row;
    }

   /* protected void startFollowupForm(String formName, SmartRegisterClient client, HashMap<String, String> overrideStringmap, SmartRegisterFragment.ByColumnAndByDetails byColumnAndByDetails) {
        if (overrideStringmap == null) {
            org.ei.opensrp.util.Log.logDebug("overrides data is null");
            formController.startFormActivity(formName, client.entityId(), null);
        } else {
            overrideStringmap.putAll(Utils.providerDetails());

            String overrides = Utils.overridesToString(overrideStringmap, client, byColumnAndByDetails);
            FieldOverrides fieldOverrides = new FieldOverrides(overrides);
            org.ei.opensrp.util.Log.logDebug("fieldOverrides data is : " + fieldOverrides.getJSONString());
            formController.startFormActivity(formName, client.entityId(), fieldOverrides.getJSONString());
        }
    }*/


    /*public void startFormActivity(String formName, String entityId, String metaData) {
        //Log.v("fieldoverride", metaData);
        try {
            int formIndex = FormUtils.getIndexForFormName(formName, formNames) + 2; // add the offset
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
            displayFormFragment.showForm(mPager, formIndex, entityId, metaData, false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }*/

   /* protected SmartRegisterFragment getBaseFragment() {
        return new WomanSmartRegisterFragment(new FormController(WomanSmartRegisterActivity));
    }*/

    private Map<String, String> followupOverrides(CommonPersonObject client){
        Map<String, String> map = new HashMap<>();

        //map.put("relationalid", client.getCaseId());
        map.put("existing_first_name", getValue(client.getDetails(), "first_name", true));
        map.put("existing_last_name", getValue(client.getDetails(), "last_name", true));
        map.put("program_client_id", getValue(client.getColumnmaps(), "program_client_id", true));
        map.put("existing_union_councilname", getValue(client.getDetails(), "union_council", true));
        map.put("existing_townname", getValue(client.getDetails(), "town", true));
        map.put("existing_city_villagename", getValue(client.getDetails(), "city_village", true));
        map.put("existing_provincename", getValue(client.getDetails(), "province", true));
        map.put("existing_landmark", getValue(client.getDetails(), "landmark", true));
        map.put("existing_address1", getValue(client.getDetails(), "adderss1", true));

        return map;
    }
}
