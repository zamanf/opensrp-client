package org.ei.opensrp.view.activity;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.R;
import org.ei.opensrp.event.Listener;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.sync.SyncAfterFetchListener;
import org.ei.opensrp.sync.SyncProgressIndicator;
import org.ei.opensrp.sync.UpdateActionsTask;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;

import static java.lang.String.valueOf;
import static org.ei.opensrp.event.Event.ACTION_HANDLED;
import static org.ei.opensrp.event.Event.FORM_SUBMITTED;
import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public abstract class NativeHomeActivity extends SecuredActivity {
    private MenuItem updateMenuItem;
    private MenuItem remainingFormsToSyncMenuItem;
    private String locationDialogTAG = "locationDialogTAG";
    private PendingFormSubmissionService pendingFormSubmissionService;
    Activity activity=this;
    private Listener<Boolean> onSyncStartListener;
    private Listener<Boolean> onSyncCompleteListener;
    private Listener<String> onFormSubmittedListener;
    private Listener<String> updateANMDetailsListener;

    protected Listener<Boolean> onSyncStartListener(){
        return new Listener<Boolean>() {
            @Override
            public void onEvent(Boolean data) {
                if (updateMenuItem != null) {
                    updateMenuItem.setActionView(R.layout.progress);
                }
            }
        };
    }

    protected Listener<Boolean> onSyncCompleteListener(){
        return new Listener<Boolean>() {
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
    }

    protected Listener<String> onFormSubmittedListener(){
        return new Listener<String>() {
            @Override
            public void onEvent(String instanceId) {
                updateRegisterCounts();
            }
        };
    }

    protected Listener<String> updateANMDetailsListener(){
        return new Listener<String>() {
            @Override
            public void onEvent(String data) {
                updateRegisterCounts();
            }
        };
    }

    public abstract int smartRegistersHomeLayout();

    @Override
    protected void onCreation() {
        Log.i(getClass().getName(), "Creating Home Activity Views:");

        setContentView(smartRegistersHomeLayout());
        setupViewsAndListeners();
        initialize();
    }

    public abstract void setupViewsAndListeners();

    protected void initialize() {
        pendingFormSubmissionService = context.pendingFormSubmissionService();
        onSyncStartListener = onSyncStartListener();
        onSyncCompleteListener = onSyncCompleteListener();
        onFormSubmittedListener = onFormSubmittedListener();
        updateANMDetailsListener = updateANMDetailsListener();

        SYNC_STARTED.addListener(onSyncStartListener);
        SYNC_COMPLETED.addListener(onSyncCompleteListener);
        FORM_SUBMITTED.addListener(onFormSubmittedListener);
        ACTION_HANDLED.addListener(updateANMDetailsListener);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(getResources().getDrawable(R.drawable.logo_header));
        getSupportActionBar().setLogo(R.drawable.logo_header);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    protected void onResumption() {
        Log.i(getClass().getName(), "Updating Counts");

        updateRegisterCounts();
        updateSyncIndicator();

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

    protected abstract void updateRegisterCounts(HomeContext homeContext);

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
        int i = item.getItemId();
        if (i == R.id.updateMenuItem) {
            updateFromServer();
            return true;
        }
        else if(i == R.id.switchLanguageMenuItem){
            //todo
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void updateFromServer() {
        UpdateActionsTask updateActionsTask = new UpdateActionsTask(
                this, context.actionService(), context.formSubmissionSyncService(),
                new SyncProgressIndicator(), context.allFormVersionSyncService());
        updateActionsTask.updateFromServer(new SyncAfterFetchListener());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SYNC_STARTED.removeListener(onSyncStartListener);
        SYNC_COMPLETED.removeListener(onSyncCompleteListener);
        FORM_SUBMITTED.removeListener(onFormSubmittedListener);
        ACTION_HANDLED.removeListener(updateANMDetailsListener);
    }

    protected void updateSyncIndicator() {
        if (updateMenuItem != null) {
            if (context.allSharedPreferences().fetchIsSyncInProgress()) {
                updateMenuItem.setActionView(R.layout.progress);
            } else
                updateMenuItem.setActionView(null);
        }
    }

    protected void updateRemainingFormsToSyncCount() {
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
}
