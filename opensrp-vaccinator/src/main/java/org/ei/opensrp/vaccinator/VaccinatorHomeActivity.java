package org.ei.opensrp.vaccinator;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.ei.opensrp.repository.db.Client;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.vaccinator.child.ChildSmartRegisterActivity;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.vaccinator.household.HouseholdSmartRegisterActivity;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeHomeActivity;
import org.ei.opensrp.view.contract.HomeContext;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class VaccinatorHomeActivity extends NativeHomeActivity {
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

        navigationController = new VaccinatorNavigationController(this);//todo refactor and maybe remove this method
        Log.i(TAG, "Created Home Activity views:");
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
        String womanCount;
        String childCount;
        int i = 0;
        if(Utils.userRoles.contains("Vaccinator")){
                //i = context.ceDB().getEvents(null, "Woman TT Enrollment", null).size();
                //i = context.ceDB().getClients().size();
            //context.getInstance().ceDB().rawQuery("select * from event where eventType = 'Woman TT Enrollment'").get(0).size();
            //String db = context.getInstance().ceDB().getDatabaseName();
            /*ContentValues row = new ContentValues();
            Cursor cur = context.getInstance().ceDB().rawQueryForCursor("SELECT * FROM event where eventType like '%Woman%'");
      if (cur.moveToNext()) {
            row.put("title", cur.getString(0));
            row.put("priority", cur.getInt(1));
      }
      cur.close();*/
            List<Client> client = new ArrayList<Client>();
            try {
                client = context.getInstance().ceDB().getClients();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Log.d("value of client", client.toString());
            womanCount = String.valueOf(context.getInstance().ceDB().rawQueryForCursor("SELECT * FROM event where eventType like '%Woman%'").getCount());
            childCount = String.valueOf(context.getInstance().ceDB().rawQueryForCursor("SELECT * FROM event where eventType like '%Child%'").getCount());

            //householdCount = String.valueOf(context.ceDB().rawQueryForCursor("SELECT COUNT(*) c FROM event where eventType like '%CHILD%'").getCount());
        } else {

            womanCount = context.commonrepository("pkwoman").rawQuery("SELECT COUNT(*) c FROM pkwoman").get(0).get("c");
            childCount = context.commonrepository("pkchild").rawQuery("SELECT COUNT(*) c FROM pkchild").get(0).get("c");

        }
        //womanCount = context.commonrepository("pkwoman").rawQuery("SELECT COUNT(*) c FROM pkwoman").get(0).get("c");
        String stockCountD = context.commonrepository("stock").rawQuery("SELECT COUNT(*) c FROM stock WHERE report='daily'").get(0).get("c");
        String stockCountM = context.commonrepository("stock").rawQuery("SELECT COUNT(*) c FROM stock WHERE report='monthly'").get(0).get("c");
        String householdCount = context.commonrepository("pkhousehold").rawQuery("SELECT COUNT(*) c FROM pkhousehold").get(0).get("c");


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
}
