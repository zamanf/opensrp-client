package org.ei.opensrp.view.controller;

import com.google.gson.Gson;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.EligibleCoupleRepository;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.Village;
import org.ei.opensrp.view.contract.Villages;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

public class VillageController {
    private static final String VILLAGE_LIST = "VILLAGE_LIST";

    @Inject
    private EligibleCoupleRepository allEligibleCouples;

    @Inject
    @Named("VillagesCache")
    private Cache<Villages> villagesCache;

    @Inject
    @Named("ListCache")
    public Cache<String> cache;

    public VillageController() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public String villages() {
        return cache.get(VILLAGE_LIST, new CacheableData<String>() {
            @Override
            public String fetch() {
                List<Village> villagesList = new ArrayList<Village>();
                List<String> villages = allEligibleCouples.villages();
                for (String village : villages) {
                    villagesList.add(new Village(village));
                }
                return new Gson().toJson(villagesList);
            }
        });
    }

    public Villages getVillages() {
        return villagesCache.get(VILLAGE_LIST, new CacheableData<Villages>() {
            @Override
            public Villages fetch() {
                Villages villagesList = new Villages();
                List<String> villages = allEligibleCouples.villages();
                for (String village : villages) {
                    villagesList.add(new Village(village));
                }
                return villagesList;
            }
        });
    }
}
