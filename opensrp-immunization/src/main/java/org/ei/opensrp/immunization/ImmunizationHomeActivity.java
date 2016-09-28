package org.ei.opensrp.immunization;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.ei.opensrp.immunization.child.ChildSmartRegisterActivity;
import org.ei.opensrp.immunization.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.immunization.household.HouseholdSmartRegisterActivity;
import org.ei.opensrp.immunization.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.view.activity.NativeHomeActivity;
import org.ei.opensrp.view.contract.HomeContext;

public class ImmunizationHomeActivity extends NativeHomeActivity {
    Activity activity=this;

    private TextView womanRegisterClientCountView;
    private TextView childRegisterClientCountView;
    private TextView fieldRegisterClientCountMView;
    private TextView householdRegisterClientCountView;
    private TextView householdRegisterClientMemberCountView;

    private final String TAG = getClass().getName();

    @Override
    public int smartRegistersHomeLayout() {
        return R.layout.smart_registers_home;
    }

    @Override
    protected void onCreation() {
        super.onCreation();

        navigationController = new ImmunizationNavigationController(this);//todo refactor and maybe remove this method
        Log.i(TAG, "Created Home Activity views:");

        /*Countly.sharedInstance().init(this, "https://cloud.count.ly", "dc5dfb412bdbd91792b29f66e5a4bd2ee226cfb6");
        new FlurryAgent.Builder().withLogEnabled(false).build(this, "PKD27SF3CWTVPRFPVDVV");*/
    }

    public void setupViewsAndListeners() {
        ImageButton imgButtonHousehold=(ImageButton)findViewById(R.id.btn_household_register);
        ImageButton imgButtonChild=(ImageButton)findViewById(R.id.btn_child_register_new);
        ImageButton imgButtonWoman=(ImageButton)findViewById(R.id.btn_woman_register);
        ImageButton imgButtonField=(ImageButton)findViewById(R.id.btn_field_register);
        if(onRegisterStartListener!=null) {
            imgButtonField.setOnClickListener(onRegisterStartListener);
            imgButtonWoman.setOnClickListener(onRegisterStartListener);
            imgButtonChild.setOnClickListener(onRegisterStartListener);
            imgButtonHousehold.setOnClickListener(onRegisterStartListener);
        }

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_provider_profile).setOnClickListener(onButtonsClickListener);

        householdRegisterClientCountView = (TextView) findViewById(R.id.txt_household_register_client_count);
        householdRegisterClientMemberCountView = (TextView) findViewById(R.id.txt_household_register_client_plus_members_count);
        womanRegisterClientCountView = (TextView) findViewById(R.id.txt_woman_register_client_count);
        childRegisterClientCountView = (TextView) findViewById(R.id.txt_child_register_client_count);
        fieldRegisterClientCountMView = (TextView) findViewById(R.id.txt_field_register_client_countm);
    }

    protected void updateRegisterCounts(HomeContext homeContext) {
        String householdCount = context.commonrepository("pkhousehold").rawQuery("SELECT COUNT(*) c FROM pkhousehold").get(0).get("c");
        String householdIndividualCount = context.commonrepository("pkindividual").rawQuery("SELECT COUNT(*) c FROM pkindividual").get(0).get("c");

        String childCount = context.commonrepository("pkchild").rawQuery("SELECT COUNT(*) c FROM pkchild").get(0).get("c");
        String womanCount = context.commonrepository("pkwoman").rawQuery("SELECT COUNT(*) c FROM pkwoman").get(0).get("c");
        String stockCountM = context.commonrepository("stock").rawQuery("SELECT COUNT(*) c FROM stock WHERE report='monthly'").get(0).get("c");

        householdRegisterClientCountView.setText(householdCount+" H");
        householdRegisterClientMemberCountView.setText(Utils.addAsInts(true, householdCount,householdIndividualCount)+" M");//HHHead + individual
        womanRegisterClientCountView.setText(womanCount);
        childRegisterClientCountView.setText(childCount);
        fieldRegisterClientCountMView.setText(stockCountM);
    }

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_household_register:
                    activity.startActivity(new Intent(activity, HouseholdSmartRegisterActivity.class));
                    break;
                case R.id.btn_field_register:
                    activity.startActivity(new Intent(activity, FieldMonitorSmartRegisterActivity.class));
                    break;

                case R.id.btn_child_register_new:
                    activity.startActivity(new Intent(activity, ChildSmartRegisterActivity.class));
                    break;

                case R.id.btn_woman_register:
                    activity.startActivity(new Intent(activity, WomanSmartRegisterActivity.class));
                    break;
            }
        }
    };

    private View.OnClickListener onButtonsClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_reporting:
                    navigationController.startReports();
                    break;

                case R.id.btn_provider_profile:
                    activity.startActivity(new Intent(activity, ProviderProfileActivity.class));
                    break;
            }
        }
    };

    /*@Override
    protected void onStart(){
        super.onStart();
        HashMap<String,String> segments = new HashMap<String, String>();
        segments.put("user", providerDetails().get("provider_name"));
        CountlyAnalytics.startAnalytics(this, CalendarContract.Events.LOGIN, segments);
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.onStartSession(this);
    }

    protected void onStop(){
        super.onStop();
        CountlyAnalytics.stopAnalytics();
        FlurryAgent.onEndSession(this);
    }*/
}
