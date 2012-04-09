package org.ei.drishti.controller;

import org.ei.drishti.adapter.AlertAdapter;
import org.ei.drishti.domain.AlertAction;
import org.ei.drishti.domain.Response;
import org.ei.drishti.repository.AllAlerts;
import org.ei.drishti.repository.AllSettings;
import org.ei.drishti.service.DrishtiService;

import java.util.List;

public class AlertController {
    private DrishtiService drishtiService;
    private AllSettings allSettings;
    private AllAlerts allAlerts;
    private AlertAdapter adapter;

    public AlertController(DrishtiService drishtiService, AllSettings allSettings, AllAlerts allAlerts, AlertAdapter adapter) {
        this.drishtiService = drishtiService;
        this.allSettings = allSettings;
        this.allAlerts = allAlerts;
        this.adapter = adapter;
    }

    public void refreshAlerts() {
        String previousFetchIndex = allSettings.fetchPreviousFetchIndex();
        Response<List<AlertAction>> response = drishtiService.fetchNewAlertActions(allSettings.fetchANMIdentifier(), previousFetchIndex);

        if (response.isFailure() || response.payload().isEmpty()) {
            return;
        }

        allAlerts.saveNewAlerts(response.payload());
        adapter.refresh(allAlerts.fetchAllAlerts());
    }
}
