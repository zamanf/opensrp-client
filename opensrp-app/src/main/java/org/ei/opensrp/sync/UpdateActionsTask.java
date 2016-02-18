package org.ei.opensrp.sync;

import android.content.Context;
import android.widget.Toast;

import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.domain.DownloadStatus;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.service.ActionService;
import org.ei.opensrp.service.AllFormVersionSyncService;
import org.ei.opensrp.service.FormSubmissionSyncService;
import org.ei.opensrp.view.BackgroundAction;
import org.ei.opensrp.view.LockingBackgroundTask;
import org.ei.opensrp.view.ProgressIndicator;

import javax.inject.Inject;

import static org.ei.opensrp.domain.FetchStatus.fetched;
import static org.ei.opensrp.domain.FetchStatus.nothingFetched;
import static org.ei.opensrp.util.Log.logInfo;

public class UpdateActionsTask {
    private final LockingBackgroundTask task;

    @Inject
    private ActionService actionService;

    private Context context;

    @Inject
    private FormSubmissionSyncService formSubmissionSyncService;

    @Inject
    private AllFormVersionSyncService allFormVersionSyncService;

    public UpdateActionsTask(Context context, ProgressIndicator progressIndicator) {
        OpenSRPApplication.getInstance().inject(this);
        this.context = context;
        task = new LockingBackgroundTask(progressIndicator);
    }

    public void updateFromServer(final AfterFetchListener afterFetchListener) {
        if (org.ei.opensrp.Context.getInstance().IsUserLoggedOut()) {
            logInfo("Not updating from server as user is not logged in.");
            return;
        }

        task.doActionInBackground(new BackgroundAction<FetchStatus>() {
            public FetchStatus actionToDoInBackgroundThread() {
                allFormVersionSyncService.verifyFormsInFolder();

                FetchStatus fetchStatusForForms = formSubmissionSyncService.sync();
                FetchStatus fetchStatusForActions = actionService.fetchNewActions();
                FetchStatus fetchVersionStatus = allFormVersionSyncService.pullFormDefinitionFromServer();
                DownloadStatus downloadStatus = allFormVersionSyncService.downloadAllPendingFormFromServer();

                if(downloadStatus == DownloadStatus.downloaded) {
                    allFormVersionSyncService.unzipAllDownloadedFormFile();
                }

                if(fetchStatusForActions == fetched || fetchStatusForForms == fetched ||
                        fetchVersionStatus == fetched || downloadStatus == DownloadStatus.downloaded)
                    return fetched;

                return fetchStatusForForms;
            }

            public void postExecuteInUIThread(FetchStatus result) {
                if (result != null && context != null && result != nothingFetched) {
                    Toast.makeText(context, result.displayValue(), Toast.LENGTH_SHORT).show();
                }
                afterFetchListener.afterFetch(result);
            }
        });
    }
}