package org.ei.opensrp.cursoradapter;

import android.app.Activity;
import android.database.Cursor;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.view.dialog.SearchFilterOption;

/**
 * Created by Maimoona on 7/4/2016.
 */
public class SmartRegisterCursorBuilder {
    private static Cursor cursor;
    private String table;
    private SmartRegisterQueryBuilder query;
    public SmartRegisterQueryBuilder query(){
        return query;
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String sort){
        this(table, mainFilter, null, null, sort);
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, CursorSortOption sort){
        this(table, mainFilter, null, null, sort==null?null:sort.sort());
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String vilageFilter, String searchFilter, String sort){
        this.table = table;
        query = new SmartRegisterQueryBuilder(table, mainFilter);
        if (StringUtils.isNotBlank(vilageFilter)){
            query.addCondition(vilageFilter);
        }
        if (StringUtils.isNotBlank(searchFilter)){
            query.addCondition(searchFilter);
        }
        if(StringUtils.isNotBlank(sort)){
            query.addOrder(sort);
        }
    }

    SmartRegisterCursorBuilder(String table, String mainFilter, CursorFilterOption villageFilter,
                               SearchFilterOption searchFilter, CursorSortOption sortOption){
        this(table, mainFilter, villageFilter==null?null:villageFilter.filter(), searchFilter==null?null:searchFilter.getCriteria(), sortOption==null?null:sortOption.sort());
    }
    SmartRegisterCursorBuilder(SmartRegisterQueryBuilder query){
        this.table = query.table();
        this.query = query;
    }

    public SmartRegisterCursorBuilder limit(int pageSize){
        query.limitAndOffset(pageSize, 0);
        return this;
    }

    public Cursor buildCursor(){
        if (cursor != null && !cursor.isClosed()) cursor.close();

        CommonRepository commonRepository = org.ei.opensrp.Context.getInstance().commonrepository(table);
        cursor = commonRepository.RawCustomQueryForAdapter(query.toString());
        return cursor;
    }
}
