package org.ei.opensrp.dghs.HH_child;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.commonregistry.CommonFtsObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.dghs.R;
import org.ei.opensrp.dghs.domain.VaccineRepo;
import org.ei.opensrp.dghs.domain.VaccineWrapper;
import org.ei.opensrp.dghs.hh_member.HouseHoldDetailActivity;
import org.ei.opensrp.dghs.vaccineFragment.UndoVaccinationDialogFragment;
import org.ei.opensrp.dghs.vaccineFragment.VaccinationActionListener;
import org.ei.opensrp.dghs.vaccineFragment.VaccinationDialogFragment;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.form.FieldOverrides;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import util.ImageCache;
import util.ImageFetcher;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;
import static org.ei.opensrp.util.StringUtil.humanize;

/**
 * Created by raihan on 5/11/15.
 */
public class ChildDetailActivity extends Activity implements VaccinationActionListener{

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private static int mImageThumbSize;
    private static int mImageThumbSpacing;

    private static ImageFetcher mImageFetcher;

    HashMap<String,String> update =  new HashMap<String, String>();

    Button undo_bcg;
    Button undo_opv0;
    Button undo_pcv1;
    Button undo_opv1;
    Button undo_penta1;
    Button undo_pcv2;
    Button undo_opv2;
    Button undo_penta2;
    Button undo_pcv3;
    Button undo_opv3;
    Button undo_penta3;
    Button undo_ipv;
    Button undo_measles1;
    Button undo_measles2;



    //image retrieving

    public static CommonPersonObjectClient childclient;
    private TextView childdetail_bcg;
    private TextView childdetail_penta1;
    private TextView childdetail_penta2;
    private TextView childdetail_penta3;
    private TextView childdetail_opv0;
    private TextView childdetail_pcv1;
    private TextView childdetail_opv1;
    private TextView childdetail_pcv2;
    private TextView childdetail_opv2;
    private TextView childdetail_pcv3;
    private TextView childdetail_opv3;
    private TextView childdetail_ipv;
    private TextView childdetail_measles1;
    private TextView childdetail_measles2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.child_detail_activity);
        TextView name = (TextView) findViewById(R.id.child_detail_name_field);
        TextView brid = (TextView) findViewById(R.id.child_detail_brid_nid_field);
        TextView fathername = (TextView) findViewById(R.id.child_detail_fathername_field);
        TextView mothername = (TextView) findViewById(R.id.child_detail_mothername_field);
        TextView epicarno = (TextView) findViewById(R.id.child_detail_epicard_field);
        TextView birthdate = (TextView) findViewById(R.id.child_detail_birthdate_field);
        TextView contactno = (TextView) findViewById(R.id.child_detail_contactno_field);
        TextView address = (TextView) findViewById(R.id.child_detail_address_field);
        ImageView profilepic = (ImageView) findViewById(R.id.childdetailprofileview);


        childdetail_bcg = (TextView) findViewById(R.id.childdetail_bcg);
        childdetail_opv0 = (TextView) findViewById(R.id.childdetail_opv0);
        childdetail_pcv1 = (TextView) findViewById(R.id.childdetail_pcv1);
        childdetail_opv1 = (TextView) findViewById(R.id.childdetail_opv1);
        childdetail_penta1 = (TextView) findViewById(R.id.childdetail_penta1);
        childdetail_pcv2 = (TextView) findViewById(R.id.childdetail_pcv2);
        childdetail_opv2 = (TextView) findViewById(R.id.childdetail_opv2);
        childdetail_penta2 = (TextView) findViewById(R.id.childdetail_penta2);
        childdetail_pcv3 = (TextView) findViewById(R.id.childdetail_pcv3);
        childdetail_opv3 = (TextView) findViewById(R.id.childdetail_opv3);
        childdetail_penta3 = (TextView) findViewById(R.id.childdetail_penta3);
        childdetail_ipv= (TextView) findViewById(R.id.childdetail_ipv);
        childdetail_measles1 = (TextView) findViewById(R.id.childdetail_measles1);
        childdetail_measles2 = (TextView) findViewById(R.id.childdetail_measles2);

        undo_bcg = (Button) findViewById(R.id.bcg_undo);
        undo_opv0 = (Button) findViewById(R.id.opv0_undo);
        undo_pcv1 = (Button) findViewById(R.id.pcv1_undo);
        undo_opv1 = (Button) findViewById(R.id.opv1_undo);
        undo_penta1 = (Button) findViewById(R.id.penta1_undo);
        undo_pcv2 = (Button) findViewById(R.id.pcv2_undo);
        undo_opv2 = (Button) findViewById(R.id.opv2_undo);
        undo_penta2 = (Button) findViewById(R.id.penta2_undo);
        undo_pcv3 = (Button) findViewById(R.id.pcv3_undo);
        undo_opv3 = (Button) findViewById(R.id.opv3_undo);
        undo_penta3 = (Button) findViewById(R.id.penta3_undo);
        undo_ipv= (Button) findViewById(R.id.ipv_undo);
        undo_measles1 = (Button) findViewById(R.id.measles1_undo);
        undo_measles2 = (Button) findViewById(R.id.measles2_undo);

        if ((childclient.getDetails().get("Child_gender") != null ? childclient.getDetails().get("Child_gender") : "").equalsIgnoreCase("1")) {
            profilepic.setImageResource(R.drawable.child_boy_infant);
//                newborn_or_fp.setText("Family Planning");
        } else {
            profilepic.setImageResource(R.drawable.child_girl_infant);
//                newborn_or_fp.setVisibility(View.INVISIBLE);
        }
        try {
            if (childclient.getDetails().get("profilepic") != null) {
                HouseHoldDetailActivity.setImagetoHolder(this, childclient.getDetails().get("profilepic"), profilepic, R.drawable.child_boy_infant);
            }
        }catch (Exception e){

        }


