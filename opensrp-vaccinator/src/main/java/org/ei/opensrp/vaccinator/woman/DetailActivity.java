package org.ei.opensrp.vaccinator.woman;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.repository.ImageRepository;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.fragment.SmartClientRegisterFragment;
import org.ei.opensrp.view.controller.FormController;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static util.Utils.convertDateFormat;
import static util.Utils.profiePic;
import static util.Utils.setProfiePic;

public abstract class DetailActivity extends Activity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static String mCurrentPhotoPath;
    static File currentPhoto;
    static ImageView mImageView;
    protected static CommonPersonObjectClient client;
    protected static SmartClientRegisterFragment parentSmartRegisterFragment;

    public static void startDetailActivity(android.content.Context context, CommonPersonObjectClient clientobj, Class<? extends DetailActivity> detailActivity){
        client = clientobj;
        context.startActivity(new Intent(context, detailActivity));
        parentSmartRegisterFragment = null;
    }

    public static void startDetailActivity(android.content.Context context, CommonPersonObjectClient clientobj,
           Class<? extends DetailActivity> detailActivity, SmartClientRegisterFragment smartClientRegisterFragment){
        startDetailActivity(context, clientobj, detailActivity);
        parentSmartRegisterFragment = smartClientRegisterFragment;
    }

    protected abstract int layoutResId();

    protected abstract String pageTitle();

    protected abstract String titleBarId();

    protected abstract void generateView();

    protected abstract Class onBackActivity();

    protected abstract int profilePicResId();

    protected abstract String bindType();

    protected abstract boolean allowImageCapture();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //setting view
        setContentView(layoutResId());

        ((TextView)findViewById(R.id.detail_heading)).setText(pageTitle());

        ((TextView)findViewById(R.id.details_id_label)).setText(titleBarId());

        ((TextView)findViewById(R.id.detail_today)).setText("Today: "+ convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        generateView();

        ImageButton back = (ImageButton)findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailActivity.this, onBackActivity()));
                overridePendingTransition(0, 0);
            }
        });

        if(allowImageCapture()){
            mImageView = (ImageView)findViewById(profilePicResId());
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(mImageView);
                }
            });
        }

        if(allowImageCapture()){
            ProfileImage photo = ((ImageRepository) org.ei.opensrp.Context.getInstance().imageRepository()).findByEntityId(client.entityId(), "dp");
            if(photo != null){
                Bitmap ppic = profiePic(photo.getFilepath(), false);
                mImageView.setImageBitmap(addWatermark(getResources(), ppic, false));
            }
            else {
                Bitmap bitmap;
                if (mImageView.getDrawable() instanceof BitmapDrawable) {
                    bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                } else {
                    Drawable d = mImageView.getDrawable();
                    bitmap = Bitmap.createBitmap(d.getIntrinsicWidth(), d.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    d.draw(canvas);
                }
                mImageView.setImageBitmap(addWatermark(getResources(), bitmap, false));
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, onBackActivity()));
        overridePendingTransition(0, 0);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = bindType()+ "_"+timeStamp + "_"+client.entityId();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void saveImageReference(String bindobject, String entityid, Map<String, String> details){
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid, details);
        ProfileImage profileImage = new ProfileImage(UUID.randomUUID().toString(),
                Context.getInstance().anmService().fetchDetails().name(), entityid,
                "Image",details.get("profilepic"), ImageRepository.TYPE_Unsynced,"dp");
        ((ImageRepository) Context.getInstance().imageRepository()).add(profileImage);
    }

    private void dispatchTakePictureIntent(ImageView imageView) {
        this.mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                currentPhoto = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (currentPhoto != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPhoto));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            HashMap<String,String> details = new HashMap<String,String>();
            details.put("profilepic", currentPhoto.getAbsolutePath());
            saveImageReference(bindType(), client.entityId(), details);
            mImageView.setImageBitmap(addWatermark(getResources(), profiePic(currentPhoto.getAbsolutePath(), false), false));
        }
    }

    /**
     * Adds a watermark on the given image.
     */
    public Bitmap addWatermark(Resources res, Bitmap source, boolean highQuality) {
        int w, h;
        Canvas c;
        Paint paint;
        Bitmap bmp, watermark;

        Matrix matrix;
        RectF r;

        w = source.getWidth();
        h = source.getHeight();

        // Create the new bitmap
        bmp = Bitmap.createBitmap(w, h, highQuality?Bitmap.Config.ARGB_8888:Bitmap.Config.ARGB_4444);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

        // Copy the original bitmap into the new one
        c = new Canvas(bmp);
        c.drawBitmap(source, 0, 0, paint);

        // Load the watermark
        watermark = BitmapFactory.decodeResource(res, R.drawable.ic_pencil);
        // Scale the watermark to be approximately 20% of the source image height
        float scaley = (float) (((float) h * 0.20) / (float) watermark.getHeight());
        float scalex = (float) (((float) w * 0.20) / (float) watermark.getWidth());

        // Create the matrix
        matrix = new Matrix();
        matrix.postScale(scalex, scaley);
        // Determine the post-scaled size of the watermark
        r = new RectF(0, 0, watermark.getWidth(), watermark.getHeight());
        matrix.mapRect(r);
        // Move the watermark to the bottom right corner
        matrix.postTranslate(0, 0);

        // Draw the watermark
        c.drawBitmap(watermark, matrix, paint);
        // Free up the bitmap memory
        watermark.recycle();

        return bmp;
    }
}
