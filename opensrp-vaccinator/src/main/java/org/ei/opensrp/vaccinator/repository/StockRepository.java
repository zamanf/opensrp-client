package org.ei.opensrp.vaccinator.repository;

import android.content.ContentValues;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.repository.DrishtiRepository;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 18-Nov-15.
 */
public class StockRepository extends DrishtiRepository {
    private  final static String ID_COLUMN="_id";
    private  final static String WASTED_COLUMN="wasted";
    private  final static String RECEIVED_COLUMN="received";
    private  final static String BALANCE_INHAND_COLUMN="balance";
    private  final static String USE_COLUMN="used";
    private  final static String VID_COLUMN="v_id";
    private  final static String TABLE_NAME="stock";

    private  final static String STOCK_TABLE="create table stock (_id int primary key ,v_id int not null ,wasted int , received int , balance int ,used int )";


    @Override
    protected void onCreate(SQLiteDatabase database) {
    database.execSQL(STOCK_TABLE);
    }

    public long add(int vaccine_id ,int wasted ,int received , int balance , int used ){

        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(VID_COLUMN, vaccine_id);
        contentValues.put(WASTED_COLUMN, wasted);
        contentValues.put(RECEIVED_COLUMN, received);
        contentValues.put(BALANCE_INHAND_COLUMN, balance);
        contentValues.put(USE_COLUMN, used);
        return database.insert(TABLE_NAME,null ,contentValues);

    }


    public long update(int id,int vaccine_id,int wasted ,int received , int balance , int used){
        SQLiteDatabase database = masterRepository.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(VID_COLUMN, vaccine_id);
        contentValues.put(WASTED_COLUMN, wasted);
        contentValues.put(RECEIVED_COLUMN, received);
        contentValues.put(BALANCE_INHAND_COLUMN, balance);
        contentValues.put(USE_COLUMN, used);
       final String _id=String.valueOf(id);

       return database.update(TABLE_NAME,contentValues, ID_COLUMN,new String[]{_id});

    }

    public void getAll(){


    }


    public void getStockByVaccineID(int vid){


    }

    public void getStockByMonthAndYear(String monthYear){


    }

    public void getStockByDate(String date){


    }

    public void calculateWastedByMonthAndYear(String monthAndYear){



    }

    public void calculateBalancedByMonthAndYear(String monthAndYear){


    }


    public void calculateReceivedByMonthAndYear(String monthAndYear){


    }

    public void calculateUsedByMonthAndYear(String monthAndYear){


    }
}
