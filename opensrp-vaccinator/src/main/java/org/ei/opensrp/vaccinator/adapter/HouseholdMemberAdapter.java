package org.ei.opensrp.vaccinator.adapter;

import android.content.Context;
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
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.vaccinator.household.HouseholdDetailActivity;
import org.ei.opensrp.vaccinator.household.HouseholdMemberDetails;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Utils;

import static util.Utils.getValue;

/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdMemberAdapter extends ArrayAdapter<HouseholdMemberDetails> {

    private final Context context;
    private final List<HouseholdMemberDetails> list;
    private FormController formController;

    TextView memberId;
    TextView memberName;
    TextView memberAge;
    TextView memberRelation;
    ImageView memberImage;
    Button btnFollowup;
    CommonPersonObjectClient client;



    public HouseholdMemberAdapter(Context context, List<HouseholdMemberDetails> list){
        super(context, R.layout.list_individual, list);
        this.context = context;
        this.list = list;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

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

                //startFollowupForm("new_member_registration_without_qr", client, map, SmartRegisterFragment.ByColumnAndByDetails.byDefault);

                //((WomanSmartRegisterActivity) getActivity()).startFormActivity("woman_followup", ((CommonPersonObjectClient) v.getTag()).entityId(), null);
            }
        });
        return row;
    }

    /*protected void startFollowupForm(String formName, SmartRegisterClient client, HashMap<String, String> overrideStringmap, SmartRegisterFragment.ByColumnAndByDetails byColumnAndByDetails) {
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
