package org.ei.opensrp.service;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.BeneficiariesAdapter;
import org.ei.opensrp.db.adapters.EligibleCoupleRepository;
import org.ei.opensrp.db.adapters.SharedPreferencesAdapter;
import org.ei.opensrp.domain.ANM;

import javax.inject.Inject;

public class ANMService {

    @Inject
    private SharedPreferencesAdapter allSharedPreferences;

    @Inject
    private BeneficiariesAdapter allBeneficiaries;

    @Inject
    private EligibleCoupleRepository allEligibleCouples;

    public ANMService() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public ANM fetchDetails() {
        return new ANM(allSharedPreferences.fetchRegisteredANM(), allEligibleCouples.count(), allEligibleCouples.fpCount(),
                allBeneficiaries.ancCount(), allBeneficiaries.pncCount(), allBeneficiaries.childCount());
    }
}
