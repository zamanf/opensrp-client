package org.ei.drishti.service.formSubmissionHandler;

import org.ei.drishti.domain.form.FormSubmission;
import org.ei.drishti.repository.FormDataRepository;

import java.util.HashMap;
import java.util.Map;

import static org.ei.drishti.AllConstants.*;
import static org.ei.drishti.event.Event.FORM_SUBMITTED;
import static org.ei.drishti.util.Log.logWarn;

public class FormSubmissionRouter {
    private final Map<String, FormSubmissionHandler> handlerMap;
    private FormDataRepository formDataRepository;

    public FormSubmissionRouter(FormDataRepository formDataRepository,
                                ECRegistrationHandler ecRegistrationHandler,
                                FPComplicationsHandler fpComplicationsHandler,
                                FPChangeHandler fpChangeHandler, RenewFPProductHandler renewFPProductHandler) {
        this.formDataRepository = formDataRepository;
        handlerMap = new HashMap<String, FormSubmissionHandler>();
        handlerMap.put(EC_REGISTRATION, ecRegistrationHandler);
        handlerMap.put(FP_COMPLICATIONS, fpComplicationsHandler);
        handlerMap.put(FP_CHANGE, fpChangeHandler);
        handlerMap.put(RENEW_FP_PRODUCT, renewFPProductHandler);
    }

    public void route(String instanceId) {
        FORM_SUBMITTED.notifyListeners(instanceId);
        FormSubmission submission = formDataRepository.fetchFromSubmission(instanceId);
        FormSubmissionHandler handler = handlerMap.get(submission.formName());
        if (handler == null) {
            logWarn("Could not find a handler due to unknown form submission: " + submission);
            return;
        }
        handler.handle(submission);
    }
}