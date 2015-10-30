package org.ei.opensrp.mcare.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.mcare.domain.Client;
import org.ei.opensrp.repository.DrishtiRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.sqlcipher.DatabaseUtils.longForQuery;

/**
 * Created by Ahmed on 19-Oct-15.
 */
public class ClientRepository extends DrishtiRepository {
    private static final String CLIENT_SQL = "CREATE TABLE client(id VARCHAR PRIMARY KEY, relationaId VARCHAR, type VARCHAR, details VARCHAR , photopath VARCHAR)";
    public static final String CLIENT_TABLE_NAME = "client";
    private static final String ID_COLUMN = "id";
    private static final String RELATIONALID_COLUMN = "relationalId";
    private static final String TYPE_COLUMN = "type";
    private static final String PHOTOPATH_COLUMN = "photopath";
    private static final String DETAILS_COLUMN = "details";
    private static final String[] CLIENT_TABLE_COLUMNS ={ID_COLUMN,RELATIONALID_COLUMN,TYPE_COLUMN,PHOTOPATH_COLUMN,DETAILS_COLUMN} ;


    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(CLIENT_SQL);
    }

    public void add(Client client) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.insert(CLIENT_TABLE_NAME, null, createValuesFor(client));
    }

    public void update(Client client) {
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        database.update(CLIENT_TABLE_NAME, createValuesFor(client), ID_COLUMN + " = ?", new String[]{client.getId()});
    }

    public long count() {
        return longForQuery(masterRepository.getReadableDatabase(), "SELECT COUNT(1) FROM " + CLIENT_TABLE_NAME, new String[0]);
    }

    public List<Client> all() {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(CLIENT_TABLE_NAME, CLIENT_TABLE_COLUMNS, null, null, null, null, null, null);
        return readAll(cursor);
    }

    public Client find(String caseId) {
        SQLiteDatabase database = masterRepository.getReadableDatabase();
        Cursor cursor = database.query(CLIENT_TABLE_NAME, CLIENT_TABLE_COLUMNS, ID_COLUMN + " = ?", new String[]{caseId}, null, null, null, null);
        List<Client> children = readAll(cursor);

        if (children.isEmpty()) {
            return null;
        }
        return children.get(0);
    }


    private List<Client> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Client> clients = new ArrayList<Client>();
        while (!cursor.isAfterLast()) {
            clients.add(new Client(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                            new Gson().<Map<String, String>>fromJson(cursor.getString(3), new TypeToken<Map<String, String>>() {
                            }.getType()))

                            .withPhotoPath(cursor.getString(cursor.getColumnIndex(PHOTOPATH_COLUMN)))
            );
            cursor.moveToNext();
        }
        cursor.close();
        return clients;
    }

    private ContentValues createValuesFor(Client client) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, client.getId());
        values.put(RELATIONALID_COLUMN, client.getRelationalId());
        values.put(TYPE_COLUMN, client.getType());
        values.put(DETAILS_COLUMN, new Gson().toJson(client.details()));
        values.put(PHOTOPATH_COLUMN, client.getPhotoPath());
        return values;
    }
}
