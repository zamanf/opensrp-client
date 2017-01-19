package org.ei.opensrp.path.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.ProfileImage;
import org.ei.opensrp.path.R;
import org.ei.opensrp.path.domain.FormSubmissionWrapper;
import org.ei.opensrp.repository.ImageRepository;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import util.VaccinateActionUtils;

import static util.Utils.convertDateFormat;
import static util.Utils.setProfiePic;
import static util.Utils.setProfiePicFromPath;

public abstract class DetailActivity extends Activity {
    static final int REQUEST_TAKE_PHOTO = 1;
    static String mCurrentPhotoPath;
    static File currentPhoto;
    static ImageView mImageView;
    public static final String EXTRA_OBJECT = "extraObject";
    public static final String EXTRA_CLIENT = "extraClient";


    public static void startDetailActivity(android.content.Context context, CommonPersonObjectClient client, Class<? extends DetailActivity> detailActivity) {
        Intent intent = new Intent(context, detailActivity);

        Bundle bundle = new Bundle();
        bundle.putSerializable(EXTRA_CLIENT, client);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    protected abstract int layoutResId();

    protected abstract String pageTitle();

    protected abstract String titleBarId();

    protected abstract void generateView(CommonPersonObjectClient client);

    protected abstract Class onBackActivity();

    protected abstract Integer profilePicContainerId();

    protected abstract Integer defaultProfilePicResId(CommonPersonObjectClient client);

    protected abstract String bindType();

    protected abstract boolean allowImageCapture();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //setting view
        setContentView(layoutResId());

        ((TextView) findViewById(org.ei.opensrp.R.id.detail_heading)).setText(pageTitle());

        ((TextView) findViewById(org.ei.opensrp.R.id.details_id_label)).setText(titleBarId());

        ((TextView) findViewById(org.ei.opensrp.R.id.detail_today)).setText(convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        final CommonPersonObjectClient client = retrieveCommonPersonObjectClient();
        generateView(client);

        ImageButton back = (ImageButton) findViewById(org.ei.opensrp.R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(DetailActivity.this, onBackActivity()));
                overridePendingTransition(0, 0);
            }
        });

        if (allowImageCapture()) {
            mImageView = (ImageView) findViewById(profilePicContainerId());
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent(mImageView, client);
                }
            });


            ProfileImage photo = ((ImageRepository) org.ei.opensrp.Context.getInstance().imageRepository()).findByEntityId(client.entityId(), "dp");

            if (photo != null) {
                setProfiePicFromPath(this, mImageView, photo.getFilepath(), org.ei.opensrp.R.drawable.ic_pencil);
            } else {
                setProfiePic(this, mImageView, defaultProfilePicResId(client), org.ei.opensrp.R.drawable.ic_pencil);
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this, onBackActivity()));
        overridePendingTransition(0, 0);
    }

    private File createImageFile(CommonPersonObjectClient client) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = bindType() + "_" + timeStamp + "_" + client.entityId();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void saveImageReference(String bindobject, String entityid, Map<String, String> details) {
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid, details);
        ProfileImage profileImage = new ProfileImage(UUID.randomUUID().toString(),
                Context.getInstance().anmService().fetchDetails().name(), entityid,
                "Image", details.get("profilepic"), ImageRepository.TYPE_Unsynced, "dp");
        ((ImageRepository) Context.getInstance().imageRepository()).add(profileImage);
    }

    private void dispatchTakePictureIntent(ImageView imageView, CommonPersonObjectClient client) {
        this.mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                currentPhoto = createImageFile(client);
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
            HashMap<String, String> details = new HashMap<String, String>();
            details.put("profilepic", currentPhoto.getAbsolutePath());
            //saveImageReference(bindType(), client.entityId(), details);
            setProfiePicFromPath(this, mImageView, currentPhoto.getAbsolutePath(), org.ei.opensrp.R.drawable.ic_pencil);
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
        bmp = Bitmap.createBitmap(w, h, highQuality ? Bitmap.Config.ARGB_8888 : Bitmap.Config.ARGB_4444);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);

        // Copy the original bitmap into the new one
        c = new Canvas(bmp);
        c.drawBitmap(source, 0, 0, paint);

        // Load the watermark
        watermark = BitmapFactory.decodeResource(res, org.ei.opensrp.R.drawable.ic_pencil);
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

    // Custom form submission
    protected FormSubmissionWrapper retrieveFormSubmissionWrapper() {
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable(EXTRA_OBJECT);
            if (serializable != null && serializable instanceof FormSubmissionWrapper) {
                return (FormSubmissionWrapper) serializable;
            }
        }
        return null;
    }

    protected void saveFormSubmission(FormSubmissionWrapper formSubmissionWrapper){
        if (formSubmissionWrapper != null && formSubmissionWrapper.updates() > 0) {
            final android.content.Context context = this;
            String data = formSubmissionWrapper.updateFormSubmission();
            if (data != null) {
                VaccinateActionUtils.saveFormSubmission(context, data, formSubmissionWrapper.getEntityId(), formSubmissionWrapper.getFormName(), formSubmissionWrapper.getOverrides());
            }
        }
    }

    // Retrieve common person object client
    protected CommonPersonObjectClient retrieveCommonPersonObjectClient() {
        Bundle extras = this.getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable(EXTRA_CLIENT);
            if (serializable != null && serializable instanceof CommonPersonObjectClient) {
                return (CommonPersonObjectClient) serializable;
            }
        }
        return null;
    }


    // Demographics edits
    private void updateEditViews(TableLayout table, boolean toEdit) {
        for (int i = 0; i < table.getChildCount(); i++) {
            View view = table.getChildAt(i);
            if (view instanceof TableRow) {
                TableRow row = (TableRow) view;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View childView = row.getChildAt(j);
                    if (childView instanceof EditText) {
                        EditText editText = (EditText) childView;
                        if (toEdit) {
                            editText.setBackgroundResource(R.drawable.edit_text_style);
                            editText.setInputType(InputType.TYPE_CLASS_TEXT);
                        } else {
                            editText.setBackgroundColor(Color.WHITE);
                            editText.setInputType(InputType.TYPE_NULL);
                        }
                    }
                }
            }
        }
    }

    private void updateEditViews(List<TableLayout> tableLayouts, boolean toEdit) {
        if(tableLayouts != null && !tableLayouts.isEmpty()){
            for (TableLayout layout : tableLayouts) {
                updateEditViews(layout, toEdit);
            }
        }
    }

    protected void updateEditView(View view, List<TableLayout> tableLayouts) {
        Button button = (Button) view;
        if (view.getTag() != null && view.getTag() instanceof String) {
            if (view.getTag().equals(getString(R.string.edit))) {
                updateEditViews(tableLayouts, true);

                button.setText(getString(R.string.save));
                button.setTag(getString(R.string.save));
            } else if (view.getTag().equals(getString(R.string.save))) {
                updateEditViews(tableLayouts, false);

                button.setText(getString(R.string.edit));
                button.setTag(getString(R.string.edit));
            }
        }
    }

}
