package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.MotherService;

import javax.inject.Inject;

public class ANCRegistrationOAHandler implements FormSubmissionHandler {

    @Inject
    private MotherService motherService;

    @Override
    public void handle(FormSubmission submission) {
        motherService.registerOutOfAreaANC(submission);
    }
}
