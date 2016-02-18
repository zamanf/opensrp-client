package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ChildService;
import org.ei.opensrp.service.MotherService;

import javax.inject.Inject;

public class DeliveryOutcomeHandler implements FormSubmissionHandler {

    @Inject
    private MotherService motherService;
    @Inject
    private ChildService childService;

    @Override
    public void handle(FormSubmission submission) {
        motherService.deliveryOutcome(submission);
        childService.register(submission);
    }
}
