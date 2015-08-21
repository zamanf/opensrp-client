package org.ei.opensrp.repository.cloudant;

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
import org.ei.opensrp.domain.TimelineEvent;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Geoffrey Koros on 8/13/2015.
 */
public class TimelineEventsModel extends BaseItemsModel {

    private static final String TIMELINEEVENT_TABLE_NAME = "timelineEvent";
    private static final String CASEID_COLUMN = "caseId";
    private static final String TYPE_COLUMN = "type";
    private static final String REF_DATE_COLUMN = "referenceDate";
    private static final String TITLE_COLUMN = "title";
    private static final String DETAIL1_COLUMN = "detail1";
    private static final String DETAIL2_COLUMN = "detail2";

    public TimelineEventsModel(Context context){
        super(context, TIMELINEEVENT_TABLE_NAME);

        //setup the indexManeger
        if(mIndexManager != null){
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(CASEID_COLUMN, TYPE_COLUMN, REF_DATE_COLUMN, TITLE_COLUMN), "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            }else{
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }

    public void add(TimelineEvent timelineEvent) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(createValuesFor(timelineEvent));
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            TimelineEvent.fromRevision(created);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public List<TimelineEvent> allFor(String caseId) {
        List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(CASEID_COLUMN, caseId);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    TimelineEvent timelineEvent = TimelineEvent.fromRevision(brev);
                    if (timelineEvent != null) {
                        timelineEvents.add(timelineEvent);
                    }
                }
            }
        }
        return timelineEvents;
    }

    public void deleteAllTimelineEventsForEntity(String caseId) {
        try {
            List<TimelineEvent> timelineEvents = new ArrayList<TimelineEvent>();
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(CASEID_COLUMN, caseId);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        TimelineEvent timelineEvent = TimelineEvent.fromRevision(brev);
                        if (timelineEvent != null) {
                            deleteDocument(timelineEvent);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
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

    private Map<String,Object> createValuesFor(TimelineEvent timelineEvent) {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put(CASEID_COLUMN, timelineEvent.caseId());
        values.put(TYPE_COLUMN, timelineEvent.type());
        values.put(REF_DATE_COLUMN, timelineEvent.referenceDate().toString());
        values.put(TITLE_COLUMN, timelineEvent.title());
        values.put(DETAIL1_COLUMN, timelineEvent.detail1());
        values.put(DETAIL2_COLUMN, timelineEvent.detail2());
        return values;
    }

    /**
     * Deletes a TimelineEvent document within the datastore.
     * @param timelineEvent TimelineEvent to delete
     * @throws ConflictException if the TimelineEvent passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public void deleteDocument(TimelineEvent timelineEvent) throws ConflictException {
        this.mDatastore.deleteDocumentFromRevision(timelineEvent.getDocumentRevision()); //We should have db column rather than actually deleting the entity
    }

    @Override
    public String getCloudantApiKey() {
        return mContext.getString(R.string.default_api_key);
    }

    @Override
    public String getCloudantDatabaseName() {
        return mContext.getString(R.string.timeline_event_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }

}
