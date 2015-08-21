package org.ei.opensrp.repository;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.opensrp.domain.Child;
import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.domain.Mother;
import org.ei.opensrp.repository.cloudant.AlertsModel;
import org.ei.opensrp.repository.cloudant.ChildsModel;
import org.ei.opensrp.repository.cloudant.MothersModel;
import org.ei.opensrp.repository.cloudant.TimelineEventsModel;

import java.util.List;

import static org.ei.opensrp.repository.MotherRepository.TYPE_ANC;
import static org.ei.opensrp.repository.MotherRepository.TYPE_PNC;

public class AllBeneficiaries {
    private ChildRepository childRepository;
    private MotherRepository motherRepository;
    private final AlertRepository alertRepository;
    private final TimelineEventRepository timelineEventRepository;

    ChildsModel mChildsModel = org.ei.opensrp.Context.getInstance().childsModel();
    MothersModel mMothersModel = org.ei.opensrp.Context.getInstance().mothersModel();
    AlertsModel mAlertsModel = org.ei.opensrp.Context.getInstance().alertsModel();
    TimelineEventsModel mTimelineEventsModel = org.ei.opensrp.Context.getInstance().timelineEventsModel();

    public AllBeneficiaries(MotherRepository motherRepository, ChildRepository childRepository,
                            AlertRepository alertRepository, TimelineEventRepository timelineEventRepository) {
        this.childRepository = childRepository;
        this.motherRepository = motherRepository;
        this.alertRepository = alertRepository;
        this.timelineEventRepository = timelineEventRepository;
    }

    //#TODO
    public Mother findMotherWithOpenStatus(String caseId) {
        //return motherRepository.findOpenCaseByCaseID(caseId);
        return mMothersModel.findOpenCaseByCaseID(caseId);
    }

    public Mother findMother(String caseId) {
//        List<Mother> mothers = motherRepository.findByCaseIds(caseId);
        List<Mother> mothers = mMothersModel.findByCaseIds(caseId);
        if (mothers.isEmpty())
            return null;
        return mothers.get(0);
    }

    public Child findChild(String caseId) {
        //return childRepository.find(caseId);
        return mChildsModel.find(caseId);
    }

    public long ancCount() {
        //return motherRepository.ancCount();
        return mMothersModel.ancCount();
    }

    public long pncCount() {
        //return motherRepository.pncCount();
        return mMothersModel.pncCount();
    }

    public long childCount() {
        //return childRepository.count();
        return mChildsModel.count();
    }

    public List<Pair<Mother, EligibleCouple>> allANCsWithEC() {
        //return motherRepository.allMothersOfATypeWithEC(TYPE_ANC);
        return mMothersModel.allMothersOfATypeWithEC(TYPE_ANC);
    }

    public List<Pair<Mother, EligibleCouple>> allPNCsWithEC() {
        //return motherRepository.allMothersOfATypeWithEC(TYPE_PNC);
        return mMothersModel.allMothersOfATypeWithEC(TYPE_PNC);
    }

    public Mother findMotherByECCaseId(String ecCaseId) {
        //List<Mother> mothers = motherRepository.findAllCasesForEC(ecCaseId);
        List<Mother> mothers = mMothersModel.findAllCasesForEC(ecCaseId);
        if (mothers.isEmpty())
            return null;
        return mothers.get(0);
    }

    public List<Child> findAllChildrenByMotherId(String entityId) {
        //return childRepository.findByMotherCaseId(entityId);
        return mChildsModel.findByMotherCaseId(entityId);
    }

    public List<Child> findAllChildrenByCaseIDs(List<String> caseIds) {
        //return childRepository.findChildrenByCaseIds(caseIds.toArray(new String[caseIds.size()]));
        return mChildsModel.findChildrenByCaseIds(caseIds.toArray(new String[caseIds.size()]));
    }

    public List<Mother> findAllMothersByCaseIDs(List<String> caseIds) {
        //return motherRepository.findByCaseIds(caseIds.toArray(new String[caseIds.size()]));
        return mMothersModel.findByCaseIds(caseIds.toArray(new String[caseIds.size()]));
    }

    public void switchMotherToPNC(String entityId) {
        //motherRepository.switchToPNC(entityId);
        mMothersModel.switchToPNC(entityId);
    }

    public void closeMother(String entityId) {
        mAlertsModel.deleteAllAlertsForEntity(entityId);
        mTimelineEventsModel.deleteAllTimelineEventsForEntity(entityId);
        mMothersModel.close(entityId);
    }

    public void closeChild(String entityId) {
        mAlertsModel.deleteAllAlertsForEntity(entityId);
        mTimelineEventsModel.deleteAllTimelineEventsForEntity(entityId);
        mChildsModel.close(entityId);
    }

    public void closeAllMothersForEC(String ecId) {
        List<Mother> mothers = mMothersModel.findAllCasesForEC(ecId);
        if (mothers == null || mothers.isEmpty())
            return;
        for (Mother mother : mothers) {
            closeMother(mother.caseId());
        }
    }

    public List<Child> allChildrenWithMotherAndEC() {
        return mChildsModel.allChildrenWithMotherAndEC();
    }

    public List<Child> findAllChildrenByECId(String ecId) {
        return mChildsModel.findAllChildrenByECId(ecId);
    }

    public Mother findMotherWithOpenStatusByECId(String ecId) {
        return mMothersModel.findMotherWithOpenStatusByECId(ecId);
    }

    public boolean isPregnant(String ecId) {
        return mMothersModel.isPregnant(ecId);
    }

    public void updateChild(Child child) {
        mChildsModel.update(child);
    }

    public void updateMother(Mother mother) {
        mMothersModel.update(mother);
    }
}
