package org.ei.opensrp.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.db.RepositoryManager;
import org.ei.opensrp.domain.ProfileImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koros on 2/12/16.
 */
public class ImageRepository {
    public static final String Image_TABLE_NAME = "ImageList";
    public static final String ID_COLUMN = "imageid";
    public static final String anm_ID_COLUMN = "anmId";
    public static final String entityID_COLUMN = "entityID";
    private static final String contenttype_COLUMN = "contenttype";
    public static final String filepath_COLUMN = "filepath";
    public static final String syncStatus_COLUMN = "syncStatus";
    public static final String filecategory_COLUMN = "filecategory";
    public static final String[] Image_TABLE_COLUMNS = {ID_COLUMN, anm_ID_COLUMN, entityID_COLUMN, contenttype_COLUMN, filepath_COLUMN, syncStatus_COLUMN,filecategory_COLUMN};

    public static final String TYPE_ANC = "ANC";
    public static final String TYPE_PNC = "PNC";
    private static final String NOT_CLOSED = "false";
    public static String TYPE_Unsynced = "Unsynced";
    public static String TYPE_Synced = "Synced";

    private Context context;
    private String password;

    public ImageRepository(Context context, String password){
        this.context = context;
        this.password = password;
    }

    public void add(ProfileImage Image) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, password);
        database.insert(Image_TABLE_NAME, null, createValuesFor(Image, TYPE_ANC));
        database.close();
    }

    public List<ProfileImage> allProfileImages() {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, password);
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, syncStatus_COLUMN + " = ?", new String[]{TYPE_Unsynced}, null, null, null, null);
        return readAll(cursor);
    }

    public ProfileImage findByEntityId(String entityId) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, password);
        Cursor cursor = database.query(Image_TABLE_NAME, Image_TABLE_COLUMNS, entityID_COLUMN + " = ?", new String[]{entityId}, null, null, null, null);
        return readAll(cursor).get(0);
    }



    public void close(String caseId) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, password);
        ContentValues values = new ContentValues();
        values.put(syncStatus_COLUMN,TYPE_Synced);
        database.update(Image_TABLE_NAME, values, ID_COLUMN + " = ?", new String[]{caseId});
    }

    private ContentValues createValuesFor(ProfileImage image, String type) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, image.getImageid());
        values.put(anm_ID_COLUMN, image.getAnmId());
        values.put(contenttype_COLUMN, image.getContenttype());
        values.put(entityID_COLUMN, image.getEntityID());
        values.put(filepath_COLUMN, image.getFilepath());
        values.put(syncStatus_COLUMN, image.getSyncStatus());
        values.put(filecategory_COLUMN, image.getFilecategory());
        return values;
    }

    private List<ProfileImage> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<ProfileImage> ProfileImages = new ArrayList<ProfileImage>();
        while (!cursor.isAfterLast()) {

            ProfileImages.add(new ProfileImage(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6)));

            cursor.moveToNext();
        }
        cursor.close();
        return ProfileImages;
    }
}
