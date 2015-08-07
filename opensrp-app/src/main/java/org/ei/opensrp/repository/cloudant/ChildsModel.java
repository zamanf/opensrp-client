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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.domain.Alert;
import org.ei.opensrp.domain.Child;
import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.domain.Mother;
import org.ei.opensrp.repository.EligibleCoupleRepository;
import org.ei.opensrp.repository.MotherRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.ei.drishti.dto.AlertStatus.complete;
import static org.ei.opensrp.repository.EligibleCoupleRepository.EC_TABLE_COLUMNS;
import static org.ei.opensrp.repository.EligibleCoupleRepository.EC_TABLE_NAME;
import static org.ei.opensrp.repository.MotherRepository.MOTHER_TABLE_COLUMNS;
import static org.ei.opensrp.repository.MotherRepository.MOTHER_TABLE_NAME;

/**
 * Created by Geoffrey Koros on 8/7/2015.
 */
public class ChildsModel extends BaseItemsModel{

    public static final String CHILD_TABLE_NAME = "child";

    private static final String ID_COLUMN = "id";
    private static final String MOTHER_ID_COLUMN = "motherCaseId";
    private static final String THAYI_CARD_COLUMN = "thayiCardNumber";
    private static final String DATE_OF_BIRTH_COLUMN = "dateOfBirth";
    private static final String GENDER_COLUMN = "gender";
    private static final String DETAILS_COLUMN = "details";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String PHOTO_PATH_COLUMN = "photoPath";
    public static final String[] CHILD_TABLE_COLUMNS = {ID_COLUMN, MOTHER_ID_COLUMN, THAYI_CARD_COLUMN, DATE_OF_BIRTH_COLUMN, GENDER_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN, PHOTO_PATH_COLUMN};
    public static final String NOT_CLOSED = "false";

    public ChildsModel(Context context) {
        super(context, CHILD_TABLE_NAME);
        //setup the indexManeger
        if(mIndexManager != null){
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(ID_COLUMN, MOTHER_ID_COLUMN, THAYI_CARD_COLUMN,
                                DATE_OF_BIRTH_COLUMN, GENDER_COLUMN, IS_CLOSED_COLUMN),
                        "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            }else{
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }

    public void add(Child child) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(child.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public void update(Child child) throws ConflictException {
        MutableDocumentRevision rev = child.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(child.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public List<Child> all() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<BasicDocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<Child> children = new ArrayList<Child>();

        // Filter all documents down to those of type Task.
        for(BasicDocumentRevision rev : all) {
            Child t = Child.fromRevision(rev);
            if (t != null) {
                children.add(t);
            }
        }

        return children;
    }

    //TODO:
    public Child find(String caseId) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(ID_COLUMN, caseId);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Child child = Child.fromRevision(brev);
                    if (child != null) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    //TODO:
    public List<Child> findChildrenByCaseIds(String... caseIds) {
        return null;
    }

    public void updateDetails(String caseId, Map<String, String> details) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(ID_COLUMN, caseId);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        Child child = Child.fromRevision(brev);
                        child.setDetails(details);
                        updateDocument(child);
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public List<Child> findByMotherCaseId(String caseId) {
        List<Child> children = new ArrayList<Child>();
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(MOTHER_ID_COLUMN, caseId);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        Child child = Child.fromRevision(brev);
                        children.add(child);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return children;
    }

    public long count() {
        return  this.mDatastore.getDocumentCount();
    }

    public void close(String caseId) {
        markAsClosed(caseId);
    }

    //TODO:
    public List<Child> allChildrenWithMotherAndEC() {
        return  null;
    }

    private String tableColumnsForQuery(String tableName, String[] tableColumns) {
        return StringUtils.join(prepend(tableColumns, tableName), ", ");
    }

    private String[] prepend(String[] input, String tableName) {
        int length = input.length;
        String[] output = new String[length];
        for (int index = 0; index < length; index++) {
            output[index] = tableName + "." + input[index] + " as " + tableName + input[index];
        }
        return output;
    }

    private void markAsClosed(String caseId) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(ID_COLUMN, caseId);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        Child child = Child.fromRevision(brev);
                        child.setIsClosed(true);
                        updateDocument(child);
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    private ContentValues createValuesFor(Child child) {
        ContentValues values = new ContentValues();
        values.put(ID_COLUMN, child.caseId());
        values.put(MOTHER_ID_COLUMN, child.motherCaseId());
        values.put(THAYI_CARD_COLUMN, child.thayiCardNumber());
        values.put(DATE_OF_BIRTH_COLUMN, child.dateOfBirth());
        values.put(GENDER_COLUMN, child.gender());
        values.put(DETAILS_COLUMN, new Gson().toJson(child.details()));
        values.put(IS_CLOSED_COLUMN, Boolean.toString(child.isClosed()));
        values.put(PHOTO_PATH_COLUMN, child.photoPath());
        return values;
    }

    private List<Child> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Child> children = new ArrayList<Child>();
        while (!cursor.isAfterLast()) {
            children.add(new Child(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4),
                            new Gson().<Map<String, String>>fromJson(cursor.getString(5), new TypeToken<Map<String, String>>() {
                            }.getType()))
                            .setIsClosed(Boolean.valueOf(cursor.getString(6)))
                            .withPhotoPath(cursor.getString(cursor.getColumnIndex(PHOTO_PATH_COLUMN)))
            );
            cursor.moveToNext();
        }
        cursor.close();
        return children;
    }

    private EligibleCouple ecFromCursor(Cursor cursor) {
        return new EligibleCouple(
                getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.ID_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.WIFE_NAME_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.HUSBAND_NAME_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.EC_NUMBER_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.VILLAGE_NAME_COLUMN),
                getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.SUBCENTER_NAME_COLUMN),
                new Gson().<Map<String, String>>fromJson(getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.DETAILS_COLUMN), new TypeToken<Map<String, String>>() {
                }.getType()))
                .withPhotoPath(getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.PHOTO_PATH_COLUMN))
                .withOutOfArea(getColumnValueByAlias(cursor, EC_TABLE_NAME, EligibleCoupleRepository.IS_OUT_OF_AREA_COLUMN));
    }

    private Mother motherFromCursor(Cursor cursor) {
        return new Mother(
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.ID_COLUMN),
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.EC_CASEID_COLUMN),
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.THAYI_CARD_NUMBER_COLUMN),
                getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.REF_DATE_COLUMN))
                .withDetails(new Gson().<Map<String, String>>fromJson(getColumnValueByAlias(cursor, MOTHER_TABLE_NAME, MotherRepository.DETAILS_COLUMN), new TypeToken<Map<String, String>>() {
                }.getType()));
    }

    private String getColumnValueByAlias(Cursor cursor, String table, String column) {
        return cursor.getString(cursor.getColumnIndex(table + column));
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    public void updatePhotoPath(String caseId, String imagePath) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(ID_COLUMN, caseId);
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        Child child = Child.fromRevision(brev);
                        child.setPhotoPath(imagePath);
                        updateDocument(child);
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public List<Child> findAllChildrenByECId(String ecId) {
        return null;
    }

    //TODO:
    public void delete(String childId) {

    }

    /**
     * Updates an Child document within the datastore.
     * @param child Child to update
     * @return the updated revision of the Alert
     * @throws ConflictException if the task passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public Alert updateDocument(Child child) throws ConflictException {
        MutableDocumentRevision rev = child.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(child.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return Alert.fromRevision(updated);
        } catch (DocumentException de) {
            return null;
        }
    }
}
