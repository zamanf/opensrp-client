package org.ei.opensrp.household;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import static org.ei.opensrp.immunization.util.VaccinatorUtils.*;

import org.ei.opensrp.household.analytics.CountlyAnalytics;
import org.ei.opensrp.household.analytics.Events;
import org.ei.opensrp.household.child.ChildSmartRegisterActivity;
import org.ei.opensrp.household.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.household.household.HouseholdSmartRegisterActivity;
import org.ei.opensrp.household.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeHomeActivity;
import org.ei.opensrp.view.contract.HomeContext;

import java.util.HashMap;

import ly.count.android.sdk.Countly;

public class HouseholdHomeActivity extends NativeHomeActivity {
    Activity activity=this;

    private TextView womanRegisterClientCountView;
    private TextView childRegisterClientCountView;
    private TextView fieldRegisterClientCountMView;
    private TextView fieldRegisterClientCountDView;
    private TextView householdRegisterClientCountView;
    private final String TAG = getClass().getName();

    @Override
    public int smartRegistersHomeLayout() {
        return R.layout.smart_registers_home;
    }

    @Override
    protected void onCreation() {
        super.onCreation();

        navigationController = new HouseholdNavigationController(this);//todo refactor and maybe remove this method
        Log.i(TAG, "Created Home Activity views:");
        Countly.sharedInstance().init(this, "https://cloud.count.ly", "dc5dfb412bdbd91792b29f66e5a4bd2ee226cfb6");
        new FlurryAgent.Builder().withLogEnabled(false).build(this, "PKD27SF3CWTVPRFPVDVV");

    }

    public void setupViewsAndListeners() {
        ImageButton imgButtonChild=(ImageButton)findViewById(R.id.btn_child_register_new);
        ImageButton imgButtonWoman=(ImageButton)findViewById(R.id.btn_woman_register);
        ImageButton imgButtonField=(ImageButton)findViewById(R.id.btn_field_register);
        ImageButton imgButtonHousehold=(ImageButton)findViewById(R.id.btn_household_register);
        if(onRegisterStartListener!=null) {
            imgButtonField.setOnClickListener(onRegisterStartListener);
            imgButtonWoman.setOnClickListener(onRegisterStartListener);
            imgButtonChild.setOnClickListener(onRegisterStartListener);
            imgButtonHousehold.setOnClickListener(onRegisterStartListener);
        }

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_provider_profile).setOnClickListener(onButtonsClickListener);

        womanRegisterClientCountView = (TextView) findViewById(R.id.txt_woman_register_client_count);
        childRegisterClientCountView = (TextView) findViewById(R.id.txt_child_register_client_count);
        fieldRegisterClientCountDView = (TextView) findViewById(R.id.txt_field_register_client_countd);
        fieldRegisterClientCountMView = (TextView) findViewById(R.id.txt_field_register_client_countm);
        householdRegisterClientCountView = (TextView) findViewById(R.id.txt_household_register_client_count);

    }

    protected void updateRegisterCounts(HomeContext homeContext) {
        String householdCount = context.commonrepository("pkhousehold").rawQuery("SELECT COUNT(*) c FROM pkhousehold").get(0).get("c");
        String childCount = context.commonrepository("pkchild").rawQuery("SELECT COUNT(*) c FROM pkchild").get(0).get("c");
        String womanCount = context.commonrepository("pkwoman").rawQuery("SELECT COUNT(*) c FROM pkwoman").get(0).get("c");
        String stockCountD = context.commonrepository("stock").rawQuery("SELECT COUNT(*) c FROM stock WHERE report='daily'").get(0).get("c");
        String stockCountM = context.commonrepository("stock").rawQuery("SELECT COUNT(*) c FROM stock WHERE report='monthly'").get(0).get("c");

        householdRegisterClientCountView.setText(householdCount);
        womanRegisterClientCountView.setText(womanCount);
        childRegisterClientCountView.setText(childCount);
        fieldRegisterClientCountDView.setText(stockCountD+" D");
        fieldRegisterClientCountMView.setText(stockCountM+" M");
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

    @Override
    protected void onStart(){
        super.onStart();
        HashMap<String,String> segments = new HashMap<String, String>();
        segments.put("user", providerDetails().get("provider_name"));
        CountlyAnalytics.startAnalytics(this, Events.LOGIN, segments);
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.onStartSession(this);
    }

    protected void onStop(){
        super.onStop();
        CountlyAnalytics.stopAnalytics();
        FlurryAgent.onEndSession(this);
    }
}
