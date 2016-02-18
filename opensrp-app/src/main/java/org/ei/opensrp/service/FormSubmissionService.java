package org.ei.opensrp.service;

import com.google.gson.Gson;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.FormDataRepository;
import org.ei.opensrp.db.adapters.SettingsRepository;
import org.ei.opensrp.domain.form.FormSubmission;

import java.util.List;

import javax.inject.Inject;

import static java.text.MessageFormat.format;
import static org.ei.opensrp.AllConstants.ENTITY_ID_PARAM;
import static org.ei.opensrp.AllConstants.FORM_NAME_PARAM;
import static org.ei.opensrp.AllConstants.INSTANCE_ID_PARAM;
import static org.ei.opensrp.AllConstants.SYNC_STATUS;
import static org.ei.opensrp.AllConstants.VERSION_PARAM;
import static org.ei.opensrp.domain.SyncStatus.SYNCED;
import static org.ei.opensrp.util.EasyMap.create;
import static org.ei.opensrp.util.Log.logError;

public class FormSubmissionService {
    @Inject
    private ZiggyService ziggyService;

    @Inject
    private FormDataRepository formDataRepository;

    @Inject
    private SettingsRepository allSettings;

    public FormSubmissionService() {
        OpenSRPApplication.getInstance().inject(this);
    }

    public void processSubmissions(List<FormSubmission> formSubmissions) {
        for (FormSubmission submission : formSubmissions) {
            if (!formDataRepository.submissionExists(submission.instanceId())) {
                try {
                    ziggyService.saveForm(getParams(submission), submission.instance());
                } catch (Exception e) {
                    logError(format("Form submission processing failed, with instanceId: {0}. Exception: {1}, StackTrace: {2}",
                            submission.instanceId(), e.getMessage(), ExceptionUtils.getStackTrace(e)));
                }
            }
            formDataRepository.updateServerVersion(submission.instanceId(), submission.serverVersion());
            allSettings.savePreviousFormSyncIndex(submission.serverVersion());
        }
    }

    private String getParams(FormSubmission submission) {
        return new Gson().toJson(
                create(INSTANCE_ID_PARAM, submission.instanceId())
                        .put(ENTITY_ID_PARAM, submission.entityId())
                        .put(FORM_NAME_PARAM, submission.formName())
                        .put(VERSION_PARAM, submission.version())
                        .put(SYNC_STATUS, SYNCED.value())
                        .map());
    }
}
