package org.ei.opensrp.household.household;

import android.os.Bundle;

import org.ei.opensrp.commonregistry.CommonPersonObject;


/**
 * Created by Safwan on 7/12/2016.
 */
public class BridgingActivity  {


    public static CommonPersonObject person;
    String register;
    Bundle extras;

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*extras = getIntent().getExtras();
        register = extras.getString("woman");
    }

    @Override
    protected SmartRegisterFragment makeBaseFragment() {
        if(register.equalsIgnoreCase("woman"))
            return new WomanSmartRegisterFragment(new FormController(this));
        else
            return new ChildSmartRegisterFragment(new FormController(this));
    }

    @Override
    protected String[] buildFormNameList() {
        extras = getIntent().getExtras();
        register = extras.getString("woman");
        String[] list = new String[1];
        if(register.equalsIgnoreCase("woman"))
            list[0] = "woman_followup";
        else
            list[0] = "child_followup";
        return list;
    }*/
}
