package org.ei.opensrp.indonesia.face.util;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import org.ei.opensrp.repository.DetailsRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wildan on 1/4/17.
 */
public class Tools {

    private static final String TAG = Tools.class.getSimpleName();

    public static boolean WritePictureToFile(android.content.Context context, Bitmap bitmap, String entityId) {

        File pictureFile = getOutputMediaFile(entityId);

        if (pictureFile == null) {
            Log.e(TAG, "Error creating media file, check storage permissions ");
            return false;
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Log.e(TAG, "Wrote image to " + pictureFile);

            MediaScannerConnection.scanFile(context, new String[]{pictureFile.toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
            String photoPath = pictureFile.toString();
            Log.e(TAG, "Path Name = " + photoPath);


//            KIDetailActivity.details = new HashMap<>();

//            HashMap<String,String> details = new HashMap<>();
//            KIDetailActivity.details.put("profilepic",photoPath);
            DetailsRepository detailsRepository = org.ei.opensrp.Context.getInstance().detailsRepository();

            Long tsLong = System.currentTimeMillis()/1000;
            detailsRepository.add(entityId, "profilepic", photoPath, tsLong);
            detailsRepository.add(entityId, "pekerjaan", photoPath, tsLong);
//            kiclient.getDetails().get("profilepic");


            return true;

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
        return false;
    }

    private static File getOutputMediaFile(String entityId) {

        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "OPENSRP_SID");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            Log.e(TAG, "failed to find directory " + mediaStorageDir.getAbsolutePath());
            if (!mediaStorageDir.mkdirs()) {
                Log.e(TAG, "failed to create directory " + mediaStorageDir.getAbsolutePath());
                return null;
            }
        }

        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String filename = entity);
        return new File(String.format("%s%sOSRP_%s.jpg", mediaStorageDir.getPath(), File.separator, entityId));
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.MediaColumns._ID));
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        assert ca != null;
        ca.close();
        return null;

    }


}
