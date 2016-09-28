package org.ei.opensrp.cursoradapter;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.repository.db.CESQLiteHelper;
import org.ei.opensrp.util.Utils;
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

    public SmartRegisterCursorBuilder(String table, String mainFilter, String sort, String idColumn){
        this(table, mainFilter, null, null, null, null, null, sort, idColumn);
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, CursorSortOption sort, String idColumn){
        this(table, mainFilter, null, null, null, null, null, sort==null?null:sort.sort(), idColumn);
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String alias, String[] customColumns, CursorSortOption sort, String idColumn){
        this(table, mainFilter, alias, customColumns, null, null, null, sort==null?null:sort.sort(), idColumn);
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String alias, String[] customColumns, String[] customJoins, CursorSortOption sort, String idColumn){
        this(table, mainFilter, alias, customColumns, customJoins, null, null, sort==null?null:sort.sort(), idColumn);
    }

    public SmartRegisterCursorBuilder(String table, String mainFilter, String alias, String[] customColumns, String[] customJoins, String vilageFilter, String searchFilter, String sort, String idColumn){
        this.table = table;
        query = new SmartRegisterQueryBuilder(table, alias, customColumns, mainFilter, idColumn);
        if (StringUtils.isNotBlank(vilageFilter)){
            query.addCondition(vilageFilter);
        }
        if (StringUtils.isNotBlank(searchFilter)){
            query.addCondition(searchFilter);
        }
        if (customJoins != null){
            for (String j: customJoins) {
                query.addJoin(j);
            }
        }
        if(StringUtils.isNotBlank(sort)){
            query.addOrder(sort);
        }
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

        Log.i(getClass().getName(), "Building a new cursor for "+query.toString());
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
