package org.ei.opensrp.repository.cloudant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.cloudant.sync.query.QueryResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.R;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Child;
import org.ei.opensrp.domain.SyncStatus;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.repository.ChildRepository;
import org.ei.opensrp.repository.EligibleCoupleRepository;
import org.ei.opensrp.repository.MotherRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.ei.drishti.dto.AlertStatus.complete;
import static org.ei.opensrp.AllConstants.ENTITY_ID_FIELD_NAME;
import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.domain.SyncStatus.SYNCED;

/**
 * Created by Geoffrey Koros on 8/7/2015.
 */
public class FormDataModel extends BaseItemsModel{

    private static final String FORM_SUBMISSION_TABLE_NAME = "form_submission";

    public static final String INSTANCE_ID_COLUMN = "instanceId";
    public static final String ENTITY_ID_COLUMN = "entityId";
    private static final String FORM_NAME_COLUMN = "formName";
    private static final String INSTANCE_COLUMN = "instance";
    private static final String VERSION_COLUMN = "version";
    private static final String SERVER_VERSION_COLUMN = "serverVersion";
    private static final String SYNC_STATUS_COLUMN = "syncStatus";
    private static final String FORM_DATA_DEFINITION_VERSION_COLUMN = "formDataDefinitionVersion";

    public static final String ID_COLUMN = "id";
    private static final String DETAILS_COLUMN_NAME = "details";
    private static final String FORM_NAME_PARAM = "formName";


    public  FormDataModel(Context context){
        super(context, FORM_SUBMISSION_TABLE_NAME);

        //setup the indexManeger
        if(mIndexManager != null) {
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(ID_COLUMN, INSTANCE_ID_COLUMN, ENTITY_ID_COLUMN,
                                FORM_NAME_COLUMN, INSTANCE_COLUMN, VERSION_COLUMN, SERVER_VERSION_COLUMN, SYNC_STATUS_COLUMN),
                        "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            } else {
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }

    @JavascriptInterface
    public String queryUniqueResult(String sql) {
        //TODO:
        return  null;
    }

    @JavascriptInterface
    public String queryList(String sql) {
        //TODO:
        return null;
    }

    @JavascriptInterface
    public String saveFormSubmission(String paramsJSON, String data, String formDataDefinitionVersion) {
        Map<String, String> params = new Gson().fromJson(paramsJSON, new TypeToken<Map<String, String>>() {
        }.getType());

        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(createValuesForFormSubmission(params, data, formDataDefinitionVersion));
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            FormSubmission.fromRevision(created);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }

        return params.get(INSTANCE_ID_PARAM);
    }

