package org.ei.opensrp.path.service.intent;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.ei.opensrp.Context;
import org.ei.opensrp.sync.CloudantSyncHandler;

import java.util.concurrent.CountDownLatch;


/**
 * Created by onamacuser on 18/03/2016.
 */
public class PathReplicationIntentService extends IntentService {
    private static final String TAG = PathReplicationIntentService.class.getCanonicalName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public PathReplicationIntentService(String name) {
        super(name);
    }

    public PathReplicationIntentService() {

        super("ReplicationIntentService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            CloudantSyncHandler mCloudantSyncHandler = CloudantSyncHandler.getInstance(Context.getInstance().applicationContext());
            CountDownLatch mCountDownLatch = new CountDownLatch(1);
            mCloudantSyncHandler.setCountDownLatch(mCountDownLatch);
            mCloudantSyncHandler.startPushReplication();

            mCountDownLatch.await();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }
}
