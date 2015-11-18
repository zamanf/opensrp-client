package org.ei.opensrp.vaccinator.repository;

import android.content.ContentValues;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.repository.DrishtiRepository;

import java.util.HashMap;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 18-Nov-15.
 */
public class VaccinesRepository extends DrishtiRepository {
    private static final String VACCINE_COLUMN="vaccine_name";
    private static final String ID_COLUMN="_id";
    public static final String TABLE_NAME="vaccine";
    private static  String VACCINE_TABLE="CREATE TABLE vaccine(_id int primary key , vaccine_name VARCHAR(100) )";

    @Override
    protected void onCreate(SQLiteDatabase database) {
        database.execSQL(VACCINE_TABLE);
    }

    public long add(String name){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(VACCINE_COLUMN, name);
        return database.insert(TABLE_NAME,null ,contentValues);


    }

    public HashMap<Integer,String> getAllVaccines(){
        HashMap<Integer,String> map=new HashMap<Integer,String>();
        SQLiteDatabase database = masterRepository.getWritableDatabase();

       // database.
        Cursor cursor=database.rawQuery("Select * from vaccine;" , null);
        for (int i=0; i<cursor.getCount();i++){

;

           int id= cursor.getInt(cursor.getColumnIndex(ID_COLUMN));
           String name= cursor.getString(cursor.getColumnIndex(VACCINE_COLUMN));
            map.put(id,name);
        }

        return map;
    }


}
