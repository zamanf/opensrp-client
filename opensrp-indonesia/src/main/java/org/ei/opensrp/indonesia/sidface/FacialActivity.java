package org.ei.opensrp.indonesia.sidface;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.qualcomm.snapdragon.sdk.face.FacialProcessing;

import org.ei.opensrp.indonesia.R;
import org.ei.opensrp.indonesia.face.CameraSurfacePreview;

/**
 * Created by wildan on 1/3/17.
 */
public class FacialActivity extends Activity implements Camera.PreviewCallback{

    public static final String TAG = FacialActivity.class.getSimpleName();
    private Camera objCamera;
    private CameraSurfacePreview mPreview;
    private FrameLayout preview;

    public void onCreate(Bundle savedStateBundle){
        super.onCreate(savedStateBundle);
        Log.e(TAG, "onCreate: " );

        setContentView(R.layout.activity_facial);
    }
    FacialProcessing faceProcess;
    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.e(TAG, "onPreviewFrame: " );

    }

    public void onStart(){
        super.onStart();
        Log.e(TAG, "onStart: " );
    }

    public void onResume(){
        super.onResume();
        Log.e(TAG, "onResume: ");
        if(objCamera != null){
            stopCamera();
        }

        startCamera();

    }

    public void onPause(){
        super.onPause();
        Log.e(TAG, "onPause: " );
    }

    public void onStop(){
        super.onStop();
        Log.e(TAG, "onStop: " );
    }
    public void onDestroy(){
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
    }

    public void startCamera(){
        objCamera = Camera.open(1);

        mPreview = new CameraSurfacePreview(FacialActivity.this, objCamera);
        preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        objCamera.setPreviewCallback(FacialActivity.this);
    }

    private void stopCamera(){
        objCamera.release();
    }

}
