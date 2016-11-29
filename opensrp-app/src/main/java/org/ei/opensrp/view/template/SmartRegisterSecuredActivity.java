package org.ei.opensrp.view.template;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.R;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ZiggyService;
import org.ei.opensrp.util.FormUtils;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.dialog.DialogOptionModel;
import org.ei.opensrp.view.dialog.SmartRegisterDialogFragment;
import org.ei.opensrp.view.fragment.DisplayFormFragment;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.viewpager.OpenSRPViewPager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.PENDING;
import static org.ei.opensrp.util.EasyMap.create;

public abstract class SmartRegisterSecuredActivity extends SecuredActivity {
    public static final String DIALOG_TAG = "dialog";

    private BaseRegisterActivityPagerAdapter mPagerAdapter;
    private int currentPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        OpenSRPViewPager mPager = (OpenSRPViewPager) findViewById(R.id.view_pager);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().hide();

        String[] formNames = this.buildFormNameList();
        Fragment[] otherFragl = null;

        DetailFragment detailFrg = getDetailFragment();
        if (detailFrg != null){
            otherFragl = new Fragment[]{detailFrg};
        }
        // Instantiate a ViewPager and a PagerAdapter.
        mPagerAdapter = new BaseRegisterActivityPagerAdapter(mPager, getSupportFragmentManager(),
                formNames, makeBaseFragment(), otherFragl);
        mPagerAdapter.getViewPager().setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            currentPage = position;
            onPageChanged(position);
            }
        });

        mPagerAdapter.switchToBaseFragment();

        onCreateActivity(savedInstanceState);
    }

    protected abstract SecuredNativeSmartRegisterFragment makeBaseFragment();

    public SecuredNativeSmartRegisterFragment getBaseFragment(){
        return (SecuredNativeSmartRegisterFragment) mPagerAdapter.getBaseFragment();
    }

    public DetailFragment getDetailFragment(){
        return null;
    }

    public void onPageChanged(int page) {
        if(page == 0 || mPagerAdapter.isFormFragment(page)){
            setRequestedOrientation(page == 0 ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    protected abstract String[] buildFormNameList() ;

    protected void onCreateActivity(Bundle savedInstanceState) {

    }

    @Override
    protected void onCreation() { }

    public void showFragmentDialog(DialogOptionModel dialogOptionModel) {
        showFragmentDialog(dialogOptionModel, null);
    }

    public void showFragmentDialog(DialogOptionModel dialogOptionModel, Object tag) {
        if (dialogOptionModel.getDialogOptions().length <= 0) {
            return;
        }
//todo not tested
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        android.app.Fragment prev = getFragmentManager().findFragmentByTag(DIALOG_TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        SmartRegisterDialogFragment.newInstance(this, dialogOptionModel, tag).show(ft, DIALOG_TAG);
    }

    public void showDetailFragment(CommonPersonObjectClient client, boolean landscape){
        if (mPagerAdapter.otherFragmentsSize() == 0){
            throw new IllegalStateException("No detail fragment configured for current activity");
        }

        setRequestedOrientation(landscape?ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Fragment pf = mPagerAdapter.getRegisteredFragment(1);
        ((DetailFragment)pf).resetView(client);
        mPagerAdapter.showFragment(1);//todo assumption
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

    public void saveFormSubmission(final String formSubmission, final String id, final String formName, final JSONObject fieldOverrides) {
        Log.v("fieldoverride", fieldOverrides.toString());
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
                        public void onClick(DialogInterface dialog, int whichButton) {
                            int formIndex = mPagerAdapter.getIndexForFormName(formName);//todo
                            closeForm(submission, formIndex, fieldOverrides); // pass data to let fragment know which record to display
                        }
                    })
                .show();
        } catch (Exception e) {
            e.printStackTrace();

            new AlertDialog.Builder(this)
                .setMessage((getResources().getString(R.string.form_saved_failed_dialog_message))+" : "+e.getMessage())
                .setTitle(R.string.form_saved_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.ok_button_label,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DisplayFormFragment displayFormFragment = (DisplayFormFragment) mPagerAdapter.getRegisteredFragment(currentPage);
                            if (displayFormFragment != null) {
                                displayFormFragment.hideTranslucentProgressDialog();
                            }
                        }
                    }).show();
        }
    }

    @Override
    public void startFormActivity(String formName, String entityId, String metaData) {
        Log.v(getClass().getName(), "Going to launch DisplayFormFragment for "+formName+" for entity "+entityId);
        Log.v(" with fieldoverride", metaData);
        try {
            int formIndex = mPagerAdapter.getIndexForFormName(formName);
            DisplayFormFragment displayFormFragment = (DisplayFormFragment) mPagerAdapter.getRegisteredFragment(formIndex);
            displayFormFragment.showForm(formIndex, entityId, metaData, false);
            mPagerAdapter.showFragment(formIndex);//todo
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

    public void closeForm(final FormSubmission data, final int pageIndex, final JSONObject overrides) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //hack reset the form
               DisplayFormFragment displayFormFragment = (DisplayFormFragment) mPagerAdapter.getRegisteredFragment(pageIndex);
                if (displayFormFragment != null) {
                    displayFormFragment.hideTranslucentProgressDialog();
                }

                SecuredNativeSmartRegisterFragment registerFragment = (SecuredNativeSmartRegisterFragment) mPagerAdapter.getBaseFragment();
                if (registerFragment != null && data != null) {
                    String id = data.getFieldValue(postFormSubmissionRecordFilterField());
                    if (StringUtils.isBlank(id)) {
                        try {
                            id = overrides.getString(postFormSubmissionRecordFilterField());
                        } catch (JSONException e) {
                            id = "";
                            e.printStackTrace();
                        }
                    }
                    registerFragment.onFilterManual(id);
                } else {
                    registerFragment.onFilterManual("");//clean up search filter
                }
                mPagerAdapter.switchToBaseFragment();
            }
        });
    }

    public abstract String postFormSubmissionRecordFilterField();

    /*public android.support.v4.app.Fragment findFragmentByPosition(int position) {
        FragmentPagerAdapter fragmentPagerAdapter = mPagerAdapter;
        return getSupportFragmentManager().findFragmentByTag("android:switcher:" + mPager.getId() + ":" + fragmentPagerAdapter.getItemId(position));
    }*/

   /* public DisplayFormFragment getDisplayFormFragmentAtIndex(int index) {
        return  (DisplayFormFragment)findFragmentByPosition(index);
    }*/

    @Override
    public void onBackPressed() {
        if (mPagerAdapter.isFormFragment(currentPage)) {
            new AlertDialog.Builder(this)
                .setMessage(R.string.form_back_confirm_dialog_message)
                .setTitle(R.string.form_back_confirm_dialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.yes_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        closeForm(null, currentPage, null);
                    }
                })
                .setNegativeButton(R.string.no_button_label, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .show();
        } else if (currentPage == 0) {
            ProgressDialog.show(this, "Wait", "Going back to home...", true);
            super.onBackPressed(); // allow back key only if we are
        } else {
            mPagerAdapter.switchToBaseFragment();
        }
    }
}
