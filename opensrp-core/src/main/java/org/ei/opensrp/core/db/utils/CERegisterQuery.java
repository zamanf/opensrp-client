package org.ei.opensrp.core.db.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maimoona on 1/5/2017.
 */
public class CERegisterQuery extends RegisterQuery {

    public String referenceTable() {
        throw new UnsupportedOperationException();
    }

    public String referenceColumn() {
        throw new UnsupportedOperationException();
    }

    public CERegisterQuery(String idColumn, String mainFilter) {
        super(null, idColumn, null, mainFilter);
    }

    public String table() {
        throw new UnsupportedOperationException();
    }

    public List<String> additionalColumns(){
        throw new UnsupportedOperationException();
    }

    public String[] makeProjection(boolean autoAppendIdColumn){
        return null;
    }

    public CERegisterQuery additionalColumn(String column){
        throw new UnsupportedOperationException();
    }

    public CERegisterQuery addCondition(String condition){
        if (StringUtils.isBlank(selection)){
            selection = "";
        }
        if (StringUtils.isNotBlank(selection) && !condition.toLowerCase().trim().startsWith("and")){
            selection += " AND ";
        }
        selection += " "+condition ;
        return this;
    }

    public CERegisterQuery addOrder(String orderCondition){
        if (StringUtils.isNotBlank(order)){
            this.order += ", "+orderCondition;
        }
        else {
            this.order = orderCondition;
        }
        return this;
    }

    public void resetCondition(){
        this.selection = "";
    }

    public void resetOrder(){
        this.order = "";
    }

    public CERegisterQuery limitAndOffset(int limit, int offset){
        this.pageSize = limit;
        this.offset = offset;
        return this;
    }

    public void resetLimit(int limit){
        this.pageSize = limit;
    }

    public void resetOffset(int offset){
        this.offset = offset;
    }

    public String countQuery(){
        throw new UnsupportedOperationException();
    }
}
