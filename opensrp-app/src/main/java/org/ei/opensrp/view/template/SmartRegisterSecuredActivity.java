package org.ei.opensrp.view.template;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.R;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.SmartRegisterDialogFragment;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONObject;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;

public abstract class SmartRegisterSecuredActivity extends SecuredActivity {
    public static final String DIALOG_TAG = "dialog";

    protected OpenSRPViewPager mPager;
    private BaseRegisterActivityPagerAdapter mPagerAdapter;
    private int currentPage;

    private String[] formNames = new String[]{};
    protected android.support.v4.app.Fragment mBaseFragment = null;

    //SAFWAN
    private SecuredFragment mProfileFragment = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        mPager = (OpenSRPViewPager) findViewById(R.id.view_pager);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().hide();
//        getWindow().getDecorView().setBackgroundDrawable(null);

        formNames = this.buildFormNameList();
        mBaseFragment = getBaseFragment();
        //SAFWAN
        mProfileFragment = getProfileFragment();

        // Instantiate a ViewPager and a PagerAdapter.
        //SAFWAN
        if(mProfileFragment == null)
            mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment);
         else
            mPagerAdapter = new BaseRegisterActivityPagerAdapter(getSupportFragmentManager(), formNames, mBaseFragment, mProfileFragment);

        mPager.setOffscreenPageLimit(formNames.length);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                onPageChanged(position);
            }
        });

        mPagerAdapter.switchToBaseFragment(mPager);
        SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
    }

    public abstract SecuredNativeSmartRegisterFragment getBaseFragment();

    public abstract SecuredFragment getProfileFragment();


    public void onPageChanged(int page) {
        setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected abstract String[] buildFormNameList() ;

    @Override
    protected void onCreation() {
    }


    public void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        showFragmentDialog(dialogOptionModel, null);
    }

    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        if (dialogOptionModel.getDialogOptions().length <= 0) {
            return;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        SmartRegisterDialogFragment
                .newInstance(this, dialogOptionModel, tag)
                .show(ft, DIALOG_TAG);
    }

    protected String getParams(FormSubmission submission) {
        return new Gson().toJson(
                create(INSTANCE_ID_PARAM, submission.instanceId())
                        .put(ENTITY_ID_PARAM, submission.entityId())
                        .put(FORM_NAME_PARAM, submission.formName())
                        .put(VERSION_PARAM, submission.version())
                        .put(SYNC_STATUS, PENDING.value())
                        .map());
    }

    public void saveFormSubmission(final String formSubmission, String id, final String formName, JSONObject fieldOverrides) {
        Log.v("fieldoverride", fieldOverrides.toString());
        // save the form
        try {
            FormUtils formUtils = FormUtils.getInstance(getApplicationContext());
            final FormSubmission submission = formUtils.generateFormSubmisionFromXMLString(id, formSubmission, formName, fieldOverrides);

            org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
            ZiggyService ziggyService = context.ziggyService();
            ziggyService.saveForm(getParams(submission), submission.instance());

            new AlertDialog.Builder(this)
                    .setMessage(R.string.form_saved_success_dialog_message)
                    .setTitle(R.string.form_saved_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok_button_label,
                        new DialogInterface.OnClickListener() {
                            int formIndex;
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(mProfileFragment != null){
                                    formIndex = FormUtils.getIndexForFormName(formName, formNames) + ((BaseRegisterActivityPagerAdapter) mPagerAdapter).offset;
                                } else {
                                    formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1;
                                }
                                switchToBaseFragment(submission, formIndex); // pass data to let fragment know which record to display
                            }
                        })
                    .show();
        } catch (Exception e) {
            new AlertDialog.Builder(this)
                    .setMessage((getResources().getString(R.string.form_saved_failed_dialog_message))+" : "+e.getMessage())
                    .setTitle(R.string.form_saved_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.ok_button_label,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(currentPage);
                                if (displayFormFragment != null) {
                                    displayFormFragment.hideTranslucentProgressDialog();
                                }
                            }
                        }).show();
            e.printStackTrace();
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.v("fieldoverride", metaData);
        try {
            //SAFWAN
            int formIndex;
            if(mProfileFragment != null) {
                // TODO remove ...
                if(formName.equals("new_member_registration_without_qr")){
                    formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1;
                } else
                    formIndex = FormUtils.getIndexForFormName(formName, formNames) + ((BaseRegisterActivityPagerAdapter) mPagerAdapter).offset;
            }
            else {
                formIndex = FormUtils.getIndexForFormName(formName, formNames) + 1;//((BaseRegisterActivityPagerAdapter) mPagerAdapter).offset; // add the offset
            }
            DisplayFormFragment displayFormFragment = getDisplayFormFragmentAtIndex(formIndex);
            displayFormFragment.showForm(mPager, formIndex, entityId, metaData, false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void savePartialFormData(String formData, String id, String formName, JSONObject fieldOverrides){
        try {
            //Save the current form data into shared preferences
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            String savedDataKey = formName + "savedPartialData";
            editor.putString(savedDataKey, formData);

            String overridesKey = formName + "overrides";
            editor.putString(overridesKey, fieldOverrides.toString());

            String idKey = formName + "id";
            if (id != null){
                editor.putString(idKey, id);
            }

            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getPreviouslySavedDataForForm(String formName, String overridesStr, String id){
        try {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            String savedDataKey = formName + "savedPartialData";
            String overridesKey = formName + "overrides";
            String idKey = formName + "id";

            JSONObject overrides = new JSONObject();

            if (overrides != null){
                JSONObject json = new JSONObject(overridesStr);
                String s = json.getString("fieldOverrides");
                overrides = new JSONObject(s);
            }

            boolean idIsConsistent = id == null && !sharedPref.contains(idKey) ||
                    id != null && sharedPref.contains(idKey) && sharedPref.getString(idKey, null).equals(id);

            if (sharedPref.contains(savedDataKey) && sharedPref.contains(overridesKey) && idIsConsistent){
                String savedDataStr = sharedPref.getString(savedDataKey, null);
                String savedOverridesStr = sharedPref.getString(overridesKey, null);


                // the previously saved data is only returned if the overrides and id are the same ones used previously
                if (savedOverridesStr.equals(overrides.toString())) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    //after retrieving the value delete it from shared pref.
                    editor.remove(savedDataKey);
                    editor.remove(overridesKey);
                    editor.remove(idKey);
                    editor.apply();
                    return savedDataStr;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void switchToBaseFragment(final FormSubmission data, final int pageIndex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //hack reset the form
                DisplayFormFragment displayFormFragment;
                if(mProfileFragment == null)
                    displayFormFragment = getDisplayFormFragmentAtIndex(pageIndex);
                else
                    displayFormFragment = getDisplayFormFragmentAtIndex(2);

                    if (displayFormFragment != null) {
                        displayFormFragment.hideTranslucentProgressDialog();
                        displayFormFragment.setFormData(null);
                        displayFormFragment.setRecordId(null);
                        displayFormFragment.setFieldOverides(null);
                    }

                mPager.setCurrentItem(0, false);
                SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) findFragmentByPosition(0);
                if (registerFragment != null && data != null) {
                    String id;
                    if(mProfileFragment != null){
                        id = data.getFieldValue("household_id");
                        if (StringUtils.isBlank(id)){
                            id = data.getFieldValue("existing_household_id");
                        }
                    } else {
                        id = data.getFieldValue("program_client_id");
                        if (StringUtils.isBlank(id)){
                            id = data.getFieldValue("existing_program_client_id");
                        }
                    }
                    registerFragment.getSearchView().setText(id);
                    registerFragment.onFilterManual(id);
                }
                else registerFragment.onFilterManual("");//clean up search filter
            }
        });
    }

    public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        long i = fragmentPagerAdapter.getItemId(position);
        long j = mPager.getId();
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + j + ":" + i);
    }

    public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }

    @Override
    public void onBackPressed() {
        if (currentPage == 0) {
            super.onBackPressed(); // allow back key only if we are
        }
        else if(mProfileFragment != null){
            //mPager.setCurrentItem(0, false);
            switchToBaseFragment(null, 0);
        }
        else  {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.form_back_confirm_dialog_message)
                    .setTitle(R.string.form_back_confirm_dialog_title)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    switchToBaseFragment(null, currentPage);
                                }
                            })
                    .setNegativeButton(R.string.no_button_label,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                }
                            })
                    .show();
        }
    }
}
