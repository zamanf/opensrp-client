package org.ei.opensrp.view.controller;

import com.google.gson.Gson;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.service.ANMService;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.HomeContext;

import javax.inject.Inject;

public class ANMController {

    @Inject
    private ANMService anmService;
    private final Cache<String> cache;
    private final Cache<HomeContext> nativeCache;
    private static final String HOME_CONTEXT = "homeContext";
    private static final String NATIVE_HOME_CONTEXT = "nativeHomeContext";


    public ANMController(Cache<String> cache, Cache<HomeContext> homeContextCache) {
        OpenSRPApplication.getInstance().inject(this);
        this.cache = cache;
        this.nativeCache = homeContextCache;
    }

    public String get() {
        return cache.get(HOME_CONTEXT, new CacheableData<String>() {
            @Override
            public String fetch() {
                return new Gson().toJson(new HomeContext(anmService.fetchDetails()));
            }
        });
    }

    public HomeContext getHomeContext() {
        return nativeCache.get(NATIVE_HOME_CONTEXT, new CacheableData<HomeContext>() {
            @Override
            public HomeContext fetch() {
                return new HomeContext(anmService.fetchDetails());
            }
        });
    }
}
