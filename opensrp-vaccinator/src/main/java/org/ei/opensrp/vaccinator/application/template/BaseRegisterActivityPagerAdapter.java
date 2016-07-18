package org.ei.opensrp.vaccinator.application.template;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.ei.opensrp.view.fragment.DisplayFormFragment;
/**
 * Created by muhammad.ahmed@ihsinformatics.com on 05-Jan-16.
 */
public class BaseRegisterActivityPagerAdapter extends FragmentPagerAdapter {
    public static final String ARG_PAGE = "page";
    String[] dialogOptions;
    Fragment mBaseFragment;
    Fragment mProfileFragment;
    public int offset = 0;


    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        //SAFWAN
        offset += 1;
    }
    //SAFWAN
    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment, Fragment mProfileFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        this.mProfileFragment = mProfileFragment;
        offset += 2;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        DisplayFormFragment f;
        String formName;
        switch (position) {
            case 0:
                fragment = mBaseFragment;
                break;

            case 1:
                if(mProfileFragment != null) {
                    fragment = mProfileFragment;
                    break;
                }

            case 6:
                formName = dialogOptions[position - offset]; // account for the base fragment
                f = new DisplayFormFragment();
                f.setFormName(formName);
                fragment = f;
                break;

            default:
                formName = dialogOptions[position - offset]; // account for the base fragment
                f = new DisplayFormFragment();
                f.setFormName(formName);
                fragment = f;
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return dialogOptions.length + offset; // index 0 is always occupied by the base fragment
    }

    public int getOffset(){
        return offset;
    }
}
