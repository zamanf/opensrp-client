package org.ei.opensrp.db;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.ei.opensrp.R;
import org.ei.opensrp.util.Session;

import java.io.File;


/**
 * Created by koros on 2/12/16.
 */
public class OpenSRPSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "OpenSRPSQLiteOpenHelper";

    Context context;
    String name;
    SQLiteDatabase.CursorFactory factory;
    private File databasePath;
    private String dbName;
    private Session session;

    private static final String create_alerts_table = "CREATE TABLE alerts(caseID VARCHAR, scheduleName VARCHAR, visitCode VARCHAR, status VARCHAR, startDate VARCHAR, expiryDate VARCHAR, completionDate VARCHAR)";
    private static final String create_child_table = "CREATE TABLE child(id VARCHAR PRIMARY KEY, motherCaseId VARCHAR, thayiCardNumber VARCHAR, dateOfBirth VARCHAR, gender VARCHAR, details VARCHAR, isClosed VARCHAR, photoPath VARCHAR)";
    private static final String create_eligible_couple_table = "CREATE TABLE eligible_couple(id VARCHAR PRIMARY KEY, wifeName VARCHAR, husbandName VARCHAR, " +
            "ecNumber VARCHAR, village VARCHAR, subCenter VARCHAR, isOutOfArea VARCHAR, details VARCHAR, isClosed VARCHAR, photoPath VARCHAR)";
    private static final String create_form_submission_table = "CREATE TABLE form_submission(instanceId VARCHAR PRIMARY KEY, entityId VARCHAR, " +
            "formName VARCHAR, instance VARCHAR, version VARCHAR, serverVersion VARCHAR, formDataDefinitionVersion VARCHAR, syncStatus VARCHAR)";
    private static final String create_all_forms_version_table = "CREATE TABLE all_forms_version(id INTEGER PRIMARY KEY," +
            "formName VARCHAR, formDirName VARCHAR, formDataDefinitionVersion VARCHAR, syncStatus VARCHAR)";
    private static final String create_image_list_table = "CREATE TABLE ImageList(imageid VARCHAR PRIMARY KEY, anmId VARCHAR, entityID VARCHAR, contenttype VARCHAR, filepath VARCHAR, syncStatus VARCHAR, filecategory VARCHAR)";
    private static final String create_mother_table = "CREATE TABLE mother(id VARCHAR PRIMARY KEY, ecCaseId VARCHAR, thayiCardNumber VARCHAR, type VARCHAR, referenceDate VARCHAR, details VARCHAR, isClosed VARCHAR)";
    private static final String create_report_table = "CREATE TABLE report(indicator VARCHAR PRIMARY KEY, annualTarget VARCHAR, monthlySummaries VARCHAR)";
    private static final String create_service_provided_table = "CREATE TABLE service_provided(id INTEGER PRIMARY KEY AUTOINCREMENT, entityId VARCHAR, name VARCHAR, date VARCHAR, data VARCHAR)";
    private static final String create_settings = "CREATE TABLE settings(key VARCHAR PRIMARY KEY, value BLOB)";
    private static final String create_timeline_event_table = "CREATE TABLE timelineEvent(caseID VARCHAR, type VARCHAR, referenceDate VARCHAR, title VARCHAR, detail1 VARCHAR, detail2 VARCHAR)";
    private static final String MOTHER_TYPE_INDEX_SQL = "CREATE INDEX mother_type_index ON mother(type);";
    private static final String MOTHER_REFERENCE_DATE_INDEX_SQL = "CREATE INDEX mother_referenceDate_index ON mother(referenceDate);";
    private static final String TIMELINEVENT_CASEID_INDEX_SQL = "CREATE INDEX timelineEvent_caseID_index ON timelineEvent(caseID);";
    private static final String REPORT_INDICATOR_INDEX_SQL = "CREATE INDEX report_indicator_index ON report(indicator);";

    public OpenSRPSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, context.getResources().getInteger(R.integer.db_version));
        this.context = context;
        this.name = name;
        this.factory = factory;
        this.databasePath = context.getDatabasePath(session.repositoryName());
        this.dbName = session.repositoryName();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating new database...");
        try
        {
            int databaseVesrsion = context.getResources().getInteger(R.integer.db_version);
            boolean success = false;
            db.beginTransaction();
            for (int v = 1; v <= databaseVesrsion; v++)
            {
                //perform incremental database upgrades upto the current version
                Log.i(TAG,"Upgrading database to version" + v);
                success = executeUpgradeFor(db, v);

                //If an upgrade fails, abort the process.
                if (!success)
                {
                    break;
                }
            }

            if (success)
            {
                db.setVersion(databaseVesrsion);
                Log.w(TAG,"Database has been set to version: " + db.getVersion());
                db.setTransactionSuccessful();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        try
        {
            Log.w(TAG, "Upgrading from version " + oldVersion + " to " + newVersion + ".");
            boolean success = false;

            db.beginTransaction();
            if (newVersion > oldVersion)
            {
                //perform database upgrades
                for (int v = oldVersion + 1; v <= newVersion; v++)
                {
                    Log.i(TAG, "Upgrading database to version" + v);
                    success = executeUpgradeFor(db, v);
                    //If an upgrade fails, abort the process.
                    if (!success)
                    {
                        break;
                    }
                }
            }

            if (success)
            {
                db.setVersion(newVersion);
                Log.w(TAG, "Database has been set to version: " + db.getVersion());
                db.setTransactionSuccessful();
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception occurred : " + e.getMessage());
        }
        finally
        {
            db.endTransaction();
        }
    }

    /**
     *
     * @param db - reference to the database
     * @param version - the version whose upgrade we want to execute
     * @return - true if successful, otherwise false
     */
    boolean executeUpgradeFor(SQLiteDatabase db, int version)
    {
        try
        {
            boolean success = false;

            switch (version)
            {
                case 1:
                    success = createDatabase(db);
                    break;
                /* case 2:
                    success = upgradesForVersion_2(db);
                    break; */
                default:
                    Log.e(TAG, "No upgrades exist for the version number" + version);
                    break;
            }

            return success;

        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception occurred : " + e.getMessage());
            return false;
        }
    }

    /**
     * Creates the database when the user first installs the application
     **/
    boolean createDatabase(SQLiteDatabase db)
    {
        try
        {
            db.execSQL(create_alerts_table);
            db.execSQL(create_child_table);
            db.execSQL(create_eligible_couple_table);
            db.execSQL(create_form_submission_table);
            db.execSQL(create_image_list_table);
            db.execSQL(create_mother_table);
            db.execSQL(create_report_table);
            db.execSQL(create_all_forms_version_table);
            db.execSQL(create_service_provided_table);
            db.execSQL(create_settings);
            db.execSQL(create_timeline_event_table);

            db.execSQL(MOTHER_TYPE_INDEX_SQL);
            db.execSQL(MOTHER_REFERENCE_DATE_INDEX_SQL);
            db.execSQL(TIMELINEVENT_CASEID_INDEX_SQL);
            db.execSQL(REPORT_INDICATOR_INDEX_SQL);

            Log.i(TAG, "Upgraded db to version 1");
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception occurred : " + e.getMessage());
            return false;
        }
    }

    /**
     * Executes statements contained in this block when db version changes to 2
     **/
    boolean upgradesForVersion_2(SQLiteDatabase db)
    {
        //Statements for v2 upgrade
        return true;
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
