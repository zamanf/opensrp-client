package org.ei.opensrp.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.db.RepositoryManager;
import org.ei.opensrp.domain.ServiceProvided;
import org.ei.opensrp.util.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.apache.commons.lang3.ArrayUtils.addAll;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * Created by koros on 2/12/16.
 */
public class ServiceProvidedRepository {

    public static final String SERVICE_PROVIDED_TABLE_NAME = "service_provided";
    public static final String ENTITY_ID_COLUMN = "entityId";
    public static final String NAME_ID_COLUMN = "name";
    public static final String DATE_ID_COLUMN = "date";
    public static final String DATA_ID_COLUMN = "data";
    public static final String[] SERVICE_PROVIDED_TABLE_COLUMNS = new String[]{ENTITY_ID_COLUMN, NAME_ID_COLUMN, DATE_ID_COLUMN, DATA_ID_COLUMN};

    private Context context;
    private Session session;

    public ServiceProvidedRepository(Context context, Session session){
        this.context = context;
        this.session = session;
    }

    public void add(ServiceProvided serviceProvided) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        database.insert(SERVICE_PROVIDED_TABLE_NAME, null, createValuesFor(serviceProvided));
    }

    public List<ServiceProvided> findByEntityIdAndServiceNames(String entityId, String... names) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        Cursor cursor = database.rawQuery(
                format("SELECT * FROM %s WHERE %s = ? AND %s IN (%s) ORDER BY DATE(%s)",
                        SERVICE_PROVIDED_TABLE_NAME, ENTITY_ID_COLUMN, NAME_ID_COLUMN,
                        insertPlaceholdersForInClause(names.length), DATE_ID_COLUMN),
                addAll(new String[]{entityId}, names));
        return readAllServicesProvided(cursor);
    }

    public List<ServiceProvided> all() {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        Cursor cursor = database.query(SERVICE_PROVIDED_TABLE_NAME, SERVICE_PROVIDED_TABLE_COLUMNS, null, null, null, null, DATE_ID_COLUMN);
        return readAllServicesProvided(cursor);
    }

    private ContentValues createValuesFor(ServiceProvided serviceProvided) {
        ContentValues values = new ContentValues();
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

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }
}
