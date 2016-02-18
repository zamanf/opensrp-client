package org.ei.opensrp.view.controller;

import com.google.gson.Gson;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.SettingsRepository;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.ANMLocation;

import javax.inject.Inject;
import javax.inject.Named;

public class ANMLocationController {
    private static final String ANM_LOCATION = "anmLocation";
    private static final String ANM_LOCATION_JSON = "anmLocationJSON";

    @Inject
    private SettingsRepository allSettings;

    @Inject
    @Named("ListCache")
    public Cache<String> cache;

    public ANMLocationController() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public String get() {
        return cache.get(ANM_LOCATION, new CacheableData<String>() {
            @Override
            public String fetch() {
                return allSettings.fetchANMLocation();
            }
        });
    }

    public String getLocationJSON() {
        return cache.get(ANM_LOCATION_JSON, new CacheableData<String>() {
            @Override
            public String fetch() {
                return new Gson().fromJson(allSettings.fetchANMLocation(), ANMLocation.class).asJSONString();
            }
        });
    }
}
