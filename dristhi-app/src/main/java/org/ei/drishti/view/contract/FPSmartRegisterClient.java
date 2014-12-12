package org.ei.drishti.view.contract;


import org.ei.drishti.domain.FPMethod;

import java.util.Comparator;
import java.util.List;

public interface FPSmartRegisterClient extends SmartRegisterClient, ECSmartRegisterBaseClient{

    public String numberOfPregnancies();

    public String parity();

    public String numberOfLivingChildren();

    public String numberOfStillbirths();

    public String numberOfAbortions();

    public String familyPlanningMethodChangeDate();

    public String numberOfOCPDelivered();

    public String numberOfCondomsSupplied();

    public String numberOfCentchromanPillsDelivered();

    public String iudPerson();

    public String iudPlace();

    public FPMethod fpMethod();

    public String youngestChildAge();

    public String complicationDate();

    public String condomSideEffect();

    public String iudSidEffect();

    public String ocpSideEffect();

    public String sterilizationSideEffect();

    public String injectableSideEffect();

    public String otherSideEffect();

    public List<AlertDTO> alerts();

    public RefillFollowUps refillFollowUps();

    public String highPriorityReason();
}

