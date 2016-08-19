package org.ei.opensrp.service;

import com.google.gson.Gson;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.ei.opensrp.commonregistry.AllCommonsRepository;
import org.ei.opensrp.domain.form.FormSubmission;
import org.ei.opensrp.repository.AllSettings;
import org.ei.opensrp.repository.FormDataRepository;

import java.util.ArrayList;
import java.util.List;

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
    private ZiggyService ziggyService;
    private FormDataRepository formDataRepository;
    private AllSettings allSettings;
    private AllCommonsRepository childRepository;
    private AllCommonsRepository womanRepository;

    public FormSubmissionService(ZiggyService ziggyService, FormDataRepository formDataRepository, AllCommonsRepository childRepository, AllCommonsRepository womanRepository, AllSettings allSettings) {
        this.ziggyService = ziggyService;
        this.formDataRepository = formDataRepository;
        this.allSettings = allSettings;
        this.childRepository = childRepository;
        this.womanRepository = womanRepository;
    }

    public void processSubmissions(List<FormSubmission> formSubmissions) {
        List<String> entityIds = new ArrayList<String>();
        for (FormSubmission submission : formSubmissions) {
            if (!formDataRepository.submissionExists(submission.instanceId())) {
                try {
                    ziggyService.saveForm(getParams(submission), submission.instance());
                    entityIds.add(submission.entityId());

                } catch (Exception e) {
                    logError(format("Form submission processing failed, with instanceId: {0}. Exception: {1}, StackTrace: {2}",
                            submission.instanceId(), e.getMessage(), ExceptionUtils.getStackTrace(e)));
                }
            }
            formDataRepository.updateServerVersion(submission.instanceId(), submission.serverVersion());
            allSettings.savePreviousFormSyncIndex(submission.serverVersion());
        }
        if(!entityIds.isEmpty()) {
            List<String> remainingIds = childRepository.updateSearch(entityIds);
            if (!remainingIds.isEmpty()) {
                womanRepository.updateSearch(remainingIds);
            }
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
