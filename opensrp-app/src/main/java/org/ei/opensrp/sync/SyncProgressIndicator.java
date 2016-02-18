package org.ei.opensrp.sync;

import org.ei.opensrp.db.adapters.SharedPreferencesAdapter;
import org.ei.opensrp.view.ProgressIndicator;

import static org.ei.opensrp.event.Event.SYNC_COMPLETED;
import static org.ei.opensrp.event.Event.SYNC_STARTED;

public class SyncProgressIndicator implements ProgressIndicator {

    private SharedPreferencesAdapter sharedPreferencesAdapter;

    public SyncProgressIndicator(SharedPreferencesAdapter sharedPreferencesAdapter){
        this.sharedPreferencesAdapter = sharedPreferencesAdapter;
    }

    @Override
    public void setVisible() {
        sharedPreferencesAdapter.saveIsSyncInProgress(true);
        SYNC_STARTED.notifyListeners(true);
    }

    @Override
    public void setInvisible() {
        sharedPreferencesAdapter.saveIsSyncInProgress(false);
        SYNC_COMPLETED.notifyListeners(true);
    }
}
