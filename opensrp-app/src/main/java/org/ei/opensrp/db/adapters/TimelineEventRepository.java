package org.ei.opensrp.db.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.db.RepositoryManager;
import org.ei.opensrp.domain.TimelineEvent;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koros on 2/12/16.
 */
public class TimelineEventRepository {
    private static final String TIMELINEEVENT_TABLE_NAME = "timelineEvent";
    private static final String CASEID_COLUMN = "caseId";
    private static final String TYPE_COLUMN = "type";
    private static final String REF_DATE_COLUMN = "referenceDate";
    private static final String TITLE_COLUMN = "title";
    private static final String DETAIL1_COLUMN = "detail1";
    private static final String DETAIL2_COLUMN = "detail2";
    private static final String[] TIMELINEEVENT_TABLE_COLUMNS = {CASEID_COLUMN, TYPE_COLUMN, REF_DATE_COLUMN, TITLE_COLUMN, DETAIL1_COLUMN, DETAIL2_COLUMN};

    private Context context;
    private String password;

    public TimelineEventRepository(Context context, String password){
        this.context = context;
        this.password = password;
    }

    public void add(TimelineEvent timelineEvent) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, password);
        database.insert(TIMELINEEVENT_TABLE_NAME, null, createValuesFor(timelineEvent));
    }

    public List<TimelineEvent> allFor(String caseId) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, password);
        Cursor cursor = database.query(TIMELINEEVENT_TABLE_NAME, TIMELINEEVENT_TABLE_COLUMNS, CASEID_COLUMN + " = ?", new String[]{caseId}, null, null, null);
        return readAllTimelineEvents(cursor);
    }

    public void deleteAllTimelineEventsForEntity(String caseId) {
        SQLiteDatabase database = RepositoryManager.getDatabase(context, password);
        database.delete(TIMELINEEVENT_TABLE_NAME, CASEID_COLUMN + " = ?", new String[]{caseId});
    }

    private List<TimelineEvent> readAllTimelineEvents(Cursor cursor) {
        cursor.moveToFirst();
        List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();
        while (!cursor.isAfterLast()) {
            timelineEvents.add(new TimelineEvent(cursor.getString(0), cursor.getString(1), LocalDate.parse(cursor.getString(2)), cursor.getString(3), cursor.getString(4), cursor.getString(5)));
            cursor.moveToNext();
        }
        cursor.close();
        return timelineEvents;
    }

    private ContentValues createValuesFor(TimelineEvent timelineEvent) {
        ContentValues values = new ContentValues();
        values.put(CASEID_COLUMN, timelineEvent.caseId());
        values.put(TYPE_COLUMN, timelineEvent.type());
        values.put(REF_DATE_COLUMN, timelineEvent.referenceDate().toString());
        values.put(TITLE_COLUMN, timelineEvent.title());
        values.put(DETAIL1_COLUMN, timelineEvent.detail1());
        values.put(DETAIL2_COLUMN, timelineEvent.detail2());
        return values;
    }
}
