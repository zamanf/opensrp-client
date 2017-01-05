/* ======================================================================
 *  Copyright � 2014 Qualcomm Technologies, Inc. All Rights Reserved.
 *  QTI Proprietary and Confidential.
 *  =====================================================================
 *  
 * @file:   ImageConfirmation.java
 *
 */
package org.ei.opensrp.indonesia.face;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.face.util.Tools;
import org.ei.opensrp.indonesia.kartu_ibu.KIDetailActivity;

import java.util.Arrays;

public class ImageConfirmation extends Activity {

    private static String TAG = ImageConfirmation.class.getSimpleName();
    Bitmap storedBitmap;
    ImageView confirmationView;
    ImageView confirmButton;
    ImageView trashButton;
    private String entityId;
    private Rect[] rects;
    private boolean faceFlag = false;
    private Bitmap mutableBitmap;
    private boolean identifyPerson = false;
    private FacialProcessing objFace;
    private FaceData[] faceDatas;
    private int arrayPossition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_face_confirmation);

        Bundle extras = getIntent().getExtras();
        byte[] data = getIntent().getByteArrayExtra("com.qualcomm.sdk.smartshutterapp.ImageConfirmation");
        int angle = extras.getInt("com.qualcomm.sdk.smartshutterapp.ImageConfirmation.orientation");
        boolean switchCamera = extras.getBoolean("com.qualcomm.sdk.smartshutterapp.ImageConfirmation.switchCamera");
        entityId = extras.getString("org.sid.sidface.ImageConfirmation.id");

        storedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, null);

        ImageView confirmationView = (ImageView) findViewById(R.id.iv_confirmationView);    // New view on which the image will be displayed.

        Matrix mat = new Matrix();
        if (!switchCamera) {
            mat.postRotate(angle == 90 ? 270 : (angle == 180 ? 180 : 0));
            mat.postScale(-1, 1);
            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        } else {
            mat.postRotate(angle == 90 ? 90 : (angle == 180 ? 180 : 0));
            storedBitmap = Bitmap.createBitmap(storedBitmap, 0, 0, storedBitmap.getWidth(), storedBitmap.getHeight(), mat, true);
        }
//        TODO : Image from gallery

        objFace = SmartShutterActivity.faceProc;
//        if (objFace == null) {
//            objFace = FacialProcessing.getInstance();
//        }


        boolean result = objFace.setBitmap(storedBitmap);
        faceDatas = objFace.getFaceData();

        int imageViewSurfaceWidth = storedBitmap.getWidth();
        int imageViewSurfaceHeight = storedBitmap.getHeight();
//        int imageViewSurfaceWidth = confirmationView.getWidth();
//        int imageViewSurfaceHeight = confirmationView.getHeight();

        Bitmap workingBitmap = Bitmap.createScaledBitmap(storedBitmap,
                imageViewSurfaceWidth, imageViewSurfaceHeight, false);
        mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);

        objFace.normalizeCoordinates(imageViewSurfaceWidth, imageViewSurfaceHeight);

        if(result){
            Log.e(TAG, "onCreate: SetBitmap objFace "+"Success" );
            if(faceDatas != null){
                Log.e(TAG, "onCreate: faceDatas "+"NotNull" );
                rects = new Rect[faceDatas.length];
                for (int i = 0; i < faceDatas.length; i++) {
                    Rect rect = faceDatas[i].rect;
                    rects[i] = rect;

                    float pixelDensity = getResources().getDisplayMetrics().density;
                    if (identifyPerson) {
                        String selectedPersonId = Integer.toString(faceDatas[i].getPersonId());
                        String personName = entityId;
//                        Iterator<HashMap.Entry<String, String>> iter = hash
//                                .entrySet().iterator();
//                        while (iter.hasNext()) {
//                            HashMap.Entry<String, String> entry = iter
//                                    .next();
//                            if (entry.getValue().equals(
//                                    selectedPersonId)) {
//                                personName = entry.getKey();
//                            }
//                        }
                        Tools.drawInfo(rect, mutableBitmap, pixelDensity, personName);
                    } else {
                        Tools.drawRectFace(rect, mutableBitmap, pixelDensity);
                        Log.e(TAG, "onCreate: PersonId "+faceDatas[i].getPersonId() );
                        if(faceDatas[i].getPersonId() < 0){
                            arrayPossition = i;
                        } else {
                            Log.e(TAG, "onCreate: Similar face found "+
                                    Integer.toString(faceDatas[i].getRecognitionConfidence()) );
                        }
                    }
                }
            } else {
                Log.e(TAG, "onCreate: faceDatas "+"Null" );
            }
        } else {
            Log.e(TAG, "onCreate: SetBitmap objFace"+"Failed" );
        }

//        confirmationView.setImageBitmap(storedBitmap);            // Setting the view with the bitmap image that came in.
        confirmationView.setImageBitmap(mutableBitmap);            // Setting the view with the bitmap image that came in.

        buttonJob();
    }

    /**
     *
     */
    private void buttonJob() {
        // If approved then save the image and close.
        confirmButton = (ImageView) findViewById(R.id.iv_approve);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                saveAndClose(entityId);
            }

        });

        confirmButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    confirmButton.setImageResource(R.drawable.confirm_highlighted);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    confirmButton.setImageResource(R.drawable.confirm);
                }

                return false;
            }
        });

        // Trash the image and return back to the camera preview.
        trashButton = (ImageView) findViewById(R.id.iv_cancel);
        trashButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent resultIntent = new Intent();
                setResult(RESULT_CANCELED, resultIntent);
                ImageConfirmation.this.finish();
            }

        });

        trashButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {

                if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
                    trashButton.setImageResource(R.drawable.trash_highlighted);
                } else if (arg1.getAction() == MotionEvent.ACTION_UP) {
                    trashButton.setImageResource(R.drawable.trash);
                }

                return false;
            }
        });

    }


    /*
     * Function to save image and get back to the camera preview.
     */
    private void saveAndClose(String entityId) {
        Log.e(TAG, "saveAndClose: "+arrayPossition );
        int res = objFace.addPerson(arrayPossition);
        Log.e(TAG, "saveAndClose: "+res );
        Log.e(TAG, "saveAndClose: "+ Arrays.toString(objFace.serializeRecogntionAlbum()));
//        SmartShutterActivity.WritePictureToFile(ImageConfirmation.this, storedBitmap);
        Tools.WritePictureToFile(ImageConfirmation.this, storedBitmap, entityId);
//        resultIntent.putExtra("com.qualcomm.sdk.smartshutterapp.SmartShutterActivity.thumbnail", thumbnail);
        ImageConfirmation.this.finish();
        Intent resultIntent = new Intent(this, KIDetailActivity.class);
        setResult(RESULT_OK, resultIntent);
        startActivityForResult(resultIntent, 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_confirmation, menu);
        return true;
    }

}
