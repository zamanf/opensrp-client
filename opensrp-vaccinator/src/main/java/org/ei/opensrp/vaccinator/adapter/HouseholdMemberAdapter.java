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
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
import org.ei.opensrp.vaccinator.child.ChildSmartRegisterActivity;
import org.ei.opensrp.vaccinator.household.HouseholdMemberDetails;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
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
    ImageView memberImage;
    Button btnFollowup;
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
        //memberId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        memberName = (TextView) row.findViewById(R.id.memberName);
        memberName.setText(list.get(position).memberName);

        memberAge = (TextView) row.findViewById(R.id.memberDate);
        memberAge.setText(list.get(position).memberAge);

        memberRelation = (TextView) row.findViewById(R.id.relationship);
        memberRelation.setText(list.get(position).memberRelationWithHousehold);

        memberImage =(ImageView) row.findViewById(R.id.individual_profilepic);
        memberImage.setImageResource(list.get(position).memberImageId);

        btnFollowup = (Button) row.findViewById(R.id.followup);
        if(list.get(position).isMemberExists()) {
            btnFollowup.setVisibility(View.VISIBLE);

        } else {
            btnFollowup.setVisibility(View.GONE);
        }

        btnFollowup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonPersonObject person;
                HashMap<String, String> map = new HashMap<>();
                person = list.get(position).getClient();
                map.putAll(followupOverrides(person));

                try {
                    formController = new FormController(WomanSmartRegisterActivity.class.newInstance());
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                client = new CommonPersonObjectClient(person.getCaseId(),person.getDetails(),person.getDetails().get("first_name"));

                client.setColumnmaps(person.getColumnmaps());
                //client.setDetails(person.getDetails());
                client.setCaseId(person.getCaseId());

                final Intent intent;
                if(client.getColumnmaps().containsKey("tt1")){
                    intent = new Intent(fragment.getActivity(), WomanSmartRegisterActivity.class);
                } else {
                    intent = new Intent(fragment.getActivity(), ChildSmartRegisterActivity.class);
                }

                //intent.putExtra("program_client_id", client.getDetails().get("program_client_id").toString());
                intent.putExtra("program_client_id", client.getColumnmaps().get("program_client_id").toString());

                fragment.getActivity().startActivity(intent);
                fragment.getActivity().finish();


                //((HouseholdDetailFragment) fragment.).startFormActivity("woman_followup", ((CommonPersonObjectClient) v.getTag()).entityId(), null);

                //startFollowupForm("woman_followup", client, map, SmartRegisterFragment.ByColumnAndByDetails.byDefault);

                /*Intent intent = new Intent(getContext(), BridgingActivity.class);
                intent.putExtra("woman", "woman");
                BridgingActivity.person  = person;
                /*intent.putExtra("client", client);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(intent);*/

                /*formNames[1] = "woman_enrollment";
                formNames[2] = "woman_followup";
                formNames[3] = "offsite_woman_followup";*/


                //client.setName();

                //startFormActivity("woman_followup", null, null);


                //((WomanSmartRegisterActivity) fragment.getActivity()).startFormActivity("woman_followup", ((CommonPersonObjectClient) v.getTag()).entityId(), null);
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
