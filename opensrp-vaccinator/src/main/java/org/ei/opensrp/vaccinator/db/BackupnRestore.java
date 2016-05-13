package org.ei.opensrp.vaccinator.db;

/**
 * Created by Safwan on 5/5/2016.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.os.Environment;

public class BackupnRestore {

    public boolean takeBackup(String packageName, String dbName, String destinationPath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+packageName+"//databases//"+dbName;
                String backupDBPath = destinationPath+"//"+dbName;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (!backupDB.exists()) {
                    backupDB.createNewFile();
                }
                if (currentDB.exists()) {
                    FileInputStream srcInputStream = new FileInputStream(currentDB);
                    FileOutputStream dstOutputStream = new FileOutputStream(backupDB);
                    FileChannel src = srcInputStream.getChannel();
                    FileChannel dst = dstOutputStream.getChannel();
                    dst.transferFrom(src, 0, src.size());
                    dst.close();
                    src.close();
                    dstOutputStream.close();
                    srcInputStream.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean restore(String packageName, String dbName, String destinationPath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+packageName+"//databases//"+dbName;
                String backupDBPath = destinationPath;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);
                if (!currentDB.exists()) {
                    currentDB.createNewFile();
                }
                if (currentDB.exists()) {
                    FileInputStream srcInputStream = new FileInputStream(backupDB);
                    FileOutputStream dstOutputStream = new FileOutputStream(currentDB);
                    FileChannel src = srcInputStream.getChannel();
                    FileChannel dst = dstOutputStream.getChannel();
                    dst.transferFrom(src, 0, src.size());
                    dst.close();
                    src.close();
                    dstOutputStream.close();
                    srcInputStream.close();
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}