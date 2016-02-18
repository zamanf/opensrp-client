package org.ei.opensrp.service;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.BeneficiariesAdapter;
import org.ei.opensrp.db.adapters.EligibleCoupleRepository;
import org.ei.opensrp.db.adapters.TimelineEventRepository;
import org.ei.opensrp.domain.TimelineEvent;
import org.ei.opensrp.domain.form.FormSubmission;

import javax.inject.Inject;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.ei.opensrp.AllConstants.NEW_FP_METHOD_FIELD_NAME;
import static org.ei.opensrp.domain.TimelineEvent.forChangeOfFPMethod;
import static org.ei.opensrp.util.EasyMap.mapOf;

public class EligibleCoupleService {
    @Inject
    private EligibleCoupleRepository allEligibleCouples;

    @Inject
    private TimelineEventRepository allTimelineEvents;

    @Inject
    private BeneficiariesAdapter allBeneficiaries;

    public EligibleCoupleService() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public void register(FormSubmission submission) {
        if (isNotBlank(submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE))) {
            allTimelineEvents.add(TimelineEvent.forECRegistered(submission.entityId(), submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE)));
        }
    }

    public void fpComplications(FormSubmission submission) {
    }

    public void fpChange(FormSubmission submission) {
        String fpMethodChangeDate = submission.getFieldValue(AllConstants.ECRegistrationFields.FAMILY_PLANNING_METHOD_CHANGE_DATE);
        if (isBlank(fpMethodChangeDate)) {
            fpMethodChangeDate = submission.getFieldValue(AllConstants.CommonFormFields.SUBMISSION_DATE);
        }
        allTimelineEvents.add(forChangeOfFPMethod(submission.entityId(), submission.getFieldValue(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD),
                submission.getFieldValue(NEW_FP_METHOD_FIELD_NAME), fpMethodChangeDate));
        allEligibleCouples.mergeDetails(submission.entityId(), mapOf(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD, submission.getFieldValue(NEW_FP_METHOD_FIELD_NAME)));
    }

    public void renewFPProduct(FormSubmission submission) {
    }

    public void closeEligibleCouple(FormSubmission submission) {
        allEligibleCouples.close(submission.entityId());
        allBeneficiaries.closeAllMothersForEC(submission.entityId());
    }
}
