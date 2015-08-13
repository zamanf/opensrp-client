package org.ei.opensrp.repository.cloudant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.R;
import org.ei.opensrp.domain.Child;
import org.ei.opensrp.domain.Mother;
import org.ei.opensrp.domain.ServiceProvided;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang3.ArrayUtils.addAll;

/**
 * Created by Geoffrey Koros on 8/11/2015.
 */
public class ServiceProvidedModel extends BaseItemsModel{

    private static final String SERVICE_PROVIDED_SQL = "CREATE TABLE service_provided(id INTEGER PRIMARY KEY AUTOINCREMENT, entityId VARCHAR, name VARCHAR, date VARCHAR, data VARCHAR)";

    public static final String SERVICE_PROVIDED_TABLE_NAME = "service_provided";
    public static final String ENTITY_ID_COLUMN = "entityId";
    public static final String NAME_ID_COLUMN = "name";
    public static final String DATE_ID_COLUMN = "date";
    public static final String DATA_ID_COLUMN = "data";

    public static final String[] SERVICE_PROVIDED_TABLE_COLUMNS = new String[]{ENTITY_ID_COLUMN, NAME_ID_COLUMN, DATE_ID_COLUMN, DATA_ID_COLUMN};

    public ServiceProvidedModel(Context context){
        super(context, SERVICE_PROVIDED_TABLE_NAME);

        //setup the indexManeger
        if(mIndexManager != null){
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(ENTITY_ID_COLUMN, NAME_ID_COLUMN, DATE_ID_COLUMN, DATA_ID_COLUMN), "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            }else{
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }


    public void add(ServiceProvided serviceProvided) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(createValuesFor(serviceProvided));
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            ServiceProvided.fromRevision(created);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public List<ServiceProvided> findByEntityIdAndServiceNames(String entityId, String... names) {
//        SQLiteDatabase database = masterRepository.getReadableDatabase();
//        Cursor cursor = database.rawQuery(
//                format("SELECT * FROM %s WHERE %s = ? AND %s IN (%s) ORDER BY DATE(%s)",
//                        SERVICE_PROVIDED_TABLE_NAME, ENTITY_ID_COLUMN, NAME_ID_COLUMN,
//                        insertPlaceholdersForInClause(names.length), DATE_ID_COLUMN),
//                addAll(new String[]{entityId}, names));
//        return readAllServicesProvided(cursor);
        //TODO :
        return null;
    }

    public List<ServiceProvided> all() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<BasicDocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<ServiceProvided> servicesProvided = new ArrayList<ServiceProvided>();

        // Filter all documents down to those of type Task.
        for(BasicDocumentRevision rev : all) {
            ServiceProvided serviceProvide = ServiceProvided.fromRevision(rev);
            if (serviceProvide != null) {
                servicesProvided.add(serviceProvide);
            }
        }
        return servicesProvided;
    }

    private Map<String,Object> createValuesFor(ServiceProvided serviceProvided) {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put(ENTITY_ID_COLUMN, serviceProvided.entityId());
        values.put(NAME_ID_COLUMN, serviceProvided.name());
        values.put(DATE_ID_COLUMN, serviceProvided.date());
        values.put(DATA_ID_COLUMN, new Gson().toJson(serviceProvided.data()));
        return values;
    }

    private List<ServiceProvided> readAllServicesProvided(Cursor cursor) {
        cursor.moveToFirst();
        List<ServiceProvided> servicesProvided = new ArrayList<ServiceProvided>();
        while (!cursor.isAfterLast()) {
            ServiceProvided serviceProvided = new ServiceProvided(
                    cursor.getString(cursor.getColumnIndex(ENTITY_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(NAME_ID_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(DATE_ID_COLUMN)),
                    new Gson().<Map<String, String>>fromJson(cursor.getString(cursor.getColumnIndex(DATA_ID_COLUMN)), new TypeToken<Map<String, String>>() {
                    }.getType()));
            servicesProvided.add(serviceProvided);
            cursor.moveToNext();
        }
        cursor.close();
        return servicesProvided;
    }

    @Override
    public String getCloudantApiKey() {
        return mContext.getString(R.string.default_api_key);
    }

    @Override
    public String getCloudantDatabaseName() {
        return mContext.getString(R.string.service_provided_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }

}
