package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.MotherService;

import javax.inject.Inject;

public class ANCCloseHandler implements FormSubmissionHandler {

    @Inject
    private MotherService motherService;

    public ANCCloseHandler() {
        OpenSRPApplication.getInstance().inject(this);
    }

    @Override
    public void handle(FormSubmission submission) {
        motherService.close(submission);
    }
}
