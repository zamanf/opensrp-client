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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.tuple.Pair;
import org.ei.opensrp.R;
import org.ei.opensrp.domain.EligibleCouple;
import org.ei.opensrp.domain.Mother;
import org.ei.opensrp.repository.EligibleCoupleRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.repeat;
import static org.ei.opensrp.repository.EligibleCoupleRepository.IS_OUT_OF_AREA_COLUMN;

/**
 * Created by Geoffrey Koros on 8/10/2015.
 */
public class MothersModel extends BaseItemsModel{

    private static final String MOTHER_TYPE_INDEX_SQL = "CREATE INDEX mother_type_index ON mother(type);";
    private static final String MOTHER_REFERENCE_DATE_INDEX_SQL = "CREATE INDEX mother_referenceDate_index ON mother(referenceDate);";
    public static final String MOTHER_TABLE_NAME = "mother";
    public static final String ID_COLUMN = "id";
    public static final String EC_CASEID_COLUMN = "ecCaseId";
    public static final String THAYI_CARD_NUMBER_COLUMN = "thayiCardNumber";
    private static final String TYPE_COLUMN = "type";
    public static final String REF_DATE_COLUMN = "referenceDate";
    public static final String DETAILS_COLUMN = "details";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String[] MOTHER_TABLE_COLUMNS = {ID_COLUMN, EC_CASEID_COLUMN, THAYI_CARD_NUMBER_COLUMN, TYPE_COLUMN, REF_DATE_COLUMN, DETAILS_COLUMN, IS_CLOSED_COLUMN};

    public static final String TYPE_ANC = "ANC";
    public static final String TYPE_PNC = "PNC";
    private static final String NOT_CLOSED = "false";

    public MothersModel(Context context){
        super(context, MOTHER_TABLE_NAME);

        //setup the indexManeger
        if(mIndexManager != null){
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(ID_COLUMN, EC_CASEID_COLUMN, THAYI_CARD_NUMBER_COLUMN, TYPE_COLUMN,
                                REF_DATE_COLUMN, IS_CLOSED_COLUMN),
                        "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            }else{
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }

    public void add(Mother mother) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(mother.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            Mother.fromRevision(created);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public void switchToPNC(String caseId) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(ID_COLUMN, caseId);//TODO: potential caveat, the id column isn't initialized most probably; probably use document id
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        Mother mother = Mother.fromRevision(brev);
                        if (mother != null) {
                            mother.setType(TYPE_ANC);
                            updateDocument(mother);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    public List<Mother> allANCs() {
        List<Mother> mothers = new ArrayList<Mother>();
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(TYPE_COLUMN, TYPE_ANC);
        query.put(IS_CLOSED_COLUMN, NOT_CLOSED);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Mother mother = Mother.fromRevision(brev);
                    if (mother != null) {
                        mothers.add(mother);
                    }
                }
            }
        }
        return mothers;
    }

