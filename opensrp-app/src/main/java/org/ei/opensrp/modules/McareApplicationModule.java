package org.ei.opensrp.modules;

import android.content.Context;

import org.ei.opensrp.application.OpenSRPApplication;

import dagger.Module;

/**
 * Created by koros on 2/12/16.
 */

@Module(complete = false, includes = { BaseModule.class, AppModule.class, ActivityFragmentModule.class, AdapterModule.class, ServiceModule.class })
public class McareApplicationModule {

    private Context applicationContext;
    private OpenSRPApplication openSRPApplication;

    public McareApplicationModule(OpenSRPApplication openSRPApplication) {
        this.openSRPApplication = openSRPApplication;
        this.applicationContext = this.openSRPApplication.getApplicationContext();
    }


}
