package org.ei.opensrp.repository.cloudant;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.cloudant.sync.datastore.Datastore;
import com.cloudant.sync.datastore.DatastoreManager;
import com.cloudant.sync.datastore.DatastoreNotCreatedException;
import com.cloudant.sync.notifications.ReplicationCompleted;
import com.cloudant.sync.notifications.ReplicationErrored;
import com.cloudant.sync.query.IndexManager;
import com.cloudant.sync.replication.PullReplication;
import com.cloudant.sync.replication.PushReplication;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorFactory;
import com.google.common.eventbus.Subscribe;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Geoffrey Koros on 8/6/2015.
 */
public class BaseItemsModel {

    static final String LOG_TAG = "BaseItemsModel";
    static final String SETTINGS_CLOUDANT_USER = "pref_key_username";
    static final String SETTINGS_CLOUDANT_DB = "pref_key_dbname";
    static final String SETTINGS_CLOUDANT_API_KEY = "pref_key_api_key";
    static final String SETTINGS_CLOUDANT_API_SECRET = "pref_key_api_password";

    static final String DATASTORE_MANGER_DIR = "data";
    protected  String tableName;

    Datastore mDatastore;
    IndexManager mIndexManager;

    Replicator mPushReplicator;
    Replicator mPullReplicator;

    final Context mContext;
    final Handler mHandler;


    public BaseItemsModel(Context context){
        this.mContext = context;
        // Set up our tasks datastore within its own folder in the applications
        // data directory.
        File path = this.mContext.getApplicationContext().getDir(
                DATASTORE_MANGER_DIR,
                Context.MODE_PRIVATE
        );
        DatastoreManager datastoreManager = new DatastoreManager(path.getAbsolutePath());
        try {
            this.mDatastore = datastoreManager.openDatastore(getTableName());
        } catch (DatastoreNotCreatedException dnce) {
            Log.e(LOG_TAG, "Unable to open Datastore", dnce);
        }
        mIndexManager = new IndexManager(mDatastore);

        Log.d(LOG_TAG, "Set up database at " + path.getAbsolutePath());

        // Set up the replicator objects from the app's settings.
        try {
            this.reloadReplicationSettings();
        } catch (URISyntaxException e) {
            Log.e(LOG_TAG, "Unable to construct remote URI from configuration", e);
        }

        // Allow us to switch code called by the ReplicationListener into
        // the main thread so the UI can update safely.
        this.mHandler = new Handler(Looper.getMainLooper());

        Log.d(LOG_TAG, getClass().getName() + " set up " + path.getAbsolutePath());
    }


    //
    // MANAGE REPLICATIONS
    //

    /**
     * <p>Stops running replications.</p>
     *
     * <p>The stop() methods stops the replications asynchronously, see the
     * replicator docs for more information.</p>
     */
    public void stopAllReplications() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.stop();
        }
        if (this.mPushReplicator != null) {
            this.mPushReplicator.stop();
        }
    }

    /**
     * <p>Starts the configured push replication.</p>
     */
    public void startPushReplication() {
        if (this.mPushReplicator != null) {
            this.mPushReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    /**
     * <p>Starts the configured pull replication.</p>
     */
    public void startPullReplication() {
        if (this.mPullReplicator != null) {
            this.mPullReplicator.start();
        } else {
            throw new RuntimeException("Push replication not set up correctly");
        }
    }

    /**
     * <p>Stops running replications and reloads the replication settings from
     * the app's preferences.</p>
     */
    public void reloadReplicationSettings() throws URISyntaxException {

        // Stop running replications before reloading the replication
        // settings.
        // The stop() method instructs the replicator to stop ongoing
        // processes, and to stop making changes to the datastore. Therefore,
        // we don't clear the listeners because their complete() methods
        // still need to be called once the replications have stopped
        // for the UI to be updated correctly with any changes made before
        // the replication was stopped.
        this.stopAllReplications();

        // Set up the new replicator objects
        URI uri = this.createServerURI();

        PushReplication push = new PushReplication();
        push.source = mDatastore;
        push.target = uri;

        mPushReplicator = ReplicatorFactory.oneway(push);
        mPushReplicator.getEventBus().register(this);

        PullReplication pull = new PullReplication();
        pull.source = uri;
        pull.target = mDatastore;

        mPullReplicator = ReplicatorFactory.oneway(pull);
        mPullReplicator.getEventBus().register(this);

        Log.d(LOG_TAG, "Set up replicators for URI:" + uri.toString());
    }

    /**
     * <p>Returns the URI for the remote database, based on the app's
     * configuration.</p>
     * @return the remote database's URI
     * @throws URISyntaxException if the settings give an invalid URI
     */
    private URI createServerURI() throws URISyntaxException {
        // We store this in plain text for the purposes of simple demonstration,
        // you might want to use something more secure.
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.mContext);
        String username = sharedPref.getString(SETTINGS_CLOUDANT_USER, "");
        String dbName = sharedPref.getString(SETTINGS_CLOUDANT_DB, "");
        String apiKey = sharedPref.getString(SETTINGS_CLOUDANT_API_KEY, "");
        String apiSecret = sharedPref.getString(SETTINGS_CLOUDANT_API_SECRET, "");
        String host = username + ".cloudant.com";

        // We recommend always using HTTPS to talk to Cloudant.
        return new URI("https", apiKey + ":" + apiSecret, host, 443, "/" + dbName, null, null);
    }

    //
    // REPLICATIONLISTENER IMPLEMENTATION
    //

    /**
     * Calls the TodoActivity's replicationComplete method on the main thread,
     * as the complete() callback will probably come from a replicator worker
     * thread.
     */
    @Subscribe
    public void complete(ReplicationCompleted rc) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                if (mListener != null) {
//                    mListener.replicationComplete();
//                }
            }
        });
    }

    /**
     * Calls the TodoActivity's replicationComplete method on the main thread,
     * as the error() callback will probably come from a replicator worker
     * thread.
     */
    @Subscribe
    public void error(ReplicationErrored re) {
        Log.e(LOG_TAG, "Replication error:", re.errorInfo.getException());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
//                if (mListener != null) {
//                    mListener.replicationError();
//                }
            }
        });
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
