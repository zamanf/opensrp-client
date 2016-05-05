package org.ei.opensrp.vaccinator.household;

import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.repository.AllTimelineEvents;
import org.ei.opensrp.service.AlertService;

/**
 * Created by Safwan on 4/22/2016.
 */
public class HouseholdService {

    private AllTimelineEvents allTimelines;
    private AllCommonsRepository allCommonsRepository;
    private AlertService alertService;

    public HouseholdService(AllTimelineEvents allTimelines, AllCommonsRepository allCommonsRepository, AlertService alertService) {
        this.allTimelines = allTimelines;
        this.allCommonsRepository = allCommonsRepository;
        this.alertService = alertService;

    }
}
