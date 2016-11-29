package org.ei.opensrp.immunization;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.internal.widget.ThemeUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.domain.LoginResponse;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.UserService;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.util.VaccinatorUtils;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static org.ei.opensrp.domain.LoginResponse.SUCCESS;
import static org.ei.opensrp.util.Utils.convertDateFormat;
import static org.ei.opensrp.util.Utils.getDataRow;
import static org.ei.opensrp.util.Utils.getValue;
import static org.ei.opensrp.util.Utils.*;

public class ProviderProfileActivity extends Activity {
    Context ctx = Context.getInstance();
    UserService usr = ctx.userService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_profile);

        JSONObject providerdt = null;
        try {
            providerdt = VaccinatorUtils.providerFullDetails();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ((TextView)findViewById(R.id.detail_heading)).setText("Provider Details");

        final ProviderProfileActivity ac = this;
        
        findViewById(R.id.refreshProvider).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.v(getClass().getName(), "Checking user info remotely");

                    tryRemoteUserDataSync(new Listener<LoginResponse>() {
                        @Override
                        public void onEvent(LoginResponse r) {
                            Log.v(getClass().getName(), r.toString() + r.payload());

                            if (r == SUCCESS) {
                                String loc = usr.getUserLocation(r.payload());
                                Log.v(getClass().getName(), "LOCATION: " + loc);
                                if (StringUtils.isNotBlank(loc)) usr.saveAnmLocation(loc);

                                String user = usr.getUserData(r.payload());
                                Log.v(getClass().getName(), "USER:" + user);
                                if (StringUtils.isNotBlank(user)) usr.saveUserInfo(user);

                                try {
                                    Utils.writePreference(ac, "team", new JSONObject(r.payload()).getJSONObject("team").toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText(ac, "SUCCESS - user data refreshed ", Toast.LENGTH_LONG).show();

                                try {
                                    fillTable(VaccinatorUtils.providerFullDetails());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Toast.makeText(ac, "ERROR while fetching provider information remotely; " + (r.payload()==null?"Check your internet connection":r.payload()), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        ((TextView)findViewById(R.id.details_id_label)).setText(getValue(providerdt, "provider_id", false, true));

        ((TextView)findViewById(R.id.detail_today)).setText(convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        fillTable(providerdt);

        initializeProgressDialog();
    }

    private void fillTable(JSONObject providerdt){
        Log.v(getClass().getName(), "PROVIDER DETAILS MAP:"+providerdt);

        //BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(R.id.report_detail_info_table1);
        dt.removeAllViews();

        addRow(this, dt, "ID", getValue(providerdt, "provider_id", false, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Name", getValue(providerdt, "provider_name", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Team Identifier", getValue(providerdt, "provider_identifier", false, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Team", getValue(providerdt, "provider_team", true, true), Utils.Size.MEDIUM);

        addRow(this, dt, "Province", getValue(providerdt, "provider_province", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "City", getValue(providerdt, "provider_city", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Town", getValue(providerdt, "provider_town", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "UC", getValue(providerdt, "provider_uc", true, true), Utils.Size.MEDIUM);
        addRow(this, dt, "Center", getValue(providerdt, "provider_location_id", true, true), Utils.Size.MEDIUM);
        try {
            addRow(this, dt, "Roles", providerdt.getJSONObject("user").getJSONArray("roles").toString().replace("\"", "").replace(",", ", "), Utils.Size.MEDIUM);
            addRow(this, dt, "Permissions", providerdt.getJSONObject("user").getJSONArray("permissions").toString().replace("\"", "").replace(",", ", "), Utils.Size.MEDIUM);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private ProgressDialog progressDialog;

    private void initializeProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Fetching provider details");
        progressDialog.setMessage("Connecting to server ... "+getString(org.ei.opensrp.R.string.loggin_in_dialog_message));
    }

    private void tryRemoteUserDataSync(final Listener<LoginResponse> afterLoginCheck) {
        LockingBackgroundTask task = new LockingBackgroundTask(new ProgressIndicator() {
            @Override
            public void setVisible() {
                progressDialog.show();
            }

            @Override
            public void setInvisible() {
                progressDialog.dismiss();
            }
        });

        task.doActionInBackground(new BackgroundAction<LoginResponse>() {
            public LoginResponse actionToDoInBackgroundThread() {
                Log.v(getClass().getName(), ctx.allSharedPreferences().fetchRegisteredANM() +"-CREDS-"+ ctx.allSettings().fetchANMPassword());
                return ctx.userService().isValidRemoteLogin(ctx.allSharedPreferences().fetchRegisteredANM(), ctx.allSettings().fetchANMPassword());
            }

            public void postExecuteInUIThread(LoginResponse result) {
                afterLoginCheck.onEvent(result);
            }
        });
    }

    @Override
    public void onBackPressed() {
        ProgressDialog.show(this, "Wait", "Going back to home...", true);

        super.onBackPressed();
    }
}
