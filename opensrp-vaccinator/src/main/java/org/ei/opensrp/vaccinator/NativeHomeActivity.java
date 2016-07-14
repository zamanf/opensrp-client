package org.ei.opensrp.vaccinator;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ei.opensrp.Context;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.vaccinator.child.ChildSmartRegisterActivity;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartRegisterActivity;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterActivity;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;
import org.joda.time.DateTime;

import static android.widget.Toast.LENGTH_SHORT;
import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public class NativeHomeActivity extends SecuredActivity {
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private String locationDialogTAG = "locationDialogTAG";
    private PendingFormSubmissionService pendingFormSubmissionService;
    Activity activity=this;

    private Listener<Boolean> onSyncStartListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            if (updateMenuItem != null) {
            //    updateMenuItem.setActionView(R.layout.progress);
            }
        }
    };

    private Listener<Boolean> onSyncCompleteListener = new Listener<Boolean>() {
        @Override
        public void onEvent(Boolean data) {
            //#TODO: RemainingFormsToSyncCount cannot be updated from a back ground thread!!
            updateRemainingFormsToSyncCount();
            if (updateMenuItem != null) {
                updateMenuItem.setActionView(null);
            }
            updateRegisterCounts();
        }
    };

    private Listener<String> onFormSubmittedListener = new Listener<String>() {
        @Override
        public void onEvent(String instanceId) {
            updateRegisterCounts();
        }
    };

    private Listener<String> updateANMDetailsListener = new Listener<String>() {
        @Override
        public void onEvent(String data) {
            updateRegisterCounts();
        }
    };

    private TextView womanRegisterClientCountView;
    private TextView childRegisterClientCountView;
    private TextView fieldRegisterClientCountMView;
    private TextView fieldRegisterClientCountDView;
    private final String TAG = getClass().getName();

    @Override
    protected void onCreation() {
        Log.i(TAG, "Creating Home Activity Views:");
        setContentView(R.layout.smart_registers_home);
        navigationController = new VaccinatorNavigationController(this);

        setupViews();
        initialize();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(locationDialogTAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
      /*  LocationSelectorDialogFragment
                .newInstance(this, new EditDialogOptionModel(), context.anmLocationController().get(), "new_household_registration")
                .show(ft, locationDialogTAG);*/
        Log.i(TAG, "Created Home Activity views:");
    }

    private void setupViews() {
        ImageButton imgButtonChild=(ImageButton)findViewById(R.id.btn_child_register_new);
        ImageButton imgButtonWoman=(ImageButton)findViewById(R.id.btn_woman_register);
        ImageButton imgButtonField=(ImageButton)findViewById(R.id.btn_field_register);
        if(onRegisterStartListener!=null) {
            imgButtonField.setOnClickListener(onRegisterStartListener);
            imgButtonWoman.setOnClickListener(onRegisterStartListener);
            imgButtonChild.setOnClickListener(onRegisterStartListener);
        }

        findViewById(R.id.btn_reporting).setOnClickListener(onButtonsClickListener);
        findViewById(R.id.btn_provider_profile).setOnClickListener(onButtonsClickListener);

        womanRegisterClientCountView = (TextView) findViewById(R.id.txt_woman_register_client_count);
        childRegisterClientCountView = (TextView) findViewById(R.id.txt_child_register_client_count);
        fieldRegisterClientCountDView = (TextView) findViewById(R.id.txt_field_register_client_countd);
        fieldRegisterClientCountMView = (TextView) findViewById(R.id.txt_field_register_client_countm);

    }

    private void initialize() {
        pendingFormSubmissionService = context.pendingFormSubmissionService();
        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(org.ei.opensrp.vaccinator.R.mipmap.logo));
        getSupportActionBar().setLogo(org.ei.opensrp.vaccinator.R.mipmap.logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        LoginActivity.setLanguage();
    }

    @Override
    protected void onResumption() {
        Log.i(getClass().getName(), "Updating Counts");

        updateRegisterCounts();
        updateSyncIndicator();
        // already inplace in onPrepareOptionsMenu
        // updateRemainingFormsToSyncCount();

        Log.i(getClass().getName(), "Updated ALL Counts but updateRemainingFormsToSyncCount ");
    }

    private void updateRegisterCounts() {
        NativeUpdateANMDetailsTask task = new NativeUpdateANMDetailsTask(Context.getInstance().anmController());
        task.fetch(new NativeAfterANMDetailsFetchListener() {
            @Override
            public void afterFetch(HomeContext anmDetails) {
                updateRegisterCounts(anmDetails);
            }
        });
    }

    private void updateRegisterCounts(HomeContext homeContext) {
        String childCount = context.commonrepository("pkchild").rawQuery("SELECT COUNT(*) c FROM pkchild").get(0).get("c");
        String womanCount = context.commonrepository("pkwoman").rawQuery("SELECT COUNT(*) c FROM pkwoman").get(0).get("c");
        String stockCountD = context.commonrepository("stock").rawQuery("SELECT COUNT(*) c FROM stock WHERE report='daily'").get(0).get("c");
        String stockCountM = context.commonrepository("stock").rawQuery("SELECT COUNT(*) c FROM stock WHERE report='monthly'").get(0).get("c");

        womanRegisterClientCountView.setText(womanCount);
        childRegisterClientCountView.setText(childCount);
        fieldRegisterClientCountDView.setText(stockCountD+" D");
        fieldRegisterClientCountMView.setText(stockCountM+" M");
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        Log.i(getClass().getName(), "Updating menu items");

        updateMenuItem = menu.findItem(R.id.updateMenuItem);
        remainingFormsToSyncMenuItem = menu.findItem(R.id.remainingFormsToSyncMenuItem);

        remainingFormsToSyncMenuItem.setTitle("Loading counts ...");
        remainingFormsToSyncMenuItem.setVisible(true);

        updateSyncIndicator();

        updateRemainingFormsToSyncCount();

        Log.i(getClass().getName(), "Updated menu items");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updateMenuItem:
                updateFromServer();
                return true;
            case R.id.switchLanguageMenuItem:
                String newLanguagePreference = LoginActivity.switchLanguagePreference();
                LoginActivity.setLanguage();
                Toast.makeText(this, "Language preference set to " + newLanguagePreference + ". Please restart the application.", LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context.actionService(), context.formSubmissionSyncService(),
                new SyncProgressIndicator(), context.allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }

    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    private void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context.allSharedPreferences().fetchIsSyncInProgress()) {
              updateMenuItem.setActionView(R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }
    }

    private void updateRemainingFormsToSyncCount() {
        // Get a handler that can be used to post to the main thread
        boolean success = new Handler(getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        if (remainingFormsToSyncMenuItem == null) {
                            return;
                        }

                        long size = pendingFormSubmissionService.pendingFormSubmissionCount();
                        if (size > 0) {
                            remainingFormsToSyncMenuItem.setTitle(valueOf(size) + " " + getString(R.string.unsynced_forms_count_message));
                        } else {
                            remainingFormsToSyncMenuItem.setTitle("0 " + getString(R.string.unsynced_forms_count_message));
                        }
                    }
                });

        Log.i(getClass().getName(), "updateRemainingFormsToSyncCount placed to queue "+success);
    }

    private View.OnClickListener onRegisterStartListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
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
