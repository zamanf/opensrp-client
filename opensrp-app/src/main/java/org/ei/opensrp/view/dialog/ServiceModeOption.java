package org.ei.opensrp.view.dialog;

import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

import static org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public abstract class ServiceModeOption implements DialogOption {

    private SmartRegisterClientsProvider clientsProvider;

    public ServiceModeOption(SmartRegisterClientsProvider clientsProvider) {
        this.clientsProvider = clientsProvider;
    }

    public void apply() {
        clientsProvider.onServiceModeSelected(this);
    }

    public SmartRegisterClientsProvider provider() {
        return clientsProvider;
    }

    public abstract ClientsHeaderProvider getHeaderProvider();
}
