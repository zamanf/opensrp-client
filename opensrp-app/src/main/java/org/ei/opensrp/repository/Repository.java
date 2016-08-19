package org.ei.opensrp.repository;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.ei.opensrp.util.Session;

import java.io.File;

public class Repository extends SQLiteOpenHelper {
    private DrishtiRepository[] repositories;
    private File databasePath;
    private Context context;
    private String dbName;
    private Session session;


    public Repository(Context context, Session session, DrishtiRepository... repositories) {
        super(context, session.repositoryName(), null, 1);
        this.repositories = repositories;
        this.context = context;
        this.session = session;
        this.dbName = session != null ? session.repositoryName() : "drishti.db";
        this.databasePath = context != null ? context.getDatabasePath(dbName) : new File("/data/data/org.ei.opensrp.indonesia/databases/drishti.db");

        SQLiteDatabase.loadLibs(context);
        for (DrishtiRepository repository : repositories) {
            repository.updateMasterRepository(this);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (DrishtiRepository repository : repositories) {
            repository.onCreate(database);
        }

        String searchSql = "create virtual table search using fts4 (program_client_id,epi_card_number,first_name,last_name,father_name,mother_name,husband_name,contact_phone_number);";
        String searchRelationsSql = "create table search_relations (search_rowid INTEGER, object_id INTEGER, object_type TEXT);";
        String searchRelationsRowIdIndex = "create index search_relations_searchrowid_index on search_relations (search_rowid)";
        String searchRelationsObjectIdIndex = "create index search_relations_objects_id_index on search_relations (object_id);";
        String searchRelationsObjectLinkIndex = "create index search_relations_objects_link_index on search_relations (object_id,object_type);";

        database.execSQL(searchSql);
        database.execSQL(searchRelationsSql);
        database.execSQL(searchRelationsRowIdIndex);
        database.execSQL(searchRelationsObjectIdIndex);
        database.execSQL(searchRelationsObjectLinkIndex);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
    }

    public SQLiteDatabase getReadableDatabase() {
        if (password() == null) {
            throw new RuntimeException("Password has not been set!");
        }
        return super.getReadableDatabase(password());
    }

    public SQLiteDatabase getWritableDatabase() {
        if (password() == null) {
            throw new RuntimeException("Password has not been set!");
        }
        return super.getWritableDatabase(password());
    }

    public boolean canUseThisPassword(String password) {
        try {
            SQLiteDatabase database = SQLiteDatabase.openDatabase(databasePath.getPath(), password, null, SQLiteDatabase.OPEN_READONLY);
            database.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String password() {
        return session.password();
    }

    public void deleteRepository() {
        close();
        context.deleteDatabase(dbName);
        context.getDatabasePath(dbName).delete();
    }
}
