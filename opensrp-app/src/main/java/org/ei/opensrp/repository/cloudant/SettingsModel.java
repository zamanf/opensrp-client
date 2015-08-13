package org.ei.opensrp.repository.cloudant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.cloudant.sync.query.QueryResult;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.R;
import org.ei.opensrp.domain.Mother;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Geoffrey Koros on 8/13/2015.
 */
public class SettingsModel extends BaseItemsModel{

    public static final String SETTINGS_TABLE_NAME = "settings";
    public static final String SETTINGS_KEY_COLUMN = "key";
    public static final String SETTINGS_VALUE_COLUMN = "value";


    //FIXME: we probably dont want to replicate this table
    public SettingsModel(Context context){
        super(context, SETTINGS_TABLE_NAME);

        //setup the indexManeger
        if(mIndexManager != null){
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(SETTINGS_KEY_COLUMN, SETTINGS_VALUE_COLUMN), "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            }else{
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }

    public void updateSetting(String key, String value) {
        updateValueOfExistingKeyWithValue(key, value);
    }

    public void updateBLOB(String key, byte[] value) {
        updateValueOfExistingKeyWithValue(key, value);
    }

    private void updateValueOfExistingKeyWithValue(String key, Object value){
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(SETTINGS_KEY_COLUMN, key);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        SettingsItem settingsItem = SettingsItem.fromRevision(brev);
                        if (settingsItem != null) {
                            settingsItem.setValue(value);
                            updateDocument(settingsItem);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public String querySetting(String key, String defaultValue) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(SETTINGS_KEY_COLUMN, key);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    SettingsItem settting = SettingsItem.fromRevision(brev);
                    if (settting != null) {
                        return (String)settting.getValue();
                    }
                }
            }
        }
        return defaultValue;
    }

    public byte[] queryBLOB(String key) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(SETTINGS_KEY_COLUMN, key);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    SettingsItem settting = SettingsItem.fromRevision(brev);
                    if (settting != null) {
                        return (byte[])settting.getValue();
                    }
                }
            }
        }
        return null;
    }


    /**
     * Updates an Mother document within the datastore.
     * @param settingsItem SettingsItem to update
     * @return the updated revision of the SettingsItem
     * @throws ConflictException if the settingsItem passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public SettingsItem updateDocument(SettingsItem settingsItem) throws ConflictException {
        MutableDocumentRevision rev = settingsItem.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(settingsItem.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return SettingsItem.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }

    //--------------------------------------------------------------------------------

    public static class SettingsItem{

        String key;
        Object value;

        private BasicDocumentRevision rev;
        public BasicDocumentRevision getDocumentRevision() {
            return rev;
        }
        public static SettingsItem fromRevision(BasicDocumentRevision rev) {
            SettingsItem r = new SettingsItem();
            r.rev = rev;
            // this could also be done by a fancy object mapper
            Map<String, Object> map = rev.asMap();
            if(map.containsKey("key")) {
                r.setKey((String) map.get("key"));
                r.setValue(map.get("value"));
                return r;
            }
            return null;
        }

        public Map<String, Object> asMap() {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("key", key);
            map.put("value", value);
            return map;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    @Override
    public String getCloudantApiKey() {
        return mContext.getString(R.string.default_api_key);
    }

    @Override
    public String getCloudantDatabaseName() {
        return mContext.getString(R.string.settings_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }

}
