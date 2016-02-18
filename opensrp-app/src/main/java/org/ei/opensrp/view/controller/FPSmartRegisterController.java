package org.ei.opensrp.view.controller;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.BeneficiariesAdapter;
import org.ei.opensrp.db.adapters.EligibleCoupleRepository;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.CacheableData;
import org.ei.opensrp.view.contract.AlertDTO;
import org.ei.opensrp.view.contract.FPClient;
import org.ei.opensrp.view.contract.FPClients;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.ei.opensrp.AllConstants.DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH;

public class FPSmartRegisterController {
    public static final String OCP_REFILL_ALERT_NAME = "OCP Refill";
    public static final String CONDOM_REFILL_ALERT_NAME = "Condom Refill";
    public static final String DMPA_INJECTABLE_REFILL_ALERT_NAME = "DMPA Injectable Refill";
    public static final String FEMALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME = "Female sterilization Followup 1";
    public static final String FEMALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME = "Female sterilization Followup 2";
    public static final String FEMALE_STERILIZATION_FOLLOWUP_3_ALERT_NAME = "Female sterilization Followup 3";
    public static final String MALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME = "Male sterilization Followup 1";
    public static final String MALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME = "Male sterilization Followup 2";
    public static final String IUD_FOLLOWUP_1_ALERT_NAME = "IUD Followup 1";
    public static final String IUD_FOLLOWUP_2_ALERT_NAME = "IUD Followup 2";
    public static final String FP_FOLLOWUP_ALERT_NAME = "FP Followup";
    public static final String FP_REFERRAL_FOLLOWUP_ALERT_NAME = "FP Referral Followup";

    private final static String FP_CLIENTS_LIST = "FPClientsList";

    @Inject
    private EligibleCoupleRepository allEligibleCouples;

    @Inject
    private BeneficiariesAdapter allBeneficiaries;

    @Inject
    private AlertService alertService;

    @Inject
    @Named("ListCache")
    public Cache<String> cache;

    @Inject
    @Named("FPClientsCache")
    public Cache<FPClients> fpClientsCache;


    public FPSmartRegisterController() {
        OpenSRPApplication.getInstance().inject(this);
    }

    private List<AlertDTO> getFPAlertsForEC(String entityId) {
        List<Alert> alerts = alertService.findByEntityIdAndAlertNames(entityId,
                OCP_REFILL_ALERT_NAME,
                CONDOM_REFILL_ALERT_NAME, DMPA_INJECTABLE_REFILL_ALERT_NAME,
                FEMALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME,
                FEMALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME,
                FEMALE_STERILIZATION_FOLLOWUP_3_ALERT_NAME,
                MALE_STERILIZATION_FOLLOWUP_1_ALERT_NAME,
                MALE_STERILIZATION_FOLLOWUP_2_ALERT_NAME,
                IUD_FOLLOWUP_1_ALERT_NAME,
                IUD_FOLLOWUP_2_ALERT_NAME,
                FP_FOLLOWUP_ALERT_NAME,
                FP_REFERRAL_FOLLOWUP_ALERT_NAME);
        ArrayList<AlertDTO> alertDTOs = new ArrayList<AlertDTO>();
        for (Alert alert : alerts) {
            alertDTOs.add(new AlertDTO(alert.visitCode(), String.valueOf(alert.status()), alert.startDate()));
        }
        return alertDTOs;
    }

    public FPClients getClients() {
        return fpClientsCache.get(FP_CLIENTS_LIST, new CacheableData<FPClients>() {
            @Override
            public FPClients fetch() {
                List<EligibleCouple> ecs = allEligibleCouples.all();
                FPClients fpClients = new FPClients();
                for (EligibleCouple ec : ecs) {
                    if (allBeneficiaries.isPregnant(ec.caseId())) {
                        continue;
                    }

                    String photoPath = isBlank(ec.photoPath()) ? DEFAULT_WOMAN_IMAGE_PLACEHOLDER_PATH : ec.photoPath();
                    List<AlertDTO> alerts = getFPAlertsForEC(ec.caseId());
                    FPClient fpClient = new FPClient(ec.caseId(), ec.wifeName(), ec.husbandName(), ec.village(), ec.ecNumber())
                            .withAge(ec.age())
                            .withFPMethod(ec.getDetail("currentMethod"))
                            .withFamilyPlanningMethodChangeDate(ec.getDetail("familyPlanningMethodChangeDate"))
                            .withComplicationDate(ec.getDetail("complicationDate"))
                            .withIUDPlace(ec.getDetail("iudPlace"))
                            .withIUDPerson(ec.getDetail("iudPerson"))
                            .withNumberOfCondomsSupplied(ec.getDetail("numberOfCondomsSupplied"))
                            .withNumberOfCentchromanPillsDelivered(ec.getDetail("numberOfCentchromanPillsDelivered"))
                            .withNumberOfOCPDelivered(ec.getDetail("numberOfOCPDelivered"))
                            .withFPMethodFollowupDate(ec.getDetail("fpFollowupDate"))
                            .withCaste(ec.getDetail("caste"))
                            .withEconomicStatus(ec.getDetail("economicStatus"))
                            .withNumberOfPregnancies(ec.getDetail("numberOfPregnancies"))
                            .withParity(ec.getDetail("parity"))
                            .withNumberOfLivingChildren(ec.getDetail("numberOfLivingChildren"))
                            .withNumberOfStillBirths(ec.getDetail("numberOfStillBirths"))
                            .withNumberOfAbortions(ec.getDetail("numberOfAbortions"))
                            .withIsYoungestChildUnderTwo(ec.isYoungestChildUnderTwo())
                            .withYoungestChildAge(ec.getDetail("youngestChildAge"))
                            .withIsHighPriority(ec.isHighPriority())
                            .withPhotoPath(photoPath)
                            .withAlerts(alerts)
                            .withCondomSideEffect(ec.getDetail("condomSideEffect"))
                            .withIUDSidEffect(ec.getDetail("iudSidEffect"))
                            .withOCPSideEffect(ec.getDetail("ocpSideEffect"))
                            .withInjectableSideEffect(ec.getDetail("injectableSideEffect"))
                            .withSterilizationSideEffect(ec.getDetail("sterilizationSideEffect"))
                            .withOtherSideEffect(ec.getDetail("otherSideEffect"))
                            .withHighPriorityReason(ec.getDetail("highPriorityReason"))
                            .preprocess();
                    fpClients.add(fpClient);
                }
                return fpClients;
            }
        });
    }
}