package org.ei.opensrp.service;

import com.google.gson.Gson;

import org.ei.drishti.dto.Action;
import org.ei.opensrp.application.OpenSRPApplication;
import org.ei.opensrp.db.adapters.ReportRepository;
import org.ei.opensrp.db.adapters.SettingsRepository;
import org.ei.opensrp.db.adapters.SharedPreferencesAdapter;
import org.ei.opensrp.domain.FetchStatus;
import org.ei.opensrp.domain.Response;
import org.ei.opensrp.router.ActionRouter;
import org.ei.opensrp.util.Log;

import java.util.List;

import javax.inject.Inject;

import static java.text.MessageFormat.format;
import static org.ei.opensrp.domain.FetchStatus.fetchedFailed;
import static org.ei.opensrp.domain.FetchStatus.nothingFetched;

public class ActionService {
    @Inject
    private ActionRouter actionRouter;

    @Inject
    private DrishtiService drishtiService;

    @Inject
    private SettingsRepository allSettings;

    @Inject
    private SharedPreferencesAdapter allSharedPreference;

    @Inject
    private ReportRepository allReports;

    public ActionService(){
        OpenSRPApplication.getInstance().inject(this);
    }

    public FetchStatus fetchNewActions() {
        String previousFetchIndex = allSettings.fetchPreviousFetchIndex();
        Response<List<Action>> response = drishtiService.fetchNewActions(allSharedPreference.fetchRegisteredANM(), previousFetchIndex);

        if (response.isFailure()) {
            return fetchedFailed;
        }

        if (response.payload().isEmpty()) {
            return nothingFetched;
        }

        handleActions(response);
        return FetchStatus.fetched;
    }

    private void handleActions(Response<List<Action>> response) {
        for (Action actionToUse : response.payload()) {
            try {
                handleAction(actionToUse);
            } catch (Exception e) {
                Log.logError(format("Failed while handling action with target: {0} and exception: {1}", actionToUse.target(), e));
            }
        }
    }

    private void handleAction(Action actionToUse) {
        if ("report".equals(actionToUse.target())) {
            runAction(actionToUse, new ActionHandler() {
                @Override
                public void run(Action action) {
                    allReports.handleAction(action);
                }
            });

        } else if ("alert".equals(actionToUse.target())) {
            runAction(actionToUse, new ActionHandler() {
                @Override
                public void run(Action action) {
                    actionRouter.directAlertAction(action);
                }
            });

        } else if ("mother".equals(actionToUse.target())) {
            runAction(actionToUse, new ActionHandler() {
                @Override
                public void run(Action action) {
                    actionRouter.directMotherAction(action);
                }
            });

        } else {
            Log.logWarn("Unknown action " + actionToUse.target());
        }

        allSettings.savePreviousFetchIndex(actionToUse.index());
    }

    private void runAction(Action action, ActionHandler actionHandler) {
        try {
            actionHandler.run(action);
        } catch (Exception e) {
            throw new RuntimeException("Failed to run action: " + new Gson().toJson(action), e);
        }
    }

    private abstract class ActionHandler {
        public void run(Action action) {
        }
    }
}
