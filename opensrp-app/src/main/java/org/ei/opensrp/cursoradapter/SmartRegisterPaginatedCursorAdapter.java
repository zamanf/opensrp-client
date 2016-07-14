package org.ei.opensrp.cursoradapter;

import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

public class SmartRegisterPaginatedCursorAdapter extends CursorAdapter implements SmartRegisterPaginatedAdapter{
    private final SmartRegisterClientsProvider listItemProvider;
    private static final int PAGE_SIZE = 20;
    Context context;
    CommonRepository commonRepository;
    String table;
    String mainFilter;
    SmartRegisterQueryBuilder lastQuery;
    SmartRegisterClients clients;

    public SmartRegisterPaginatedCursorAdapter(Context context, SmartRegisterCursorBuilder cursorBuilder, SmartRegisterClientsProvider listItemProvider) {
        super(context, cursorBuilder.buildCursor(), false);
        this.listItemProvider = listItemProvider;
        this.context= context;
        this.table = cursorBuilder.query().table();
        this.mainFilter = cursorBuilder.query().mainFilter();
        lastQuery = cursorBuilder.query();
        this.commonRepository = org.ei.opensrp.Context.getInstance().commonrepository(table);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        clients = new SmartRegisterClients();
        return  listItemProvider.inflateLayoutForAdapter();
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(cursor);
        CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), null);
        pClient.setColumnmaps(personinlist.getColumnmaps());
        clients.add(pClient);
        listItemProvider.getView(pClient, view, null/*todo*/);
    }

    public void swapCursorWithNew(Cursor newCursor) {
        Cursor c = super.swapCursor(newCursor);
        if (c != null && !c.isClosed()) c.close();
    }


    private int totalcount = 0;
    public int limitPerPage(){return lastQuery.pageSize();}
    public int currentoffset(){return lastQuery.offset();}

    public int getTotalCount(){
        return totalcount;
    }

    @Override
    public int getCount() {
        refreshTotalCount();
        if(totalcount < currentoffset()+limitPerPage()){
            return totalcount - currentoffset();
        }
        return limitPerPage();
    }

    public int pageCount() {
        return totalcount/limitPerPage();
    }

    public int currentPage() {
        if(currentoffset() != 0) {
            return (int)Math.ceil(pageCount()-((totalcount-currentoffset())/(1.0*limitPerPage())));
        }else{
            return 1;
        }
    }

    public boolean hasNextPage() {
        return ((totalcount>(currentoffset()+limitPerPage())));
    }

    public boolean hasPreviousPage() {
        return currentoffset()!=0;
    }

    public void gotoNextPage() {
        if(hasNextPage()){
            lastQuery.limitAndOffset(limitPerPage(), currentoffset()+limitPerPage());
            filterandSortExecute();
            notifyDataSetChanged();
        }
    }

    public void goBackToPreviousPage() {
        if(hasPreviousPage()){
            lastQuery.limitAndOffset(limitPerPage(), currentoffset()-limitPerPage());
            filterandSortExecute();
            notifyDataSetChanged();
        }
    }

    public void refreshList(FilterOption villageFilter, ServiceModeOption serviceModeOption,
                            SearchFilterOption searchFilter, SortOption sortOption) {
        filterandSortExecute(((CursorFilterOption)villageFilter).filter(), searchFilter.getCriteria(),
                ((CursorSortOption)sortOption).sort());
        notifyDataSetChanged();
    }

    @Override
    public SmartRegisterClients currentPageList() {
        SmartRegisterClients c = new SmartRegisterClients();
        boolean hasFirst = getCursor().moveToFirst();
        do {
            if (hasFirst) {
                CommonPersonObject personinlist = commonRepository.readAllcommonforCursorAdapter(getCursor());
                CommonPersonObjectClient pClient = new CommonPersonObjectClient(personinlist.getCaseId(), personinlist.getDetails(), null);
                pClient.setColumnmaps(personinlist.getColumnmaps());
                c.add(pClient);
            }
        }
        while (getCursor().moveToNext());

        getCursor().moveToFirst();
        return c;
    }

    @Override
    public SmartRegisterClientsProvider getListItemProvider() {
        return listItemProvider;
    }

    public void filterandSortExecute(String vilageFilter, String searchFilter, String sort) {
//todo        refresh();
        lastQuery = new SmartRegisterQueryBuilder(table, mainFilter);
        if (StringUtils.isNotBlank(vilageFilter)){
            lastQuery.addCondition(vilageFilter);
        }
        if (StringUtils.isNotBlank(searchFilter)){
            lastQuery.addCondition(searchFilter);
        }
        if(StringUtils.isNotBlank(sort)){
            lastQuery.addOrder(sort);
        }
        lastQuery.limitAndOffset(limitPerPage(), currentoffset());

        filterandSortExecute();
    }

    public void filterandSortExecute() {
        clients = new SmartRegisterClients();

        String query = lastQuery.toString();
        Cursor c = commonRepository.RawCustomQueryForAdapter(query);
        swapCursorWithNew(c);
    }

    public int refreshTotalCount(){
        Cursor c = commonRepository.RawCustomQueryForAdapter(lastQuery.countQuery());
        c.moveToFirst();
        totalcount= c.getInt(0);
        c.close();
        return  totalcount;
    }

}
