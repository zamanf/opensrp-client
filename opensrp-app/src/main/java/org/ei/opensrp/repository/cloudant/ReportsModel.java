package org.ei.opensrp.repository.cloudant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.ConflictException;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.DocumentRevision;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.cloudant.sync.query.QueryResult;

import org.ei.opensrp.R;
import org.ei.opensrp.domain.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * Created by Geoffrey Koros on 8/14/2015.
 */
public class ReportsModel extends BaseItemsModel{

    private static final String REPORT_TABLE_NAME = "report";
    private static final String INDICATOR_COLUMN = "indicator";
    private static final String ANNUAL_TARGET_COLUMN = "annualTarget";
    private static final String MONTHLY_SUMMARIES_COLUMN = "monthlySummaries";
    private static final String[] REPORT_TABLE_COLUMNS = {INDICATOR_COLUMN, ANNUAL_TARGET_COLUMN, MONTHLY_SUMMARIES_COLUMN};

    public ReportsModel(Context context){
        super(context, REPORT_TABLE_NAME);

        //setup the indexManeger
        if(mIndexManager != null){
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(INDICATOR_COLUMN, ANNUAL_TARGET_COLUMN, MONTHLY_SUMMARIES_COLUMN), "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            }else{
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }

    public void add(Report report) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(report.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            Report.fromRevision(created);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public void update(Report report) {
        try {
            updateDocument(report);
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public List<Report> allFor(String... indicators) {
        Map<String, Object> query = new HashMap<String, Object>();
        List<Object> qList = new ArrayList<Object>();
        for(String str : indicators){
            Map<String, Object> eqClause = new HashMap<String, Object>();
            Map<String, Object> orClause = new HashMap<String, Object>();
            eqClause.put("$eq", str);
            query.put(INDICATOR_COLUMN, eqClause);
            qList.add(orClause);
        }
        query.put("$or", qList);

        List<Report> reports = new ArrayList<Report>();

        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Report report = Report.fromRevision(brev);
                    if (report != null) {
                        reports.add(report);
                    }
                }
            }
        }
        return reports;
    }

    public List<Report> all() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<BasicDocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<Report> reports = new ArrayList<Report>();

        // Filter all documents down to those of type Task.
        for(BasicDocumentRevision rev : all) {
            Report t = Report.fromRevision(rev);
            if (t != null) {
                reports.add(t);
            }
        }
        return reports;
    }

    private ContentValues createValuesFor(Report report) {
        ContentValues values = new ContentValues();
        values.put(INDICATOR_COLUMN, report.indicator());
        values.put(ANNUAL_TARGET_COLUMN, report.annualTarget());
        values.put(MONTHLY_SUMMARIES_COLUMN, report.monthlySummariesJSON());
        return values;
    }

    private List<Report> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Report> reports = new ArrayList<Report>();
        while (!cursor.isAfterLast()) {
            reports.add(read(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return reports;
    }

    private Report read(Cursor cursor) {
        return new Report(cursor.getString(0), cursor.getString(1), cursor.getString(2));
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }


    /**
     * Updates an Report document within the datastore.
     * @param report Report to update
     * @return the updated revision of the Report
     * @throws ConflictException if the report passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public Report updateDocument(Report report) throws ConflictException {
        MutableDocumentRevision rev = report.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(report.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return Report.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }

    @Override
    public String getCloudantApiKey() {
        return mContext.getString(R.string.default_api_key);
    }

    @Override
    public String getCloudantDatabaseName() {
        return mContext.getString(R.string.report_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }
}