//        TextView age = (TextView) findViewById(R.id.age);
//        TextView godhhid = (TextView) findViewById(R.id.gobhhid);
//        TextView village = (TextView) findViewById(R.id.ward);
//
        ImageButton back = (ImageButton) findViewById(R.id.btn_back_to_home);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//
        name.setText(humanize((childclient.getColumnmaps().get("Member_Fname") != null ? childclient.getColumnmaps().get("Member_Fname") : "").replace("+", "_")));//
        brid.setText((childclient.getDetails().get("Member_BRID") != null ? childclient.getDetails().get("Member_BRID") : "").replace("+", "_"));//
        fathername.setText((childclient.getDetails().get("Child_father_name") != null ? childclient.getDetails().get("Child_father_name") : ""));
        mothername.setText((childclient.getColumnmaps().get("Child_mother_name") != null ? childclient.getColumnmaps().get("Child_mother_name") : ""));
        epicarno.setText((childclient.getDetails().get("epi_card_number") != null ? childclient.getDetails().get("epi_card_number") : ""));
        birthdate.setText((childclient.getDetails().get("Child_dob") != null ? childclient.getDetails().get("Child_dob") : ""));
        contactno.setText((childclient.getDetails().get("contact_phone_number") != null ? childclient.getDetails().get("contact_phone_number") : ""));
        address.setText((childclient.getDetails().get("HH_Address") != null ? childclient.getDetails().get("HH_Address") : ""));

        ChildVaccinecheck(childclient,childdetail_bcg,findViewById(R.id.child_block1),"final_bcg","child_bcg");
        ChildVaccinecheck(childclient,childdetail_opv0,findViewById(R.id.child_block5),"final_opv0","child_opv0");
        ChildVaccinecheck(childclient,childdetail_pcv1,findViewById(R.id.child_block10),"final_pcv1","child_pcv1");
        ChildVaccinecheck(childclient,childdetail_opv1,findViewById(R.id.child_block6),"final_opv1","child_opv1");
        ChildVaccinecheck(childclient,childdetail_penta1,findViewById(R.id.child_block2),"final_penta1","child_penta1");
        ChildVaccinecheck(childclient,childdetail_pcv2,findViewById(R.id.child_block11),"final_pcv2","child_pcv2");
        ChildVaccinecheck(childclient,childdetail_opv2,findViewById(R.id.child_block7),"final_opv2","child_opv2");
        ChildVaccinecheck(childclient,childdetail_penta2,findViewById(R.id.child_block3),"final_penta2","child_penta2");
        ChildVaccinecheck(childclient,childdetail_pcv3,findViewById(R.id.child_block12),"final_pcv3","child_pcv3");
        ChildVaccinecheck(childclient,childdetail_opv3,findViewById(R.id.child_block8),"final_opv3","child_opv3");
        ChildVaccinecheck(childclient,childdetail_penta3,findViewById(R.id.child_block4),"final_penta3","child_penta3");
        ChildVaccinecheck(childclient,childdetail_ipv,findViewById(R.id.child_block9),"final_ipv","child_ipv");
        ChildVaccinecheck(childclient,childdetail_measles1,findViewById(R.id.child_block13),"final_measles1","child_measles1");
        ChildVaccinecheck(childclient,childdetail_measles2,findViewById(R.id.child_block14),"final_measles2","child_measles2");


    }
    public LinearLayout makevaccinerow (String vaccinename,String vaccinedate){
        LinearLayout vaccinerow = (LinearLayout) getLayoutInflater().inflate(R.layout.vaccine_row, null);
        ((TextView)vaccinerow.findViewById(R.id.vaccinename)).setText(vaccinename);
        ((TextView)vaccinerow.findViewById(R.id.vaccine_date)).setText(vaccinedate);

        return vaccinerow;
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }
    static final int REQUEST_TAKE_PHOTO = 1;
   static ImageView mImageView;
    static File currentfile;
    static String bindobject;
    static String entityid;
    private void dispatchTakePictureIntent(ImageView imageView) {
        mImageView = imageView;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                currentfile = photoFile;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            String imageBitmap = (String) extras.get(MediaStore.EXTRA_OUTPUT);
//            Toast.makeText(this,imageBitmap,Toast.LENGTH_LONG).show();
            HashMap <String,String> details = new HashMap<String,String>();
            details.put("profilepic",currentfile.getAbsolutePath());
            saveimagereference(bindobject,entityid,details);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(currentfile.getPath(), options);
            mImageView.setImageBitmap(bitmap);
        }
    }
    public void saveimagereference(String bindobject,String entityid,Map<String,String> details){
        Context.getInstance().allCommonsRepositoryobjects(bindobject).mergeDetails(entityid,details);
//                Elcoclient.entityId();
//        Toast.makeText(this,entityid,Toast.LENGTH_LONG).show();
    }
    public static void setImagetoHolder(Activity activity,String file, ImageView view, int placeholder){
        mImageThumbSize = 300;
        mImageThumbSpacing = Context.getInstance().applicationContext().getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);


        ImageCache.ImageCacheParams cacheParams =
                new ImageCache.ImageCacheParams(activity, IMAGE_CACHE_DIR);
             cacheParams.setMemCacheSizePercent(0.50f); // Set memory cache to 25% of app memory
        mImageFetcher = new ImageFetcher(activity, mImageThumbSize);
        mImageFetcher.setLoadingImage(placeholder);
        mImageFetcher.addImageCache(activity.getFragmentManager(), cacheParams);
//        Toast.makeText(activity,file,Toast.LENGTH_LONG).show();
        mImageFetcher.loadImage("file:///"+file,view);

//        Uri.parse(new File("/sdcard/cats.jpg")






