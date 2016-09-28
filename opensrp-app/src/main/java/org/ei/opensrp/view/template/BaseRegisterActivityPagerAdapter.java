package org.ei.opensrp.view.template;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;

import org.ei.opensrp.view.fragment.DisplayFormFragment;
/**
 * Created by muhammad.ahmed@ihsinformatics.com on 05-Jan-16.
 */
public class BaseRegisterActivityPagerAdapter extends SmartFragmentStatePagerAdapter {
    public static final String ARG_PAGE = "page";
    String[] dialogOptions;
    Fragment mBaseFragment;
    Fragment[] otherFragments;
    FragmentManager fragmentManager;
    ViewPager pager;

    public BaseRegisterActivityPagerAdapter(ViewPager pager, FragmentManager fragmentManager, String[] dialogOptions, Fragment baseFragment) {
        this(pager, fragmentManager, dialogOptions, baseFragment, null);
    }

    public BaseRegisterActivityPagerAdapter(ViewPager pager, FragmentManager fragmentManager, String[] dialogOptions,
            Fragment baseFragment, Fragment[] otherFragments) {
        super(fragmentManager);
        this.pager = pager;
        this.otherFragments = otherFragments;
        this.fragmentManager = fragmentManager;
        this.dialogOptions = dialogOptions;
        this.mBaseFragment = baseFragment;

       // this.pager.setOffscreenPageLimit(dialogOptions.length); //todo
        this.pager.setAdapter(this);
        this.pager.setOffscreenPageLimit(getCount());
    }

    public ViewPager getViewPager(){
        return pager;
    }

    @Override
    public Fragment getItem(int position) {
        Log.v(getClass().getName(), "Getting fragment at "+position);
        Fragment fragment = null;
        if (position == 0){
            fragment = getBaseFragment();
        }
        // if has other fragments and position lessthan of.size+basefragment
        else if (otherFragmentsSize() > 0 && position <= otherFragmentsSize()){ // base fragment counted
            fragment = otherFragments[position - 1];// account for the base fragment
        }
        else {
            String formName = dialogOptions[position - (otherFragmentsSize()+1)]; // account for the base fragment and other fragments
            DisplayFormFragment f = new DisplayFormFragment();
            f.setFormName(formName);
            fragment = f;
        }

        Log.v(getClass().getName(), "Got fragment "+fragment.toString());

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, position);
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isFormFragment(int position){
        if (position == 0){
            return false;
        }
        else if (position <= otherFragmentsSize()){//size is index+1 so baseFragment handled
            return false;
        }
        else {
            return true;
        }
    }

/*  todo  public android.support.v4.app.Fragment findFragmentByPosition(ViewGroup view, int position) {
        return fragmentManager.findFragmentByTag("android:switcher:" + view.getId() + ":" + getItemId(position));
    }*/

    public boolean isBaseFragment(int position){
        if (position == 0){
            return true;
        }
        return false;
    }

    public int otherFragmentsSize(){
        return otherFragments == null ? 0 : otherFragments.length;
    }

    public int formFragmentsSize(){
        return dialogOptions == null ? 0 : dialogOptions.length;
    }

    public int getIndexForFormName(String formName){
        for (int i = 0; i < dialogOptions.length; i++){
            if (formName.equalsIgnoreCase(dialogOptions[i])){
                return i+1+(otherFragmentsSize());//plus base fragment and other fragments
            }
        }
        return -1;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Page # "+position;
    }

    @Override
    public int getCount() {
        int count = otherFragmentsSize() + formFragmentsSize() + 1; // index 0 is always occupied by the base fragment
        return count;
    }

    public Fragment getBaseFragment(){
        return mBaseFragment;
    }

    public void switchToBaseFragment(){
        pager.setCurrentItem(0, false);
    }

    public void showFragment(int position){
        Log.i(getClass().getName(), "Show fragment at "+position);
        pager.setCurrentItem(position, false);
    }
}
