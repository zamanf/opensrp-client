package org.ei.opensrp.repository;

import org.ei.opensrp.repository.cloudant.AlertsModel;

public class AllAlerts {
    private AlertRepository repository;

    AlertsModel mAlertsModel = org.ei.opensrp.Context.getInstance().alertsModel();

    public AllAlerts(AlertRepository repository) {
        this.repository = repository;
    }

    public void changeAlertStatusToInProcess(String entityId, String alertName) {
        //repository.changeAlertStatusToInProcess(entityId, alertName);
        mAlertsModel.changeAlertStatusToInProcess(entityId, alertName);
    }
}
