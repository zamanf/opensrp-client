package org.ei.opensrp.service;


import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.FormDataRepository;

import javax.inject.Inject;

public class PendingFormSubmissionService {

    @Inject
    private FormDataRepository formDataRepository;

    public PendingFormSubmissionService() {
        OpenSRPApplication.getInstance().inject(this);
    }


    public long pendingFormSubmissionCount() {
        return formDataRepository.getPendingFormSubmissionsCount();
    }
}