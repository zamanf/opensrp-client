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
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.FormDefinitionVersion;
import org.ei.opensrp.domain.SyncStatus;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.util.EasyMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.ei.drishti.dto.AlertStatus.inProcess;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.domain.SyncStatus.SYNCED;

/**
 * Created by Geoffrey Koros on 8/10/2015.
 */
public class FormsVersionsModel extends BaseItemsModel{

    private static final String FORM_VERSION_SQL = "CREATE TABLE all_forms_version(id INTEGER PRIMARY KEY," +
            "formName VARCHAR, formDirName VARCHAR, formDataDefinitionVersion VARCHAR, syncStatus VARCHAR)";
    private static final String FORM_VERSION_TABLE_NAME = "all_forms_version";

    private static final String ID_COLUMN = "id";
    public static final String FORM_NAME_COLUMN = "formName";
    public static final String VERSION_COLUMN = "formDataDefinitionVersion";
    public static final String FORM_DIR_NAME_COLUMN = "formDirName";
    public static final String SYNC_STATUS_COLUMN = "syncStatus";


    public FormsVersionsModel(Context context) {
        super(context, FORM_VERSION_TABLE_NAME);

        //setup the indexManeger
        if(mIndexManager != null){
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(ID_COLUMN,FORM_NAME_COLUMN, VERSION_COLUMN, FORM_DIR_NAME_COLUMN,
                                SYNC_STATUS_COLUMN),
                        "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            }else{
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }


    public FormDefinitionVersion fetchVersionByFormName(String formName) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(FORM_NAME_COLUMN, formName);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormDefinitionVersion formDefinitionVersion = FormDefinitionVersion.fromRevision(brev);
                    if (formDefinitionVersion != null) {
                        return  formDefinitionVersion;
                    }
                }
            }
        }
        return null;
    }

    public FormDefinitionVersion fetchVersionByFormDirName(String formDirName) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(FORM_DIR_NAME_COLUMN, formDirName);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormDefinitionVersion formDefinitionVersion = FormDefinitionVersion.fromRevision(brev);
                    if (formDefinitionVersion != null) {
                        return  formDefinitionVersion;
                    }
                }
            }
        }
        return null;
    }

    public String getVersion(String formDirName) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(FORM_DIR_NAME_COLUMN, formDirName);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormDefinitionVersion formDefinitionVersion = FormDefinitionVersion.fromRevision(brev);
                    if (formDefinitionVersion != null) {
                        return  formDefinitionVersion.getVersion();
                    }
                }
            }
        }
        return null;
    }

    public FormDefinitionVersion getFormByFormDirName(String formDirName) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(FORM_DIR_NAME_COLUMN, formDirName);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormDefinitionVersion formDefinitionVersion = FormDefinitionVersion.fromRevision(brev);
                    if (formDefinitionVersion != null) {
                        return  formDefinitionVersion;
                    }
                }
            }
        }
        return null;
    }


    public List<FormDefinitionVersion> getAllFormWithSyncStatus(SyncStatus status) {
        List<FormDefinitionVersion> formDefinitionVersions = new ArrayList<FormDefinitionVersion>();
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(SYNC_STATUS_COLUMN, status.value());
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormDefinitionVersion formSubmission = FormDefinitionVersion.fromRevision(brev);
                    if (formSubmission != null) {
                        formDefinitionVersions.add(formSubmission);
                    }
                }
            }
        }
        return formDefinitionVersions;
    }

    public List<Map<String,String>> getAllFormWithSyncStatusAsMap(SyncStatus status) {
        List<FormDefinitionVersion> formDefinitionVersions = new ArrayList<FormDefinitionVersion>();
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(SYNC_STATUS_COLUMN, status.value());
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    FormDefinitionVersion formSubmission = FormDefinitionVersion.fromRevision(brev);
                    if (formSubmission != null) {
                        formDefinitionVersions.add(formSubmission);
                    }
                }
            }
        }
        return readFormVersionToMap(formDefinitionVersions);
    }

    public void addFormVersion(Map<String, String> dataJSON) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(createValuesFormVersions(dataJSON, rev));
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public void addFormVersionFromObject(FormDefinitionVersion formDefinitionVersion) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(createValuesFromObject(formDefinitionVersion, rev));
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public void deleteAll() {
        try {
            int nDocs = this.mDatastore.getDocumentCount();
            List<BasicDocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
            List<FormDefinitionVersion> formDefinitionVersion = new ArrayList<FormDefinitionVersion>();

            for(BasicDocumentRevision rev : all) {
                FormDefinitionVersion f = FormDefinitionVersion.fromRevision(rev);
                if (f != null) {
                    this.mDatastore.deleteDocumentFromRevision(rev);
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public void updateServerVersion(String formDirName, String serverVersion) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(FORM_DIR_NAME_COLUMN, formDirName);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        FormDefinitionVersion formDefinitionVersion = FormDefinitionVersion.fromRevision(brev);
                        if (formDefinitionVersion != null) {
                            formDefinitionVersion.setVersion(serverVersion);
                            updateDocument(formDefinitionVersion);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public void updateFormName(String formDirName, String formName) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(FORM_DIR_NAME_COLUMN, formDirName);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        FormDefinitionVersion formDefinitionVersion = FormDefinitionVersion.fromRevision(brev);
                        if (formDefinitionVersion != null) {
                            formDefinitionVersion.setFormName(formName);
                            updateDocument(formDefinitionVersion);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public void updateSyncStatus(String formDirName, SyncStatus status){
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(FORM_DIR_NAME_COLUMN, formDirName);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        FormDefinitionVersion formDefinitionVersion = FormDefinitionVersion.fromRevision(brev);
                        if (formDefinitionVersion != null) {
                            formDefinitionVersion.setSyncStatus(status);
                            updateDocument(formDefinitionVersion);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public boolean formExists(String formDirName) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(FORM_DIR_NAME_COLUMN, formDirName);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            return  result.size() > 0;
        }
        return false;

    }

    public Map<String, Object> createValuesFormVersions(Map<String, String> params, MutableDocumentRevision revision) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(ID_COLUMN, revision.docId);
        values.put(FORM_NAME_COLUMN, params.get(FORM_NAME_COLUMN));
        values.put(VERSION_COLUMN, params.get(VERSION_COLUMN));
        values.put(FORM_DIR_NAME_COLUMN, params.get(FORM_DIR_NAME_COLUMN));
        values.put(SYNC_STATUS_COLUMN, params.get(SYNC_STATUS_COLUMN));
        return values;
    }

    public Map<String, Object> createValuesFromObject(FormDefinitionVersion formDefinitionVersion, MutableDocumentRevision revision) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(ID_COLUMN, revision.docId);
        values.put(FORM_NAME_COLUMN, formDefinitionVersion.getFormName());
        values.put(VERSION_COLUMN, formDefinitionVersion.getVersion());
        values.put(FORM_DIR_NAME_COLUMN, formDefinitionVersion.getFormDirName());
        values.put(SYNC_STATUS_COLUMN, formDefinitionVersion.getSyncStatus().value());
        return values;
    }

    public long count() {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(SYNC_STATUS_COLUMN, SYNCED.value());
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            return  result.size();
        }
        return 0;
    }

    private List<FormDefinitionVersion> readFormVersion(Cursor cursor) {
        cursor.moveToFirst();
        List<FormDefinitionVersion> submissions = new ArrayList<FormDefinitionVersion>();
        while (!cursor.isAfterLast()) {
            FormDefinitionVersion _formDefinitionVersion = new FormDefinitionVersion(
                    cursor.getString(cursor.getColumnIndex(FORM_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(FORM_DIR_NAME_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(VERSION_COLUMN)))
                    .withFormId(cursor.getString(cursor.getColumnIndex(ID_COLUMN)));
            String _syncStatus = cursor.getString(cursor.getColumnIndex(SYNC_STATUS_COLUMN));
            if(!_syncStatus.isEmpty()) {
                _formDefinitionVersion.withSyncStatus(SyncStatus.valueOf(_syncStatus));
            }
            submissions.add(_formDefinitionVersion);
            cursor.moveToNext();
        }
        cursor.close();
        return submissions;
    }
    private List<Map<String, String>> readFormVersionToMap(List<FormDefinitionVersion>  formDefinitionVersions) {
        List<Map<String, String>> submissions = new ArrayList<Map<String, String>>();
        for (FormDefinitionVersion formDefinitionVersion : formDefinitionVersions) {
            Map<String, String> _formDefinitionVersion = EasyMap.create(FORM_NAME_COLUMN, formDefinitionVersion.getFormName())
                    .put(FORM_DIR_NAME_COLUMN, formDefinitionVersion.getFormDirName())
                    .put(VERSION_COLUMN, formDefinitionVersion.getVersion())
                    .put(ID_COLUMN, formDefinitionVersion.getEntityId())
                    .put(SYNC_STATUS_COLUMN, formDefinitionVersion.getSyncStatus().value())
                    .map();

            submissions.add(_formDefinitionVersion);
        }
        return submissions;
    }

    /**
     * Updates an FormDefinitionVersion document within the datastore.
     * @param formDefinitionVersion FormDefinitionVersion to update
     * @return the updated revision of the FormDefinitionVersion
     * @throws ConflictException if the formDefinitionVersion passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public FormDefinitionVersion updateDocument(FormDefinitionVersion formDefinitionVersion) throws ConflictException {
        MutableDocumentRevision rev = formDefinitionVersion.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(formDefinitionVersion.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return FormDefinitionVersion.fromRevision(updated);
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
        return mContext.getString(R.string.all_forms_version_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }
}
