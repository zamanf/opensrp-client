package org.ei.opensrp.vaccinator.household;

import android.os.Bundle;

import org.ei.opensrp.commonregistry.CommonPersonObject;

import org.ei.opensrp.vaccinator.application.template.SmartRegisterFragment;
import org.ei.opensrp.vaccinator.child.ChildSmartRegisterFragment;
import org.ei.opensrp.vaccinator.woman.WomanSmartRegisterFragment;
import org.ei.opensrp.view.controller.FormController;


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
    protected SmartRegisterFragment getBaseFragment() {
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
