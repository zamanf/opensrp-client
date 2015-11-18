package org.ei.opensrp.vaccinator.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.repository.DrishtiRepository;

import java.util.HashMap;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 18-Nov-15.
 */
public class ReportTypeRepository extends DrishtiRepository {
    private static final String ID_COLUMN="_id";
    private static final String TYPE_COLUMN="type";
    private static final String TABLE_NAME="report";

    private static String REPORT_TABLE="create table report (_id int primary key , type varchar )";

    @Override
    protected void onCreate(SQLiteDatabase database) {
    database.execSQL(REPORT_TABLE);
    }

    public long add(String name){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(TYPE_COLUMN, name);
        return database.insert(TABLE_NAME,null ,contentValues);


    }

    public HashMap<Integer,String> getAllReportTypes(){
        HashMap<Integer,String> map=new HashMap<Integer,String>();
        SQLiteDatabase database = masterRepository.getWritableDatabase();

        // database.
        Cursor cursor=database.rawQuery("Select * from report;" , null);
        for (int i=0; i<cursor.getCount();i++){

            ;

            int id= cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
            String name= cursor.getString(cursor.getColumnIndex(TABLE_NAME));
            map.put(id,name);
        }

        return map;
    }


}
