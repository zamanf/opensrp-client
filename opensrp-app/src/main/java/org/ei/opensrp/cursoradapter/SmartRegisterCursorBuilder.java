package org.ei.opensrp.cursoradapter;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.repository.db.CESQLiteHelper;
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

    public static void closeCursor(){
        Log.i(SmartRegisterCursorBuilder.class.getName(), "Invalidating and closing cursor");
        if (cursor != null && !cursor.isClosed()) cursor.close();
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String sort){
        this(table, mainFilter, null, null, null, null, sort);
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, CursorSortOption sort){
        this(table, mainFilter, null, null, null, null, sort==null?null:sort.sort());
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String alias, String[] customColumns, CursorSortOption sort){
        this(table, mainFilter, alias, customColumns, null, null, sort==null?null:sort.sort());
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String alias, String[] customColumns, String vilageFilter, String searchFilter, String sort){
        this.table = table;
        query = new SmartRegisterQueryBuilder(table, alias, customColumns, mainFilter);
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
        this(table, mainFilter, null, null, villageFilter==null?null:villageFilter.filter(), searchFilter==null?null:searchFilter.getCriteria(), sortOption==null?null:sortOption.sort());
    }
    SmartRegisterCursorBuilder(SmartRegisterQueryBuilder query){
        this.table = query.table();
        this.query = query;
    }

    public SmartRegisterCursorBuilder limit(int pageSize){
        query.limitAndOffset(pageSize, 0);
        return this;
    }

    public Cursor buildCursor(DB db){
        closeCursor();

        Log.i(getClass().getName(), "Building a new cursor");
        if(db == null || db.equals(DB.DRISHTI)){
            CommonRepository commonRepository = org.ei.opensrp.Context.getInstance().commonrepository(table);
            cursor = commonRepository.RawCustomQueryForAdapter(query.toString());
        } else {
            CESQLiteHelper ceDB = Context.getInstance().ceDB();
            cursor = ceDB.rawQueryForCursor(query.toString());
        }
        return cursor;
    }

    public enum DB{
        DRISHTI,
        OPENSRP
    }
}
