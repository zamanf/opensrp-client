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
    //Fragment mProfileFragment;
    public int offset = 0;


    public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        //offset += 1;
    }

    /*public BaseRegisterActivityPagerAdapter(FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment, Fragment mProfileFragment) {
        super(fragmentManager);
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;
        this.mProfileFragment = mProfileFragment;
        offset += 2;
    }*/


    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = mBaseFragment;
                break;

            default:
                String formName = dialogOptions[position - 1]; // account for the base fragment
                DisplayFormFragment f = new DisplayFormFragment();
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
        return dialogOptions.length + 1; // index 0 is always occupied by the base fragment
    }

    /*public int getOffset(){
        return offset;
    }*/
}
