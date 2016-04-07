package org.ei.opensrp.vaccinator.woman;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static util.Utils.setProfiePic;

public abstract class DetailActivity extends Activity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static String mCurrentPhotoPath;
    static File currentPhoto;
    static ImageView mImageView;

    protected static CommonPersonObjectClient client;
    private static CommonPersonObjectController controller;

    public static void startDetailActivity(android.content.Context context, CommonPersonObjectClient clientobj, Class<? extends DetailActivity> detailActivity){
        client = clientobj;
        context.startActivity(new Intent(context, detailActivity));
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
            setProfiePic(mImageView, client.entityId());
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhoto.getPath(), options);
            mImageView.setImageBitmap(bitmap);
        }
    }
}