//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(file, options);
//        view.setImageBitmap(bitmap);
    }
    public void ChildVaccinecheck(final CommonPersonObjectClient childClient, TextView tt1TextView, View block, String ttfinalKey, final String ttschedulename){
        boolean active = false;
        if(!(childClient.getDetails().get(ttfinalKey)!=null?childClient.getDetails().get(ttfinalKey):"").equalsIgnoreCase("")){
            block.setBackgroundColor(getResources().getColor(R.color.alert_complete_green));
            String text = (childClient.getDetails().get(ttfinalKey)!=null?childClient.getDetails().get(ttfinalKey):"");
            active = false;
            tt1TextView.setText(text);
        }else{

            List<Alert> child_alertlist_for_client = Context.getInstance().alertService().findByEntityIdAndAlertNames(childClient.entityId(), ttschedulename);
            if(child_alertlist_for_client.size()>0) {
                for (int i = 0; i < child_alertlist_for_client.size(); i++) {
                    if (child_alertlist_for_client.get(i).status().value().equalsIgnoreCase("upcoming")) {
                        block.setBackgroundColor(getResources().getColor(R.color.alert_upcoming_yellow));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,childClient);
                        tt1TextView.setText(text);
                        active = true;
                    } else if (child_alertlist_for_client.get(i).status().value().equalsIgnoreCase("urgent")) {
                        block.setBackgroundColor(getResources().getColor(R.color.alert_urgent_red));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,childClient);
                        tt1TextView.setText(text);
                        active = true;
                    } else if (child_alertlist_for_client.get(i).status().value().equalsIgnoreCase("expired")) {
                        block.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,childClient);
                        tt1TextView.setText(text);
                        active = false;
                    } else if (child_alertlist_for_client.get(i).status().value().equalsIgnoreCase("normal")) {
                        block.setBackgroundColor(getResources().getColor(R.color.alert_upcoming_light_blue));
                        String text = "";
                        text = getVaccineDateText(ttschedulename,childClient);
                        tt1TextView.setText(Html.fromHtml(text));
                        active = false;
                    }

                }
            }else{
                String text = " ";
                tt1TextView.setText(text);
                active = false;
            }

        }
        if(active) {
            tt1TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    android.content.Context context = ChildDetailActivity.this;
                    VaccineWrapper vaccineWrapper = new VaccineWrapper();
                    if (ttschedulename.equalsIgnoreCase("child_bcg")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.bcg);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_opv0")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.opv0);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_pcv1")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.pcv1);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_opv1")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.opv1);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_penta1")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.penta1);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_pcv2")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.pcv2);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_opv2")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.opv2);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_penta2")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.penta2);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_pcv3")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.pcv3);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_opv3")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.opv3);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_penta3")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.penta3);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_ipv")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.ipv);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_measles1")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.measles1);
                    }
                    if (ttschedulename.equalsIgnoreCase("child_measles1")) {
                        vaccineWrapper.setVaccine(VaccineRepo.Vaccine.measles2);
                    }
