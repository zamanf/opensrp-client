package org.ei.opensrp.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.drishti.dto.Action;
import org.ei.opensrp.db.RepositoryManager;
import org.ei.opensrp.domain.Report;
import org.ei.opensrp.domain.ReportIndicator;
import org.ei.opensrp.util.Session;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * Created by koros on 2/15/16.
 */
public class ReportRepository {
    private static final String REPORT_TABLE_NAME = "report";
    private static final String INDICATOR_COLUMN = "indicator";
    private static final String ANNUAL_TARGET_COLUMN = "annualTarget";
    private static final String MONTHLY_SUMMARIES_COLUMN = "monthlySummaries";
    private static final String[] REPORT_TABLE_COLUMNS = {INDICATOR_COLUMN, ANNUAL_TARGET_COLUMN, MONTHLY_SUMMARIES_COLUMN};

    private Context context;
    private Session session;

    public ReportRepository(Context context, Session session){
        this.context = context;
        this.session = session;
    }

    public void update(Report report) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        database.replace(REPORT_TABLE_NAME, null, createValuesFor(report));
    }

    public List<Report> allFor(String... indicators) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        Cursor cursor = database.rawQuery(String.format("SELECT * FROM %s WHERE %s IN (%s)", REPORT_TABLE_NAME, INDICATOR_COLUMN, insertPlaceholdersForInClause(indicators.length)), indicators);
        return readAll(cursor);
    }

    public List<Report> all() {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, session.password());
        Cursor cursor = database.query(REPORT_TABLE_NAME, REPORT_TABLE_COLUMNS, null, null, null, null, null);
        return readAll(cursor);
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

    public void handleAction(Action action) {
        update(new Report(action.type(), action.get("annualTarget"), action.get("monthlySummaries")));
    }

    public List<Report> allFor(List<ReportIndicator> indicators) {
        List<String> indicatorList = new ArrayList<String>();
        for (ReportIndicator indicator : indicators) {
            indicatorList.add(indicator.value());
        }
        return allFor(indicatorList.toArray(new String[indicatorList.size()]));
    }
}
