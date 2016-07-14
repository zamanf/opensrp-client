package org.ei.opensrp.adapter;

import android.widget.ListAdapter;

import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

import java.util.List;

public interface SmartRegisterPaginatedAdapter extends ListAdapter{
    int limitPerPage();

    int refreshTotalCount();

    int getTotalCount();

    int getCount();

    int pageCount();

    int currentPage();

    boolean hasNextPage();

    boolean hasPreviousPage();

    void gotoNextPage();

    void goBackToPreviousPage();

    void refreshList(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                     SearchFilterOption searchFilter, SortOption sortOption);

    SmartRegisterClients currentPageList();

    SmartRegisterClientsProvider getListItemProvider();
}
