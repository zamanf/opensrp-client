package org.ei.opensrp.modules;

import android.content.Context;
import android.content.res.AssetManager;

import org.ei.opensrp.application.OpenSRPApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

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

    @Provides
    @Singleton
    Context provideApplicationContext() {
        return applicationContext;
    }

}