    @JavascriptInterface
    public void saveFormSubmission(FormSubmission formSubmission) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(createValuesForFormSubmission(formSubmission));
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            FormSubmission.fromRevision(created);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public FormSubmission fetchFromSubmission(String instanceId) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(INSTANCE_ID_COLUMN, instanceId);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormSubmission formSubmission = FormSubmission.fromRevision(brev);
                    if (formSubmission != null) {
                        return formSubmission;
                    }
                }
            }
        }
        return null;
    }

    public List<FormSubmission> getPendingFormSubmissions() {
        List<FormSubmission> formSubmissions = new ArrayList<FormSubmission>();
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(SYNC_STATUS_COLUMN, PENDING.value());
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormSubmission formSubmission = FormSubmission.fromRevision(brev);
                    if (formSubmission != null) {
                        formSubmissions.add(formSubmission);
                    }
                }
            }
        }
        return formSubmissions;
    }

    public long getPendingFormSubmissionsCount() {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(SYNC_STATUS_COLUMN, PENDING.value());
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            return  result.size();
        }
        return 0;
    }

    public void markFormSubmissionsAsSynced(List<FormSubmission> formSubmissions) {
        try {
            for(FormSubmission formSubmission : formSubmissions){
                formSubmission.setSyncStatus(SYNCED);
                formSubmission.setFormDataDefinitionVersion("1");
                updateDocument(formSubmission);
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public void updateServerVersion(String instanceId, String serverVersion) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(INSTANCE_ID_COLUMN, instanceId);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        FormSubmission formSubmission = FormSubmission.fromRevision(brev);
                        if (formSubmission != null) {
                            formSubmission.setServerVersion(serverVersion);
                            updateDocument(formSubmission);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public boolean submissionExists(String instanceId) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(INSTANCE_ID_COLUMN, instanceId);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            return  result.size() > 0;
        }
        return false;
    }

    @JavascriptInterface
    public String saveEntity(String entityType, String fields) {
        Map<String, String> updatedFieldsMap = new Gson().fromJson(fields, new TypeToken<Map<String, String>>() {
        }.getType());

        String entityId = updatedFieldsMap.get(ENTITY_ID_FIELD_NAME);
//        Map<String, String> entityMap = loadEntityMap(entityType, database, entityId);
//
//        ContentValues contentValues = getContentValues(updatedFieldsMap, entityType, entityMap);
//        database.replace(entityType, null, contentValues);
        return entityId;
    }

    private Map<String,Object> createValuesForFormSubmission(FormSubmission submission) {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put(INSTANCE_ID_COLUMN, submission.instanceId());
        values.put(ENTITY_ID_COLUMN, submission.entityId());
        values.put(FORM_NAME_COLUMN, submission.formName());
        values.put(INSTANCE_COLUMN, submission.instance());
        values.put(VERSION_COLUMN, submission.version());
        values.put(SERVER_VERSION_COLUMN, submission.serverVersion());
        values.put(FORM_DATA_DEFINITION_VERSION_COLUMN, submission.formDataDefinitionVersion());
        values.put(SYNC_STATUS_COLUMN, submission.syncStatus().value());
        return values;
    }

    private Map<String,Object> createValuesForFormSubmission(Map<String, String> params, String data, String formDataDefinitionVersion) {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put(INSTANCE_ID_COLUMN, params.get(INSTANCE_ID_PARAM));
        values.put(ENTITY_ID_COLUMN, params.get(ENTITY_ID_PARAM));
        values.put(FORM_NAME_COLUMN, params.get(FORM_NAME_PARAM));
        values.put(INSTANCE_COLUMN, data);
        values.put(VERSION_COLUMN, currentTimeMillis());
        values.put(FORM_DATA_DEFINITION_VERSION_COLUMN, formDataDefinitionVersion);
        String syncStatus = PENDING.value();
        if (params.containsKey(SYNC_STATUS)) {
            syncStatus = params.get(SYNC_STATUS);
        }
        values.put(SYNC_STATUS_COLUMN, syncStatus);
        return values;
    }

    private List<FormSubmission> readFormSubmission(Cursor cursor) {
        cursor.moveToFirst();
        List<FormSubmission> submissions = new ArrayList<FormSubmission>();
        while (!cursor.isAfterLast()) {
            submissions.add(new FormSubmission(
                    cursor.getString(cursor.getColumnIndex(INSTANCE_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(ENTITY_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(FORM_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(INSTANCE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(VERSION_COLUMN)),
                    SyncStatus.valueOf(cursor.getString(cursor.getColumnIndex(SYNC_STATUS_COLUMN))),
                    cursor.getString(cursor.getColumnIndex(FORM_DATA_DEFINITION_VERSION_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(SERVER_VERSION_COLUMN))
            ));
            cursor.moveToNext();
        }
        cursor.close();
        return submissions;
    }

    private Map<String, String> readARow(Cursor cursor) {
        Map<String, String> columnValues = new HashMap<String, String>();
        if (cursor.isAfterLast())
            return columnValues;
        String[] columns = cursor.getColumnNames();
        int numberOfColumns = columns.length;
        for (int index = 0; index < numberOfColumns; index++) {
            if (DETAILS_COLUMN_NAME.equalsIgnoreCase(columns[index])) {
                Map<String, String> details = new Gson().fromJson(cursor.getString(index), new TypeToken<Map<String, String>>() {
                }.getType());
                columnValues.putAll(details);
            } else {
                columnValues.put(columns[index], cursor.getString(index));
            }
        }
        return columnValues;
    }



    private Map<String, String> initializeDetailsBasedOnExistingValues(Map<String, String> entityMap) {
        Map<String, String> details;
        String detailsJSON = entityMap.get(DETAILS_COLUMN_NAME);
        if (detailsJSON == null) {
            details = new HashMap<String, String>();
        } else {
            details = new Gson().fromJson(detailsJSON, new TypeToken<Map<String, String>>() {
            }.getType());
        }
        return details;
    }

    private ContentValues initializeContentValuesBasedExistingValues(Map<String, String> entityMap) {
        ContentValues contentValues = new ContentValues();
        for (String column : entityMap.keySet()) {
            contentValues.put(column, entityMap.get(column));
        }
        return contentValues;
    }

    @JavascriptInterface
    public String generateIdFor(String entityType) {
        return randomUUID().toString();
    }

    /**
     * Updates an FormSubmission document within the datastore.
     * @param formSubmission FormSubmission to update
     * @return the updated revision of the FormSubmission
     * @throws ConflictException if the formSubmission passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public FormSubmission updateDocument(FormSubmission formSubmission) throws ConflictException {
        MutableDocumentRevision rev = formSubmission.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(formSubmission.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return FormSubmission.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }

    @Override
    public String getCloudantApiKey() {
        return mContext.getString(R.string.default_api_key);
    }

    @Override
    public String getCloudantDatabaseName() {
        return mContext.getString(R.string.form_submission_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }
}
