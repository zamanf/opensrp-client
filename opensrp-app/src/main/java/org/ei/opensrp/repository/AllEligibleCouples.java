package org.ei.opensrp.repository;

import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.repository.cloudant.AlertsModel;
import org.ei.opensrp.repository.cloudant.EligibleCouplesModel;
import org.ei.opensrp.repository.cloudant.TimelineEventsModel;

import java.util.List;
import java.util.Map;

public class AllEligibleCouples {
    private EligibleCoupleRepository eligibleCoupleRepository;
    private final TimelineEventRepository timelineEventRepository;
    private final AlertRepository alertRepository;

    EligibleCouplesModel mEligibleCouplesModel = org.ei.opensrp.Context.getInstance().eligibleCouplesModel();
    AlertsModel mAlertsModel = org.ei.opensrp.Context.getInstance().alertsModel();
    TimelineEventsModel mTimelineEventsModel = org.ei.opensrp.Context.getInstance().timelineEventsModel();

    public AllEligibleCouples(EligibleCoupleRepository eligibleCoupleRepository, AlertRepository alertRepository, TimelineEventRepository timelineEventRepository) {
        this.eligibleCoupleRepository = eligibleCoupleRepository;
        this.timelineEventRepository = timelineEventRepository;
        this.alertRepository = alertRepository;
    }

    public List<EligibleCouple> all() {
        //return eligibleCoupleRepository.allEligibleCouples();
        return mEligibleCouplesModel.allEligibleCouples();
    }

    public EligibleCouple findByCaseID(String caseId) {
        //return eligibleCoupleRepository.findByCaseID(caseId);
        return mEligibleCouplesModel.findByCaseID(caseId);
    }

    public long count() {
        //return eligibleCoupleRepository.count();
        return mEligibleCouplesModel.count();
    }

    public long fpCount() {
        return mEligibleCouplesModel.fpCount();
    }

    public List<String> villages() {
        return mEligibleCouplesModel.villages();
    }

    public List<EligibleCouple> findByCaseIDs(List<String> caseIds) {
        return mEligibleCouplesModel.findByCaseIDs(caseIds.toArray(new String[caseIds.size()]));
    }

    public void updatePhotoPath(String caseId, String imagePath) {
        mEligibleCouplesModel.updatePhotoPath(caseId, imagePath);
    }

    public void close(String entityId) {
        mAlertsModel.deleteAllAlertsForEntity(entityId);
        mTimelineEventsModel.deleteAllTimelineEventsForEntity(entityId);
        mEligibleCouplesModel.close(entityId);
    }

    public void mergeDetails(String entityId, Map<String, String> details) {
        mEligibleCouplesModel.mergeDetails(entityId, details);
    }
}
