package org.ei.opensrp.household.household;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;

/**
 * Created by Safwan on 5/10/2016.
 */
public class HouseholdFollowupHandler implements FormSubmissionHandler {

    private HouseholdService householdService;

    public HouseholdFollowupHandler(HouseholdService householdService) {
        this.householdService = householdService;
    }

    @Override
    public void handle(FormSubmission submission) {
        householdService.followup(submission);
    }
}
