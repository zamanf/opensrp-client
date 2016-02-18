package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.EligibleCoupleService;

import javax.inject.Inject;

public class ECRegistrationHandler implements FormSubmissionHandler {

    @Inject
    private EligibleCoupleService ecService;

    @Override
    public void handle(FormSubmission submission) {
        ecService.register(submission);
    }
}
