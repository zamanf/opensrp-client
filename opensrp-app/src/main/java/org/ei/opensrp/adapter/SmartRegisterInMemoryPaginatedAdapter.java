package org.ei.opensrp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

public class SmartRegisterInMemoryPaginatedAdapter extends BaseAdapter implements SmartRegisterPaginatedAdapter{
    private final int CLIENTS_PER_PAGE;

    private int clientCount;
    private int pageCount;
    private int currentPage = 0;
    private SmartRegisterClients filteredClients;

    private final SmartRegisterClientsProvider listItemProvider;

    public SmartRegisterInMemoryPaginatedAdapter(
            int clientsPerPage, SmartRegisterClientsProvider listItemProvider) {
        this.CLIENTS_PER_PAGE = clientsPerPage;
        this.listItemProvider = listItemProvider;
        // todo refreshClients(listItemProvider.getClients());
    }

    public void refreshClients(SmartRegisterClients filteredClients) {
        this.filteredClients = filteredClients;
        refreshTotalCount();
        pageCount = (int) Math.ceil((double) clientCount / (double) limitPerPage());
        currentPage = 0;
    }

    @Override
    public int limitPerPage() {
        return CLIENTS_PER_PAGE;
    }

    public int refreshTotalCount(){
        clientCount = filteredClients.size();
        return clientCount;
    }

    public int getTotalCount(){
        return clientCount;
    }

    @Override
    public int getCount() {
        if (clientCount <= limitPerPage()) {
            return clientCount;
        } else if (currentPage == pageCount() - 1) {
            return clientCount - currentPage * limitPerPage();
        }
        return limitPerPage();
    }

    @Override
    public Object getItem(int i) {
        return filteredClients.get(i);
    }

    @Override
    public long getItemId(int i) {
        return actualPosition(i);
    }

    @Override
    public View getView(int i, View parentView, ViewGroup viewGroup) {
        return listItemProvider.getView((SmartRegisterClient) getItem(actualPosition(i)), parentView, viewGroup);
    }

    private int actualPosition(int i) {
        if (clientCount <= limitPerPage()) {
            return i;
        } else {
            return i + (currentPage * limitPerPage());
        }
    }

    public int pageCount() {
        return pageCount;
    }

    public int currentPage() {
        return currentPage + 1 > pageCount() ? pageCount() : currentPage + 1;
    }

    public void nextPage() {
        if (hasNextPage()) {
            currentPage++;
        }
    }

    public void previousPage() {
        if (hasPreviousPage()) {
            currentPage--;
        }
    }

    public boolean hasNextPage() {
        return currentPage < pageCount() - 1;
    }

    public boolean hasPreviousPage() {
        return currentPage > 0;
    }

    public void gotoNextPage() {
        nextPage();
        notifyDataSetChanged();
    }

    public void goBackToPreviousPage() {
        previousPage();
        notifyDataSetChanged();
    }

    public void refreshList(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                            SearchFilterOption searchFilter, SortOption sortOption) {
        SmartRegisterClients filteredClients = listItemProvider
                .updateClients(villageFilter, serviceModeOption, searchFilter, sortOption);
        refreshClients(filteredClients);
        notifyDataSetChanged();
    }

    @Override
    public SmartRegisterClients currentPageList() {
        return listItemProvider.getClients();
    }

    public SmartRegisterClientsProvider getListItemProvider() {
        return listItemProvider;
    }
}
