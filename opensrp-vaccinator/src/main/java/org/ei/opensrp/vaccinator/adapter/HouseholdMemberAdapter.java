package org.ei.opensrp.vaccinator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.household.HouseholdMemberDetails;

import java.util.List;

/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdMemberAdapter extends ArrayAdapter<HouseholdMemberDetails> {

    private final Context context;
    private final List<HouseholdMemberDetails> list;

    TextView memberId;
    TextView memberName;
    TextView memberAge;
    TextView memberRelation;
    ImageView memberImage;


    public HouseholdMemberAdapter(Context context, List<HouseholdMemberDetails> list){
        super(context, R.layout.list_individual, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.list_individual, parent, false);


        memberId = (TextView) row.findViewById(R.id.memberId);
        memberId.setText(list.get(position).memberId);
        //memberId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        memberName = (TextView) row.findViewById(R.id.memberName);
        memberName.setText(list.get(position).memberName);
        //memberId.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

        memberAge = (TextView) row.findViewById(R.id.memberDate);
        memberAge.setText(list.get(position).memberAge);

        memberRelation = (TextView) row.findViewById(R.id.relationship);
        memberRelation.setText(list.get(position).memberRelationWithHousehold);

        memberImage =(ImageView) row.findViewById(R.id.individual_profilepic);
        memberImage.setImageResource(list.get(position).memberImageId);

        return row;
    }
}