//                vaccineWrapper.setVaccineDate((DateTime) m.get("date"));
//                vaccineWrapper.setAlert((Alert) m.get("alert"));
//                vaccineWrapper.setPreviousVaccine(previousVaccine);
                    vaccineWrapper.setCompact(true);

                    vaccineWrapper.setPatientNumber(childClient.getColumnmaps().get(""));
                    vaccineWrapper.setPatientName(childClient.getColumnmaps().get(""));


                    FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                    Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(VaccinationDialogFragment.DIALOG_TAG);
                    if (prev != null) {
                        ft.remove(prev);
                    }
                    ft.addToBackStack(null);
                    VaccinationDialogFragment vaccinationDialogFragment = VaccinationDialogFragment.newInstance(context, vaccineWrapper);
                    vaccinationDialogFragment.show(ft, VaccinationDialogFragment.DIALOG_TAG);

                }
            });
        }


    }

    private String getVaccineDateText(String Schedulename, CommonPersonObjectClient pc) {
        if (Schedulename.equalsIgnoreCase("child_bcg")) {
            return ((pc.getDetails().get("Child_dob") != null ? pc.getDetails().get("Child_dob") : ""));
        }
        if (Schedulename.equalsIgnoreCase("child_opv0")) {
            return ((pc.getDetails().get("Child_dob") != null ? pc.getDetails().get("Child_dob") : ""));
        }
        if (Schedulename.equalsIgnoreCase("child_pcv1")) {
            return ( setDate((pc.getDetails().get("Child_dob") != null ? pc.getDetails().get("Child_dob") : ""),42));
        }
        if (Schedulename.equalsIgnoreCase("child_opv1")) {
            return (setDate((pc.getDetails().get("final_opv0") != null ? pc.getDetails().get("final_opv0") : ""),42));
        }
        if (Schedulename.equalsIgnoreCase("child_penta1")) {
            return (setDate((pc.getDetails().get("Child_dob") != null ? pc.getDetails().get("Child_dob") : ""),42));
        }
        if (Schedulename.equalsIgnoreCase("child_pcv2")) {
            return (setDate((pc.getDetails().get("final_pcv1") != null ? pc.getDetails().get("final_pcv1") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("child_opv2")) {
            return (setDate((pc.getDetails().get("final_opv1") != null ? pc.getDetails().get("final_opv1") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("child_penta2")) {
            return (setDate((pc.getDetails().get("final_penta1") != null ? pc.getDetails().get("final_penta1") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("child_pcv3")) {
            return (setDate((pc.getDetails().get("final_pcv2") != null ? pc.getDetails().get("final_pcv2") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("child_opv3")) {
            return (setDate((pc.getDetails().get("final_opv2") != null ? pc.getDetails().get("final_opv2") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("child_penta3")) {
            return (setDate((pc.getDetails().get("final_penta2") != null ? pc.getDetails().get("final_penta2") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("child_ipv")) {
            return (setDate((pc.getDetails().get("final_opv2") != null ? pc.getDetails().get("final_opv2") : ""),28));
        }
        if (Schedulename.equalsIgnoreCase("child_measles1")) {
            return (setDate((pc.getDetails().get("Child_dob") != null ? pc.getDetails().get("Child_dob") : ""),273));
        }
        if (Schedulename.equalsIgnoreCase("child_measles2")) {
            return (setDate((pc.getDetails().get("final_measles1") != null ? pc.getDetails().get("final_measles1") : ""),182));
        }
        return "";
    }
    public String setDate(String date, int daystoadd) {

        Date lastdate = converdatefromString(date);

        if(lastdate!=null){
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(lastdate);
            calendar.add(Calendar.DATE, daystoadd);//8 weeks
            lastdate.setTime(calendar.getTime().getTime());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            //            String result = String.format(Locale.ENGLISH, format.format(lastdate) );
            return (format.format(lastdate));
            //             due_visit_date.append(format.format(lastdate));

        }else{
            return "";
        }
    }
    public Date converdatefromString(String dateString){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        }catch (Exception e){
            return null;
        }
        return convertedDate;
    }
//    bcg("BCG", 1, null, 366, 0, 0, 4, 366,  "child"),
//    penta1("Penta 1", 3, null, 366, 42, 0, 4, 366,  "child"),
//    penta2("Penta 2", 6, penta1, 366, 70, 28, 4, 366,  "child"),
//    penta3("Penta 3", 9, penta2, 366, 98, 28, 4, 366,  "child"),
//    opv0("OPV 0", 2, null, 1830, 0, 0, 4, 1830,  "child"),
//    opv1("OPV 1", 4, null, 1830, 42, 0, 4, 1830,  "child"),
//    opv2("OPV 2", 7, opv1, 1830, 70, 28, 4, 1830,  "child"),
//    opv3("OPV 3", 10, opv2, 1830, 98, 28, 4, 1830,  "child"),
//    ipv("IPV", 11, opv2, 1830, 98, 28, 4, 1830,  "child"),
//    pcv1("PCV 1", 5, null, 1830, 42, 0, 4, 1830,  "child"),
//    pcv2("PCV 2", 8, pcv1, 1830, 70, 28, 4, 1830,  "child"),
//    pcv3("PCV 3", 12, pcv2, 1830, 98, 28, 4, 1830,  "child"),
//    measles1("Measles 1", 13, null, 1830, 273, 0, 14, 1830,  "child"),
//    measles2("Measles 2", 14, measles1, 1830, 458, 28, 70, 1830,  "child"),
    @Override
    public void onVaccinateToday(VaccineWrapper tag) {
        if(tag.getVaccine().display().equalsIgnoreCase("BCG")) {
            update.put("final_bcg", tag.getUpdatedVaccineDateAsString());
            update.put("bcg", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "BCG");
            vaccine_complete_from_pop_up(childdetail_bcg,(View)findViewById(R.id.child_block1),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_bcg);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 1")) {
            update.put("final_penta1", tag.getUpdatedVaccineDateAsString());
            update.put("penta1_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Penta 1");
            vaccine_complete_from_pop_up(childdetail_penta1,(View)findViewById(R.id.child_block2),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_penta1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 2")) {
            update.put("final_penta2", tag.getUpdatedVaccineDateAsString());
            update.put("penta2_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Penta 2");
            vaccine_complete_from_pop_up(childdetail_penta2,(View)findViewById(R.id.child_block3),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_penta2);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 3")) {
            update.put("final_penta3", tag.getUpdatedVaccineDateAsString());
            update.put("penta3_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Penta 3");
            vaccine_complete_from_pop_up(childdetail_penta3,(View)findViewById(R.id.child_block4),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_penta3);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 0")) {
            update.put("final_opv0", tag.getUpdatedVaccineDateAsString());
            update.put("opv0_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 0");
            vaccine_complete_from_pop_up(childdetail_opv0,(View)findViewById(R.id.child_block5),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv0);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 1")) {
            update.put("final_opv1", tag.getUpdatedVaccineDateAsString());
            update.put("opv1_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 1");
            vaccine_complete_from_pop_up(childdetail_opv1,(View)findViewById(R.id.child_block6),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 2")) {
            update.put("final_opv2", tag.getUpdatedVaccineDateAsString());
            update.put("opv2_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 2");
            vaccine_complete_from_pop_up(childdetail_opv2,(View)findViewById(R.id.child_block7),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv2);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 3")) {
            update.put("final_opv3", tag.getUpdatedVaccineDateAsString());
            update.put("opv3_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 3");
            vaccine_complete_from_pop_up(childdetail_opv3,(View)findViewById(R.id.child_block8),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv3);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("IPV")) {
            update.put("final_ipv", tag.getUpdatedVaccineDateAsString());
            update.put("ipv", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "IPV");
            vaccine_complete_from_pop_up(childdetail_ipv,(View)findViewById(R.id.child_block9),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_ipv);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 1")) {
            update.put("final_pcv1", tag.getUpdatedVaccineDateAsString());
            update.put("pcv1_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "PCV 1");
            vaccine_complete_from_pop_up(childdetail_pcv1,(View)findViewById(R.id.child_block10),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_pcv1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 2")) {
            update.put("final_pcv2", tag.getUpdatedVaccineDateAsString());
            update.put("pcv2_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "PCV 2");
            vaccine_complete_from_pop_up(childdetail_pcv2,(View)findViewById(R.id.child_block11),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_pcv2);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 3")) {
            update.put("final_pcv3", tag.getUpdatedVaccineDateAsString());
            update.put("pcv3_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "PCV 3");
            vaccine_complete_from_pop_up(childdetail_pcv3,(View)findViewById(R.id.child_block12),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_pcv3);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Measles 1")) {
            update.put("final_measles1", tag.getUpdatedVaccineDateAsString());
            update.put("measles1_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Measles 1");
            vaccine_complete_from_pop_up(childdetail_measles1,(View)findViewById(R.id.child_block13),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_measles1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Measles 2")) {
            update.put("final_measles2", tag.getUpdatedVaccineDateAsString());
            update.put("measles2_dose_today", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Measles 2");
            vaccine_complete_from_pop_up(childdetail_measles2,(View)findViewById(R.id.child_block14),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_measles2);
        }
    }

    @Override
    public void onVaccinateEarlier(VaccineWrapper tag) {
        if(tag.getVaccine().display().equalsIgnoreCase("BCG")) {
            update.put("final_bcg", tag.getUpdatedVaccineDateAsString());
            update.put("bcg_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "BCG");
            vaccine_complete_from_pop_up(childdetail_bcg,(View)findViewById(R.id.child_block1),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_bcg);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 1")) {
            update.put("final_penta1", tag.getUpdatedVaccineDateAsString());
            update.put("penta1_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Penta 1");
            vaccine_complete_from_pop_up(childdetail_penta1,(View)findViewById(R.id.child_block2),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_penta1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 2")) {
            update.put("final_penta2", tag.getUpdatedVaccineDateAsString());
            update.put("penta2_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Penta 2");
            vaccine_complete_from_pop_up(childdetail_penta2,(View)findViewById(R.id.child_block3),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_penta2);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 3")) {
            update.put("final_penta3", tag.getUpdatedVaccineDateAsString());
            update.put("penta3_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Penta 3");
            vaccine_complete_from_pop_up(childdetail_penta3,(View)findViewById(R.id.child_block4),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_penta3);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 0")) {
            update.put("final_opv0", tag.getUpdatedVaccineDateAsString());
            update.put("opv0_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 0");
            vaccine_complete_from_pop_up(childdetail_opv0,(View)findViewById(R.id.child_block5),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv0);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 1")) {
            update.put("final_opv1", tag.getUpdatedVaccineDateAsString());
            update.put("opv1_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 1");
            vaccine_complete_from_pop_up(childdetail_opv1,(View)findViewById(R.id.child_block6),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 2")) {
            update.put("final_opv2", tag.getUpdatedVaccineDateAsString());
            update.put("opv2_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 2");
            vaccine_complete_from_pop_up(childdetail_opv2,(View)findViewById(R.id.child_block7),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv2);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 3")) {
            update.put("final_opv3", tag.getUpdatedVaccineDateAsString());
            update.put("opv3_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "OPV 3");
            vaccine_complete_from_pop_up(childdetail_opv3,(View)findViewById(R.id.child_block8),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_opv3);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("IPV")) {
            update.put("final_ipv", tag.getUpdatedVaccineDateAsString());
            update.put("ipv_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "IPV");
            vaccine_complete_from_pop_up(childdetail_ipv,(View)findViewById(R.id.child_block9),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_ipv);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 1")) {
            update.put("final_pcv1", tag.getUpdatedVaccineDateAsString());
            update.put("pcv1_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "PCV 1");
            vaccine_complete_from_pop_up(childdetail_pcv1,(View)findViewById(R.id.child_block10),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_pcv1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 2")) {
            update.put("final_pcv2", tag.getUpdatedVaccineDateAsString());
            update.put("pcv2_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "PCV 2");
            vaccine_complete_from_pop_up(childdetail_pcv2,(View)findViewById(R.id.child_block11),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_pcv2);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 3")) {
            update.put("final_pcv3", tag.getUpdatedVaccineDateAsString());
            update.put("pcv3_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "PCV 3");
            vaccine_complete_from_pop_up(childdetail_pcv3,(View)findViewById(R.id.child_block12),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_pcv3);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Measles 1")) {
            update.put("final_measles1", tag.getUpdatedVaccineDateAsString());
            update.put("measles1_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Measles 1");
            vaccine_complete_from_pop_up(childdetail_measles1,(View)findViewById(R.id.child_block13),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_measles1);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Measles 2")) {
            update.put("final_measles2", tag.getUpdatedVaccineDateAsString());
            update.put("measles2_retro", tag.getUpdatedVaccineDateAsString());
            update.put("child_vaccines_2", "Measles 2");
            vaccine_complete_from_pop_up(childdetail_measles2,(View)findViewById(R.id.child_block14),tag.getUpdatedVaccineDateAsString());
            make_undo_visible(tag,undo_measles2);
        }
    }

    @Override
    public void onUndoVaccination(VaccineWrapper tag) {
        update.remove("child_vaccines_2");
        ChildVaccinecheck(childclient,childdetail_opv0,findViewById(R.id.child_block5),"final_opv0","child_opv0");
        ChildVaccinecheck(childclient,childdetail_pcv1,findViewById(R.id.child_block10),"final_pcv1","child_pcv1");
        ChildVaccinecheck(childclient,childdetail_opv1,findViewById(R.id.child_block6),"final_opv1","child_opv1");
        ChildVaccinecheck(childclient,childdetail_penta1,findViewById(R.id.child_block2),"final_penta1","child_penta1");
        ChildVaccinecheck(childclient,childdetail_pcv2,findViewById(R.id.child_block11),"final_pcv2","child_pcv2");
        ChildVaccinecheck(childclient,childdetail_opv2,findViewById(R.id.child_block7),"final_opv2","child_opv2");
        ChildVaccinecheck(childclient,childdetail_penta2,findViewById(R.id.child_block3),"final_penta2","child_penta2");
        ChildVaccinecheck(childclient,childdetail_pcv3,findViewById(R.id.child_block12),"final_pcv3","child_pcv3");
        ChildVaccinecheck(childclient,childdetail_opv3,findViewById(R.id.child_block8),"final_opv3","child_opv3");
        ChildVaccinecheck(childclient,childdetail_penta3,findViewById(R.id.child_block4),"final_penta3","child_penta3");
        ChildVaccinecheck(childclient,childdetail_ipv,findViewById(R.id.child_block9),"final_ipv","child_ipv");
        ChildVaccinecheck(childclient,childdetail_measles1,findViewById(R.id.child_block13),"final_measles1","child_measles1");
        ChildVaccinecheck(childclient,childdetail_measles2,findViewById(R.id.child_block14),"final_measles2","child_measles2");

        if(tag.getVaccine().display().equalsIgnoreCase("BCG")) {
            update.remove("final_bcg");
            update.remove("final_bcg");
            update.remove("bcg");
            undo_bcg.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 1")) {
            update.remove("final_penta1");
            update.remove("penta1_retro");
            update.remove("penta1_dose_today");
            undo_penta1.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 2")) {
            update.remove("final_penta2");
            update.remove("penta2_retro");
            update.remove("penta2_dose_today");
            undo_penta2.setVisibility(View.INVISIBLE);

        }
        if(tag.getVaccine().display().equalsIgnoreCase("Penta 3")) {
            update.remove("final_penta3");
            update.remove("penta3_retro");
            update.remove("penta3_dose_today");
            undo_penta3.setVisibility(View.INVISIBLE);

        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 0")) {
            update.remove("final_opv0");
            update.remove("opv0_retro");
            update.remove("opv0_dose_today");
            undo_opv0.setVisibility(View.INVISIBLE);

        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 1")) {
            update.remove("final_opv1");
            update.remove("opv1_retro");
            update.remove("opv1_dose_today");
            undo_opv1.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 2")) {
            update.remove("final_opv2");
            update.remove("opv2_retro");
            update.remove("opv2_dose_today");
            undo_opv2.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("OPV 3")) {
            update.remove("final_opv3");
            update.remove("opv3_retro");
            update.remove("opv3_dose_today");
            undo_opv3.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("IPV")) {
            update.remove("final_ipv");
            update.remove("ipv_retro");
            update.remove("ipv_dose_today");
            undo_ipv.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 1")) {
            update.remove("final_pcv1");
            update.remove("pcv1_retro");
            update.remove("pcv1_dose_today");
            undo_pcv1.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 2")) {
            update.remove("final_pcv2");
            update.remove("pcv2_retro");
            update.remove("pcv2_dose_today");
            undo_pcv2.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("PCV 3")) {
            update.remove("final_pcv3");
            update.remove("pcv3_retro");
            update.remove("pcv3_dose_today");
            undo_pcv3.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Measles 1")) {
            update.remove("final_measles1");
            update.remove("measles1_retro");
            update.remove("measles1_dose_today");
            undo_measles1.setVisibility(View.INVISIBLE);
        }
        if(tag.getVaccine().display().equalsIgnoreCase("Measles 2")) {
            update.remove("final_measles2");
            update.remove("measles2_retro");
            update.remove("measles2_dose_today");
            undo_measles2.setVisibility(View.INVISIBLE);
        }
    }




    private void vaccine_complete_from_pop_up(TextView tt1TextView, View viewById, String updatedVaccineDateAsString) {
        viewById.setBackgroundColor(getResources().getColor(R.color.alert_complete_green));
        tt1TextView.setText(updatedVaccineDateAsString);
        tt1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    class childVaccineDetailAdapter extends BaseAdapter {
       vaccineInfo [] vaccineInfos;
       android.content.Context context;
       public childVaccineDetailAdapter(android.content.Context context,vaccineInfo [] childvaccineInfo){
           this.vaccineInfos = childvaccineInfo;
           this.context = context;
       }

       @Override
       public int getCount() {
           return vaccineInfos.length;
       }

       @Override
       public Object getItem(int position) {
           return null;
       }

       @Override
       public long getItemId(int position) {
           return 0;
       }

       @Override
       public View getView(int position, View convertView, ViewGroup parent) {

           LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
           convertView = inflater.inflate(R.layout.vaccine_row,null);
           TextView vaccinename = (TextView)convertView.findViewById(R.id.vaccinename);
           TextView vaccinedate = (TextView)convertView.findViewById(R.id.vaccine_date);
           Button vaccinestate = (Button) convertView.findViewById(R.id.vacc_state);
           vaccinename.setText(vaccineInfos[position].name);
           vaccinedate.setText(vaccineInfos[position].date);
           if(vaccineInfos[position].state.equalsIgnoreCase("upcoming")){
               vaccinestate.setBackgroundColor(getResources().getColor(R.color.alert_complete_green));
           }
           if(vaccineInfos[position].state.equalsIgnoreCase("urgent")){
               vaccinestate.setBackgroundColor(getResources().getColor(R.color.alert_urgent_red));
           }
           if(vaccineInfos[position].state.equalsIgnoreCase("expired")){
               vaccinestate.setBackgroundColor(getResources().getColor(R.color.client_list_header_dark_grey));
           }
           if(vaccineInfos[position].state.equalsIgnoreCase("not yet due")){
               vaccinestate.setBackgroundColor(getResources().getColor(R.color.alert_upcoming_yellow));
           }

           return convertView;
       }


   }
    private void formwrapperForChild(HashMap<String,String> vaccinemap) {

        JSONObject overridejsonobject = new JSONObject();
        try {
            overridejsonobject.put("child_age",((childclient.getDetails().get("child_age")!=null?childclient.getDetails().get("child_age"):"")));
            overridejsonobject.put("child_age_days",((childclient.getDetails().get("child_age_days")!=null?childclient.getDetails().get("child_age_days"):"")));
            overridejsonobject.put("opv0_dose",((childclient.getDetails().get("opv0_dose")!=null?childclient.getDetails().get("opv0_dose"):"")));
            overridejsonobject.put("pcv1_dose",((childclient.getDetails().get("pcv1_dose")!=null?childclient.getDetails().get("pcv1_dose"):"")));
            overridejsonobject.put("opv1_dose",((childclient.getDetails().get("opv1_dose")!=null?childclient.getDetails().get("opv1_dose"):"")));
            overridejsonobject.put("penta1_dose",((childclient.getDetails().get("penta1_dose")!=null?childclient.getDetails().get("penta1_dose"):"")));
            overridejsonobject.put("pcv2_dose",((childclient.getDetails().get("pcv2_dose")!=null?childclient.getDetails().get("pcv2_dose"):"")));
            overridejsonobject.put("opv2_dose",((childclient.getDetails().get("opv2_dose")!=null?childclient.getDetails().get("opv2_dose"):"")));
            overridejsonobject.put("penta2_dose",((childclient.getDetails().get("penta2_dose")!=null?childclient.getDetails().get("penta2_dose"):"")));
            overridejsonobject.put("pcv3_dose",((childclient.getDetails().get("pcv3_dose")!=null?childclient.getDetails().get("pcv3_dose"):"")));
            overridejsonobject.put("opv3_dose",((childclient.getDetails().get("opv3_dose")!=null?childclient.getDetails().get("opv3_dose"):"")));
            overridejsonobject.put("penta3_dose",((childclient.getDetails().get("penta3_dose")!=null?childclient.getDetails().get("penta3_dose"):"")));
            overridejsonobject.put("measles1_dose",((childclient.getDetails().get("measles1_dose")!=null?childclient.getDetails().get("measles1_dose"):"")));
            overridejsonobject.put("measles2_dose",((childclient.getDetails().get("measles2_dose")!=null?childclient.getDetails().get("measles2_dose"):"")));

            overridejsonobject.put("opv0",((childclient.getDetails().get("opv0")!=null?childclient.getDetails().get("opv0"):"")));
            overridejsonobject.put("pcv1",((childclient.getDetails().get("pcv1")!=null?childclient.getDetails().get("pcv1"):"")));
            overridejsonobject.put("opv1",((childclient.getDetails().get("opv1")!=null?childclient.getDetails().get("opv1"):"")));
            overridejsonobject.put("penta1",((childclient.getDetails().get("penta1")!=null?childclient.getDetails().get("penta1"):"")));
            overridejsonobject.put("pcv2",((childclient.getDetails().get("pcv2")!=null?childclient.getDetails().get("pcv2"):"")));
            overridejsonobject.put("opv2",((childclient.getDetails().get("opv2")!=null?childclient.getDetails().get("opv2"):"")));
            overridejsonobject.put("penta2",((childclient.getDetails().get("penta2")!=null?childclient.getDetails().get("penta2"):"")));
            overridejsonobject.put("pcv3",((childclient.getDetails().get("pcv3")!=null?childclient.getDetails().get("pcv3"):"")));
            overridejsonobject.put("opv3",((childclient.getDetails().get("opv3")!=null?childclient.getDetails().get("opv3"):"")));
            overridejsonobject.put("penta3",((childclient.getDetails().get("penta3")!=null?childclient.getDetails().get("penta3"):"")));
            overridejsonobject.put("measles1",((childclient.getDetails().get("measles1")!=null?childclient.getDetails().get("measles1"):"")));
            overridejsonobject.put("measles2",((childclient.getDetails().get("measles2")!=null?childclient.getDetails().get("measles2"):"")));

            overridejsonobject.put("e_bcg",((childclient.getDetails().get("final_bcg")!=null?childclient.getDetails().get("final_bcg"):"")));
            overridejsonobject.put("e_opv0",((childclient.getDetails().get("final_opv0")!=null?childclient.getDetails().get("final_opv0"):"")));
            overridejsonobject.put("e_penta2",((childclient.getDetails().get("final_penta2")!=null?childclient.getDetails().get("final_penta2"):"")));
            overridejsonobject.put("e_penta1",((childclient.getDetails().get("final_penta1")!=null?childclient.getDetails().get("final_penta1"):"")));
            overridejsonobject.put("e_penta3",((childclient.getDetails().get("final_penta3")!=null?childclient.getDetails().get("final_penta3"):"")));
            overridejsonobject.put("e_opv1",((childclient.getDetails().get("final_opv1")!=null?childclient.getDetails().get("final_opv1"):"")));
            overridejsonobject.put("e_opv2",((childclient.getDetails().get("final_opv2")!=null?childclient.getDetails().get("final_opv2"):"")));
            overridejsonobject.put("e_opv3",((childclient.getDetails().get("final_opv3")!=null?childclient.getDetails().get("final_opv3"):"")));
            overridejsonobject.put("e_pcv1",((childclient.getDetails().get("final_pcv1")!=null?childclient.getDetails().get("final_pcv1"):"")));
            overridejsonobject.put("e_pcv2",((childclient.getDetails().get("final_pcv2")!=null?childclient.getDetails().get("final_pcv2"):"")));
            overridejsonobject.put("e_pcv3",((childclient.getDetails().get("final_pcv3")!=null?childclient.getDetails().get("final_pcv3"):"")));
            overridejsonobject.put("e_ipv",((childclient.getDetails().get("final_ipv")!=null?childclient.getDetails().get("final_ipv"):"")));
            overridejsonobject.put("e_measles1",((childclient.getDetails().get("final_measles1")!=null?childclient.getDetails().get("final_measles1"):"")));
            overridejsonobject.put("e_measles2",((childclient.getDetails().get("final_measles2")!=null?childclient.getDetails().get("final_measles2"):"")));

            if(vaccinemap.get("final_bcg")==null){
                overridejsonobject.put("final_bcg",((childclient.getDetails().get("final_bcg")!=null?childclient.getDetails().get("final_bcg"):"")));
            }
            if(vaccinemap.get("final_opv0")==null){
                overridejsonobject.put("final_opv0",((childclient.getDetails().get("final_opv0")!=null?childclient.getDetails().get("final_opv0"):"")));
            }
            if(vaccinemap.get("final_penta2")==null){
                overridejsonobject.put("final_penta2",((childclient.getDetails().get("final_penta2")!=null?childclient.getDetails().get("final_penta2"):"")));
            }
            if(vaccinemap.get("final_penta1")==null){
                overridejsonobject.put("final_penta1",((childclient.getDetails().get("final_penta1")!=null?childclient.getDetails().get("final_penta1"):"")));
            }
            if(vaccinemap.get("final_penta3")==null){
                overridejsonobject.put("final_penta3",((childclient.getDetails().get("final_penta3")!=null?childclient.getDetails().get("final_penta3"):"")));
            }
            if(vaccinemap.get("final_opv1")==null){
                overridejsonobject.put("final_opv1",((childclient.getDetails().get("final_opv1")!=null?childclient.getDetails().get("final_opv1"):"")));
            }
            if(vaccinemap.get("final_opv2")==null){
                overridejsonobject.put("final_opv2",((childclient.getDetails().get("final_opv2")!=null?childclient.getDetails().get("final_opv2"):"")));
            }
            if(vaccinemap.get("final_opv3")==null){
                overridejsonobject.put("final_opv3",((childclient.getDetails().get("final_opv3")!=null?childclient.getDetails().get("final_opv3"):"")));
            }
            if(vaccinemap.get("final_pcv1")==null){
                overridejsonobject.put("final_pcv1",((childclient.getDetails().get("final_pcv1")!=null?childclient.getDetails().get("final_pcv1"):"")));
            }
            if(vaccinemap.get("final_pcv2")==null){
                overridejsonobject.put("final_pcv2",((childclient.getDetails().get("final_pcv2")!=null?childclient.getDetails().get("final_pcv2"):"")));
            }
            if(vaccinemap.get("final_pcv3")==null){
                overridejsonobject.put("final_pcv3",((childclient.getDetails().get("final_pcv3")!=null?childclient.getDetails().get("final_pcv3"):"")));
            }
            if(vaccinemap.get("final_ipv")==null){
                overridejsonobject.put("final_ipv",((childclient.getDetails().get("final_ipv")!=null?childclient.getDetails().get("final_ipv"):"")));
            }
            if(vaccinemap.get("final_measles1")==null){
                overridejsonobject.put("final_measles1",((childclient.getDetails().get("final_measles1")!=null?childclient.getDetails().get("final_measles1"):"")));
            }
            if(vaccinemap.get("final_measles2")==null){
                overridejsonobject.put("final_measles2",((childclient.getDetails().get("final_measles2")!=null?childclient.getDetails().get("final_measles2"):"")));
            }


            for (HashMap.Entry<String, String> entry : vaccinemap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                overridejsonobject.put(key,value);                // ...
            }

            DateTime currentDateTime = new DateTime(new Date());
//            overridejsonobject.put("tt1_final",currentDateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

            overridejsonobject.put("start",currentDateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            overridejsonobject.put( "end", currentDateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            overridejsonobject.put("today", currentDateTime.toString("yyyy-MM-dd"));

        } catch (JSONException e) {
            e.printStackTrace();
//            updateJson(encounterJson, "end", currentDateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
//            updateJson(encounterJson, "today", currentDateTime.toString("yyyy-MM-dd"));

        }

        FieldOverrides fieldOverrides = new FieldOverrides(overridejsonobject.toString());

        String formMadeforprint = FormUtils.getInstance(this).generateXMLInputForFormWithEntityId(childclient.entityId(), "woman_tt_form", fieldOverrides.getJSONString());

        try {
            JSONObject formSubmission = XML.toJSONObject(formMadeforprint);
            JSONObject encounterJson = find(formSubmission, "Child_Vaccination_Followup");
//            Log.v("formMadeforprint",encounterJson.toString());

            DateTime currentDateTime = new DateTime(new Date());
//            updateJson(encounterJson, "start", currentDateTime.toString("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
            updateJson(encounterJson, "deviceid", "Error: could not determine deviceID");
            updateJson(encounterJson, "subscriberid", "no subscriberid property in enketo");
            updateJson(encounterJson, "simserial", "no simserial property in enketo");
            updateJson(encounterJson, "phonenumber", "no phonenumber property in enketo");
            Log.v("formMadeforencounter",encounterJson.getString("start"));

            String data = XML.toString(formSubmission);
            saveFormSubmission(this, data, childclient.entityId(), "child_vaccine_followup", retrieveFieldOverides(fieldOverrides.getJSONString()));
            Log.v("formMadeforsubmission",formSubmission.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.v("formMadeforprint",formMadeforprint);
    }
    public static void saveFormSubmission(android.content.Context appContext, final String formSubmission, String id, final String formName, JSONObject fieldOverrides) {
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(appContext);
            final FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());
            /////////////handler mechanisms///////////////////////////////////////////////////////////////////////////
            context.formSubmissionRouter().route(submission.instanceId());
//            FormSubmission formsubmission = context.formDataRepository().fetchFromSubmission(submission.instance());
//
//            FormSubmissionHandler handler = context.formSubmissionRouter().getHandlerMap().get(submission.formName());
//            if (handler == null) {
//                logWarn("Could not find a handler due to unknown form submission: " + formsubmission);
//            } else {
//                try {
//                    handler.handle(submission);
//                } catch (Exception e) {
//                    org.ei.opensrp.util.Log.logError(format("Handling {0} form submission with instance Id: {1} for entity: {2} failed with exception : {3}",
//                            submission.formName(), submission.instanceId(), submission.entityId(), ExceptionUtils.getStackTrace(e)));
//                    throw e;
//                }
//            }
//            FORM_SUBMITTED.notifyListeners(submission.instance());
            /////////////////////////////////////handler mechanisms////////////////////////////////////////////////////
            // Update Fts Tables
            CommonFtsObject commonFtsObject = context.commonFtsObject();
            if (commonFtsObject != null) {
                String[] ftsTables = commonFtsObject.getTables();
                for (String ftsTable : ftsTables) {
                    AllCommonsRepository allCommonsRepository = context.allCommonsRepositoryobjects(ftsTable);
                    boolean updated = allCommonsRepository.updateSearch(submission.entityId());
                    if (updated) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
//            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }
    }
    private static String getParams(FormSubmission submission) {
        return new Gson().toJson(
                create(INSTANCE_ID_PARAM, submission.instanceId())
                        .put(ENTITY_ID_PARAM, submission.entityId())
                        .put(FORM_NAME_PARAM, submission.formName())
                        .put(VERSION_PARAM, submission.version())
                        .put(SYNC_STATUS, PENDING.value())
                        .map());
    }
    public static JSONObject retrieveFieldOverides(String overrides) {
        try {
            //get the field overrides map
            if (overrides != null) {
                JSONObject json = new JSONObject(overrides);
                String overridesStr = json.getString("fieldOverrides");
                return new JSONObject(overridesStr);
            }
        } catch (Exception e) {
//            Log.e(VaccinateActionUtils.class.getName(), "", e);
        }
        return null;
    }
    public static void updateJson(JSONObject jsonObject, String field, String value) {
        try {
            if (jsonObject.has(field)) {
                JSONObject fieldJson = jsonObject.getJSONObject(field);
                fieldJson.put("content", value);
            }
        } catch (JSONException e) {
        }
    }
    public static void updateJsonObject(JSONObject jsonObject, String field, JSONObject value) {
        try {
            if (jsonObject.has(field)) {
                JSONObject fieldJson = jsonObject.getJSONObject(field);

                fieldJson.put(field, value);
            }
        } catch (JSONException e) {
        }
    }
    public static JSONObject find(JSONObject jsonObject, String field) {
        try {
            if (jsonObject.has(field)) {
                return jsonObject.getJSONObject(field);

            }
        } catch (JSONException e) {
        }

        return null;
    }
    @Override
    public void onBackPressed() {
        if(update.size()>0) {
            formwrapperForChild(update);
        }
        super.onBackPressed();
    }
    private void make_undo_visible(final VaccineWrapper tag,Button tt1_undo) {
        tt1_undo.setVisibility(View.VISIBLE);
        tt1_undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.Context context = ChildDetailActivity.this;
                FragmentTransaction ft = ((Activity) context).getFragmentManager().beginTransaction();
                Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag(VaccinationDialogFragment.DIALOG_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);
                UndoVaccinationDialogFragment undoVaccinationDialogFragment = UndoVaccinationDialogFragment.newInstance(context, tag);
                undoVaccinationDialogFragment.show(ft, VaccinationDialogFragment.DIALOG_TAG);
            }
        });
    }
    class vaccineInfo {
        String name;
        String state;
        String date;

        public vaccineInfo(String name, String state, String date) {
            this.name = name;
            this.state = state;
            this.date = date;
        }
    }
}
