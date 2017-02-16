package org.ei.opensrp.core.template;

import android.view.View;
import android.view.ViewGroup;

import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;

import java.util.List;

public interface RegisterClientsProvider <T>{

    public View getView(T client, View parentView, ViewGroup viewGroup);

    public List<T> getClients();

    void onServiceModeSelected(ServiceModeOption serviceModeOption);

    public View inflateLayoutForAdapter();
}
