package org.ei.opensrp.db;

import android.content.Context;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.ei.opensrp.db.adapters.AlertRepository;
import org.ei.opensrp.db.adapters.ChildRepository;
import org.ei.opensrp.db.adapters.EligibleCoupleRepository;
import org.ei.opensrp.db.adapters.FormDataRepository;
import org.ei.opensrp.db.adapters.FormsVersionRepository;
import org.ei.opensrp.db.adapters.ImageRepository;
import org.ei.opensrp.db.adapters.MotherRepository;
import org.ei.opensrp.db.adapters.ServiceProvidedRepository;
import org.ei.opensrp.db.adapters.SettingsRepository;
import org.ei.opensrp.db.adapters.TimelineEventRepository;


/**
 * Created by koros on 2/12/16.
 */
public class RepositoryManager {

    private static  final String TAG = "DbConnectionManager";
    private static RepositoryManager repositoryManager;
    private static Context appContext;

    private static final String DATABASE_NAME = "opensrp.db";

    protected static OpenSRPSQLiteOpenHelper openSRPSQLiteOpenHelper;
    protected static SQLiteDatabase db;

    private AlertRepository alertRepository;
    private ChildRepository childRepository;
    private EligibleCoupleRepository eligibleCoupleRepository;
    private FormDataRepository formDataRepository;
    private FormsVersionRepository formsVersionRepository;
    private ImageRepository imageRepository;
    private MotherRepository motherRepository;
    private ServiceProvidedRepository serviceProvidedRepository;
    private SettingsRepository settingsRepository;
    private TimelineEventRepository timelineEventRepository;

    private String password;

    public RepositoryManager(Context applicationContext)
    {
        Log.w(TAG, "initializing Db connection manager...");
        appContext = applicationContext;
        openSRPSQLiteOpenHelper = new OpenSRPSQLiteOpenHelper(applicationContext, DATABASE_NAME, null);
        alertRepository = new AlertRepository(applicationContext, password);
        childRepository = new ChildRepository(applicationContext, password);
        eligibleCoupleRepository = new EligibleCoupleRepository(applicationContext, password);
        formDataRepository = new FormDataRepository();
        formsVersionRepository = new FormsVersionRepository(applicationContext, password);
        imageRepository = new ImageRepository(applicationContext, password);
        motherRepository = new MotherRepository(applicationContext, password);
    }

    public static boolean open(String password)
    {
        if (openSRPSQLiteOpenHelper == null)
        {
            openSRPSQLiteOpenHelper = new OpenSRPSQLiteOpenHelper(appContext, DATABASE_NAME, null);
        }
        try
        {
            if (openWritableDatabase(password) || openReadableDatabase(password))
            {
                return true;
            }
            else
            {
                Log.e(TAG, "Unable to open the database");
                return false;
            }
        }
        catch (SQLiteException ex)
        {
            Log.e(TAG, "Unable to open the database");
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean openWritableDatabase(String password)
    {
        try
        {
            db = openSRPSQLiteOpenHelper.getWritableDatabase(password);
            return true;
        }
        catch (SQLiteException ex)
        {
            Log.e(TAG, "Unable to open a writable database");
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean openReadableDatabase(String password)
    {
        try
        {
            db = openSRPSQLiteOpenHelper.getReadableDatabase(password);
            return true;
        }
        catch (SQLiteException ex)
        {
            Log.e(TAG, "Unable to open a readable database");
            ex.printStackTrace();
            return false;
        }
    }

    protected static void close()
    {
        db.close();
    }

    /**
     * @param context
     */

    public static void initializeRepositoryManager(Context context)
    {
        if (repositoryManager == null)
        {
            repositoryManager = new RepositoryManager(context);
        }
    }

    /**
     * @return
     */
    public static RepositoryManager current()
    {
        return repositoryManager;
    }

    /**
     * A static method that can be used to get a db connection. If none is currently
     * open, a new one is created, otherwise the existing one is returned.
     **/
    public static SQLiteDatabase getDatabase(Context context, String password)
    {
        try
        {
            if (db == null || !db.isOpen())
            {
                appContext = context;
                open(password);
            }
        }
        catch (SQLiteException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return db;
    }

    public AlertRepository alertRepository(){
        return alertRepository;
    }

    public ChildRepository childRepository(){
        return childRepository;
    }

    public EligibleCoupleRepository eligibleCoupleRepository(){
        return eligibleCoupleRepository;
    }

    public FormDataRepository formDataRepository(){
        return formDataRepository();
    }

    public FormsVersionRepository formsVersionRepository(){
        return formsVersionRepository;
    }

    public  ImageRepository imageRepository(){
        return imageRepository;
    }

    public MotherRepository motherRepository(){
        return motherRepository;
    }

}
