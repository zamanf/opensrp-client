package org.ei.opensrp.modules;

import android.content.res.AssetManager;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.router.ActionRouter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(complete = false, injects = { OpenSRPApplication.class })
public class AppModule {

    @Provides
    @Singleton
    AssetManager provideAssetManager() {
        return OpenSRPApplication.getInstance().getAssets();
    }
	
}
