package org.ei.opensrp.cursoradapter;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by raihan on 3/17/16.
 */
public class SmartRegisterQueryBuilder {
    private String alias;
    private String Selectquery;
    private String table;
    private String mainFilter = "";
    private String order = "";
    private String joins = "";
    private int pageSize = 20;
    private int offset = 0;
    private boolean isFTS;
    private String ftsSearchFilter = "";
    private String ftsSort = "";

    public String table() {
        return table;
    }

    public String mainFilter() {
        return mainFilter;
    }

    public String order() {
        return order;
    }

    public String joins() {
        return joins;
    }

    public int pageSize() {
        return pageSize;
    }

    public int offset() {
        return offset;
    }

    /**
     * @param table
     * @param mainFilter optional
     */
    public SmartRegisterQueryBuilder(String table, String mainFilter) {
        Selectquery = "SELECT id AS _id, t.* FROM "+table+" t ";
        this.table = table;
        if(StringUtils.isNotBlank(mainFilter)){
            this.mainFilter = mainFilter;
        }
    }

    /**
     * @param table
     * @param additionalColumns
     * @param mainFilter optional
     */
    public SmartRegisterQueryBuilder(String table, String alias, String [] additionalColumns, String mainFilter){
        this.table = table;
        alias = StringUtils.isBlank(alias)?"t":alias;
        this.alias = alias;
        if(StringUtils.isNotBlank(mainFilter)){
            this.mainFilter = mainFilter;
        }

        Selectquery = "SELECT id AS _id";

        if (additionalColumns != null) {
            for (int i = 0; i < additionalColumns.length; i++) {
                Selectquery = Selectquery + " , " + additionalColumns[i];
            }
        }
        Selectquery = Selectquery+ ", "+alias+".* FROM " + table +" AS "+alias;
    }

    /*
            This method takes in @param tablename and columns other than ID. Any special conditions
            for sorting if required can also be added in condition string and if not you can pass null.
            Alertname is the name of the alert you would like to sort this by.
             */
    public  String queryForRegisterSortBasedOnRegisterAndAlert(String tablename,String[]columns,String condition,String AlertName){
        Selectquery = "Select id as _id";
        for(int i = 0;i<columns.length;i++){
            Selectquery= Selectquery + " , " + columns[i];
        }
        Selectquery= Selectquery+ " From " + tablename;
        Selectquery = Selectquery+ " LEFT JOIN alerts ";
        Selectquery = Selectquery+ " ON "+ tablename +".id = alerts.caseID";
        if(condition != null){
            Selectquery= Selectquery+ " Where " + condition + " AND";
        }
        Selectquery= Selectquery+ " Where " + "alerts.scheduleName = '" + AlertName + "' ";
        Selectquery = Selectquery + "ORDER BY CASE WHEN alerts.status = 'urgent' THEN '1'\n" +
                "WHEN alerts.status = 'upcoming' THEN '2'\n" +
                "WHEN alerts.status = 'normal' THEN '3'\n" +
                "WHEN alerts.status = 'expired' THEN '4'\n" +
                "WHEN alerts.status is Null THEN '5'\n" +
                "Else alerts.status END ASC";
        return Selectquery;
    }

    public SmartRegisterQueryBuilder limitAndOffset(int limit, int offset){
        this.pageSize = limit;
        this.offset = offset;
        return this;
    }

    public SmartRegisterQueryBuilder addCondition(String condition){
        if (StringUtils.isNotBlank(mainFilter) && !condition.toLowerCase().trim().startsWith("and")){
            mainFilter += " AND ";
        }
        mainFilter += " "+condition ;
        return this;
    }
    public SmartRegisterQueryBuilder addOrder(String orderCondition){
        if (StringUtils.isNotBlank(order)){
            this.order += ", "+orderCondition;
        }
        else {
            this.order = orderCondition;
        }
        return this;
    }
    public SmartRegisterQueryBuilder joinwithALerts(String alertname){
        joins += " LEFT JOIN alerts ON "+alias+"._id = alerts.caseID and alerts.scheduleName = '"+alertname+"'" ;
        return this;
    }
    public SmartRegisterQueryBuilder joinwithALerts(){
        joins += " LEFT JOIN alerts ON "+alias+"._id = alerts.caseID " ;
        return this;
    }

    @Override
    public String toString(){
        String res = Selectquery;
        if (StringUtils.isNotBlank(joins)){
            res += " "+ joins;
        }
        if (StringUtils.isNotBlank(mainFilter)){
            res += " WHERE "+mainFilter;
        }
        if (StringUtils.isNotBlank(order)){
            res += " ORDER BY " + order;
        }

        res += " LIMIT "+offset+","+pageSize;

        return res;
    }

    public String toStringFts(){
        String res = Selectquery;

        String searchFilter = searchFilterFts();
        if (StringUtils.isNotBlank(searchFilter)){
            res += " WHERE "+searchFilterFts();
        }

        return res;
    }

    public String countQuery(){
        String res = "SELECT COUNT(*) FROM "+table;
        if (StringUtils.isNotBlank(joins)){
            res += " "+ joins;
        }
        if (StringUtils.isNotBlank(mainFilter)){
            res += " WHERE "+mainFilter;
        }

        return res;
    }

    public void setIsFTS(boolean isFTS) {
        this.isFTS = isFTS;
    }

    public boolean isFTS() {
        return isFTS;
    }

    public void setFtsSearchFilter(String ftsSearchFilter) {
        this.ftsSearchFilter = ftsSearchFilter;
    }

    public String getFtsSearchFilter() {
        return ftsSearchFilter;
    }

    public void setFtsSort(String ftsSort) {
        this.ftsSort = ftsSort;
    }

    private String searchFilterFts(){
        String query = "SELECT object_id FROM search JOIN search_relations ON search_rowid = search.rowid " + phraseClause(this.table, this.ftsSearchFilter) + orderByClause(this.ftsSort) + limitClause(this.pageSize, this.offset);
        String searchString = String.format(" %s IN (%s) ", "id", query);
        return searchString;
    }

    public String countQueryFts(){
        String groupByClause = " GROUP BY object_type HAVING object_type = '" + this.table + "'";
        String countQuery = "SELECT COUNT(*) FROM search JOIN search_relations ON search_rowid = search.rowid " + phraseClause(this.ftsSearchFilter) + groupByClause;
        return countQuery;
    }

    private String phraseClause(String tableName, String phrase){
        if(StringUtils.isNotBlank(phrase)){
            return " WHERE phrase MATCH '"+phrase+"*' AND object_type = '" + this.table + "'";
        }
        return "WHERE object_type = '" + tableName + "'";
    }

    private String phraseClause(String phrase){
        if(StringUtils.isNotBlank(phrase)){
            return " WHERE phrase MATCH '"+phrase+"*'";
        }
        return "";
    }

    private String orderByClause(String sort){
        if(StringUtils.isNotBlank(sort)){
            return " ORDER BY search." + sort;
        }
        return "";
    }

    private String limitClause(int limit, int offset){
        return " LIMIT " +  offset + "," + limit;
    }
}
