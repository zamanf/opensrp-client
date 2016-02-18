package org.ei.opensrp.service.formSubmissionHandler;

import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.service.ChildService;
import org.ei.opensrp.service.MotherService;

import javax.inject.Inject;

public class PNCVisitHandler implements FormSubmissionHandler {

    @Inject
    private MotherService motherService;

    @Inject
    private ChildService childService;

    @Override
    public void handle(FormSubmission submission) {
        motherService.pncVisitHappened(submission);
        childService.pncVisitHappened(submission);
    }
}