    public Mother findById(String entityId) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(ID_COLUMN, entityId);//TODO: potential caveat, the id column isn't initialized most probably; probably use document id
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Mother mother = Mother.fromRevision(brev);
                    if (mother != null) {
                        return mother;
                    }
                }
            }
        }
        return null;
    }

    public List<Mother> allPNCs() {
        List<Mother> mothers = new ArrayList<Mother>();
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(TYPE_COLUMN, TYPE_PNC);
        query.put(IS_CLOSED_COLUMN, NOT_CLOSED);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Mother mother = Mother.fromRevision(brev);
                    if (mother != null) {
                        mothers.add(mother);
                    }
                }
            }
        }
        return mothers;
    }

    public long ancCount() {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(TYPE_COLUMN, TYPE_ANC);
        query.put(IS_CLOSED_COLUMN, NOT_CLOSED);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            return result.size();
        }
        return 0;
    }

    public long pncCount() {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(TYPE_COLUMN, TYPE_PNC);
        query.put(IS_CLOSED_COLUMN, NOT_CLOSED);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            return result.size();
        }
        return 0;
    }

    public Mother findOpenCaseByCaseID(String caseId) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(ID_COLUMN, caseId);//TODO: potential caveat, the id column isn't initialized most probably; probably use document id
        query.put(IS_CLOSED_COLUMN, NOT_CLOSED);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Mother mother = Mother.fromRevision(brev);
                    if (mother != null) {
                        return  mother;
                    }
                }
            }
        }
        return null;

    }

    public List<Mother> findAllCasesForEC(String ecCaseId) {
        List<Mother> mothers = new ArrayList<Mother>();
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(EC_CASEID_COLUMN, ecCaseId);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Mother mother = Mother.fromRevision(brev);
                    if (mother != null) {
                        mothers.add(mother);
                    }
                }
            }
        }
        return mothers;

    }

    public List<Mother> findByCaseIds(String... caseIds) {
        Map<String, Object> query = new HashMap<String, Object>();
        List<Object> qList = new ArrayList<Object>();
        for(String str : caseIds){
            Map<String, Object> eqClause = new HashMap<String, Object>();
            Map<String, Object> orClause = new HashMap<String, Object>();
            eqClause.put("$eq", str);
            query.put(ID_COLUMN, eqClause);
            qList.add(orClause);
        }
        query.put("$or", qList);

        List<Mother> mothers = new ArrayList<Mother>();

        QueryResult result = mIndexManager.find(query);
        if(result != null){
            for (DocumentRevision rev : result) {
                if(rev instanceof BasicDocumentRevision){
                    BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                    Mother mother = Mother.fromRevision(brev);
                    if (mother != null) {
                        mothers.add(mother);
                    }
                }
            }
        }
        return mothers;
    }

    //TODO:
    public List<Pair<Mother, EligibleCouple>> allMothersOfATypeWithEC(String type) {
//        SQLiteDatabase database = masterRepository.getReadableDatabase();
//        Cursor cursor = database.rawQuery("SELECT " + tableColumnsForQuery(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS) + ", " + tableColumnsForQuery(EC_TABLE_NAME, EC_TABLE_COLUMNS) +
//                " FROM " + MOTHER_TABLE_NAME + ", " + EC_TABLE_NAME +
//                " WHERE " + TYPE_COLUMN + "='" + type +
//                "' AND " + MOTHER_TABLE_NAME + "." + IS_CLOSED_COLUMN + "= '" + NOT_CLOSED + "' AND " +
//                MOTHER_TABLE_NAME + "." + EC_CASEID_COLUMN + " = " + EC_TABLE_NAME + "." + EligibleCoupleRepository.ID_COLUMN, null);
//        return readAllMothersWithEC(cursor);

        return null;
    }

    public void closeAllCasesForEC(String ecCaseId) {
        List<Mother> mothers = findAllCasesForEC(ecCaseId);
        for (Mother mother : mothers) {
            close(mother.caseId());
        }
    }

    public void close(String caseId) {
        try {
            Map<String, Object> query = new HashMap<String, Object>();
            query.put(ID_COLUMN, caseId);//TODO: potential caveat, the id column isn't initialized most probably; probably use document id
            QueryResult result = mIndexManager.find(query);
            if(result != null){
                for (DocumentRevision rev : result) {
                    if(rev instanceof BasicDocumentRevision){
                        BasicDocumentRevision brev = (BasicDocumentRevision)rev;
                        Mother mother = Mother.fromRevision(brev);
                        if (mother != null) {
                            mother.setIsClosed(true);
                            updateDocument(mother);
                        }
                    }
                }
            }
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    private Map<String,Object> createValuesFor(Mother mother, String type) {
        Map<String,Object> values = new HashMap<String,Object>();
        values.put(ID_COLUMN, mother.caseId());
        values.put(EC_CASEID_COLUMN, mother.ecCaseId());
        values.put(THAYI_CARD_NUMBER_COLUMN, mother.thayiCardNumber());
        values.put(TYPE_COLUMN, type);
        values.put(REF_DATE_COLUMN, mother.referenceDate());
        values.put(DETAILS_COLUMN, new Gson().toJson(mother.details()));
        values.put(IS_CLOSED_COLUMN, Boolean.toString(mother.isClosed()));
        return values;
    }

    private List<Mother> readAll(Cursor cursor) {
        cursor.moveToFirst();
        List<Mother> mothers = new ArrayList<Mother>();
        while (!cursor.isAfterLast()) {
            Map<String, String> details = new Gson().fromJson(cursor.getString(5), new TypeToken<Map<String, String>>() {
            }.getType());

            mothers.add(new Mother(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(4))
                    .withDetails(details)
                    .setIsClosed(Boolean.valueOf(cursor.getString(6)))
                    .withType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN))));
            cursor.moveToNext();
        }
        cursor.close();
        return mothers;
    }

    private List<Pair<Mother, EligibleCouple>> readAllMothersWithEC(Cursor cursor) {
        cursor.moveToFirst();
        List<Pair<Mother, EligibleCouple>> ancsWithEC = new ArrayList<Pair<Mother, EligibleCouple>>();
        while (!cursor.isAfterLast()) {
            Mother mother = new Mother(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(4))
                    .withType(cursor.getString(cursor.getColumnIndex(TYPE_COLUMN)))
                    .withDetails(new Gson().<Map<String, String>>fromJson(cursor.getString(5), new TypeToken<Map<String, String>>() {
                    }.getType()));
            EligibleCouple eligibleCouple = new EligibleCouple(cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12),
                    new Gson().<Map<String, String>>fromJson(cursor.getString(14), new TypeToken<Map<String, String>>() {
                    }.getType())).withPhotoPath(cursor.getString(cursor.getColumnIndex(EligibleCoupleRepository.PHOTO_PATH_COLUMN)));
            if (Boolean.valueOf(cursor.getString(cursor.getColumnIndex(IS_OUT_OF_AREA_COLUMN)))) {
                eligibleCouple.asOutOfArea();
            }

            ancsWithEC.add(Pair.of(mother, eligibleCouple));
            cursor.moveToNext();
        }
        cursor.close();
        return ancsWithEC;
    }

    private String tableColumnsForQuery(String tableName, String[] tableColumns) {
        return join(prepend(tableColumns, tableName + "."), ", ");
    }

    private String[] prepend(String[] input, String textToPrepend) {
        int length = input.length;
        String[] output = new String[length];
        for (int index = 0; index < length; index++) {
            output[index] = textToPrepend + input[index];
        }
        return output;
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }


    public Mother findMotherWithOpenStatusByECId(String ecId) {
//        SQLiteDatabase database = masterRepository.getReadableDatabase();
//        Cursor cursor = database.query(MOTHER_TABLE_NAME, MOTHER_TABLE_COLUMNS, EC_CASEID_COLUMN + " = ? AND " + IS_CLOSED_COLUMN + " = ?", new String[]{ecId, NOT_CLOSED}, null, null, null, null);
//        List<Mother> mothers = readAll(cursor);
//        return mothers.isEmpty() ? null : mothers.get(0);
        return null;
    }

    public boolean isPregnant(String ecId) {
        Map<String, Object> query = new HashMap<String, Object>();
        query.put(EC_CASEID_COLUMN, ecId);
        query.put(TYPE_COLUMN, TYPE_PNC);
        query.put(IS_CLOSED_COLUMN, NOT_CLOSED);
        QueryResult result = mIndexManager.find(query);
        if(result != null){
            return result.size() > 0;
        }
        return false;
    }

    public void update(Mother mother) {
        try {
            updateDocument(mother);
        } catch (ConflictException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates an Mother document within the datastore.
     * @param mother FormDefinitionVersion to update
     * @return the updated revision of the Mother
     * @throws ConflictException if the formDefinitionVersion passed in has a rev which doesn't
     *      match the current rev in the datastore.
     */
    public Mother updateDocument(Mother mother) throws ConflictException {
        MutableDocumentRevision rev = mother.getDocumentRevision().mutableCopy();
        rev.body = DocumentBodyFactory.create(mother.asMap());
        try {
            BasicDocumentRevision updated = this.mDatastore.updateDocumentFromRevision(rev);
            return Mother.fromRevision(updated);
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
        return mContext.getString(R.string.mother_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }
}
