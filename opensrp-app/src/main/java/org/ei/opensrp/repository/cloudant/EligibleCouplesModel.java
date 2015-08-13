package org.ei.opensrp.repository.cloudant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.cloudant.sync.datastore.BasicDocumentRevision;
import com.cloudant.sync.datastore.DocumentBodyFactory;
import com.cloudant.sync.datastore.DocumentException;
import com.cloudant.sync.datastore.MutableDocumentRevision;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import net.sqlcipher.database.SQLiteDatabase;

import org.ei.opensrp.AllConstants;
import org.ei.opensrp.R;
import org.ei.opensrp.domain.Child;
import org.ei.opensrp.domain.EligibleCouple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;
import static java.text.MessageFormat.format;
import static net.sqlcipher.DatabaseUtils.longForQuery;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.repeat;

/**
 * Created by Geoffrey Koros on 8/7/2015.
 */
public class EligibleCouplesModel extends BaseItemsModel{

    public static final String EC_TABLE_NAME = "eligible_couple";

    public static final String ID_COLUMN = "id";
    public static final String EC_NUMBER_COLUMN = "ecNumber";
    public static final String WIFE_NAME_COLUMN = "wifeName";
    public static final String HUSBAND_NAME_COLUMN = "husbandName";
    public static final String VILLAGE_NAME_COLUMN = "village";
    public static final String SUBCENTER_NAME_COLUMN = "subCenter";
    public static final String IS_OUT_OF_AREA_COLUMN = "isOutOfArea";
    public static final String DETAILS_COLUMN = "details";
    private static final String IS_CLOSED_COLUMN = "isClosed";
    public static final String PHOTO_PATH_COLUMN = "photoPath";

    public static final String NOT_CLOSED = "false";
    private static final String IN_AREA = "false";

    public EligibleCouplesModel(Context context) {
        super(context, EC_TABLE_NAME);
        //setup the indexManeger
        if(mIndexManager != null) {
            if (mIndexManager.isTextSearchEnabled()) {
                // Create an index over the searchable text fields
                String name = mIndexManager.ensureIndexed(Arrays.<Object>asList(ID_COLUMN, EC_NUMBER_COLUMN, WIFE_NAME_COLUMN,
                                HUSBAND_NAME_COLUMN, VILLAGE_NAME_COLUMN, SUBCENTER_NAME_COLUMN, IS_CLOSED_COLUMN),
                        "basic");
                if (name == null) {
                    Log.e(LOG_TAG, "there was an error creating the index");
                }
            } else {
                Log.e(LOG_TAG, "there was an error creating the index");
            }
        }
    }

    public void add(EligibleCouple eligibleCouple) {
        MutableDocumentRevision rev = new MutableDocumentRevision();
        rev.body = DocumentBodyFactory.create(eligibleCouple.asMap());
        try {
            BasicDocumentRevision created = this.mDatastore.createDocumentFromRevision(rev);
            EligibleCouple.fromRevision(created);
        } catch (DocumentException de) {
            Log.e(LOG_TAG, de.toString());
        }
    }

    public void updateDetails(String caseId, Map<String, String> details) {
        //TODO:
    }

    public void mergeDetails(String caseId, Map<String, String> details) {
        //TODO:
    }

    public List<EligibleCouple> allEligibleCouples() {
        int nDocs = this.mDatastore.getDocumentCount();
        List<BasicDocumentRevision> all = this.mDatastore.getAllDocuments(0, nDocs, true);
        List<EligibleCouple> eligibleCouples = new ArrayList<EligibleCouple>();

        // Filter all documents down to those of type Task.
        for(BasicDocumentRevision rev : all) {
            EligibleCouple t = EligibleCouple.fromRevision(rev);
            if (t != null) {
                eligibleCouples.add(t);
            }
        }

        return eligibleCouples;
    }

    public List<EligibleCouple> findByCaseIDs(String... caseIds) {
        //TODO:
        return null;
    }

    public EligibleCouple findByCaseID(String caseId) {
        //TODO
        return null;
    }

    public long count() {
        return this.mDatastore.getDocumentCount();
    }

    public List<String> villages() {
        //TODO:
        return null;
    }

    public void updatePhotoPath(String caseId, String imagePath) {
        //TODO:
    }

    public void close(String caseId) {
        //
    }

    private String insertPlaceholdersForInClause(int length) {
        return repeat("?", ",", length);
    }

    public long fpCount() {
        return  this.mDatastore.getDocumentCount();
    }

    private long getECsUsingFPMethod(List<Map<String, String>> detailsList) {
        long fpCount = 0;
        for (Map<String, String> details : detailsList) {
            if (!(isBlank(details.get(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD)) || "none".equalsIgnoreCase(details.get(AllConstants.ECRegistrationFields.CURRENT_FP_METHOD)))) {
                fpCount++;
            }
        }
        return fpCount;
    }

    private List<Map<String, String>> readDetailsList(Cursor cursor) {
        cursor.moveToFirst();
        List<Map<String, String>> detailsList = new ArrayList<Map<String, String>>();
        while (!cursor.isAfterLast()) {
            String detailsJSON = cursor.getString(0);
            detailsList.add(new Gson().<Map<String, String>>fromJson(detailsJSON, new TypeToken<HashMap<String, String>>() {
            }.getType()));
            cursor.moveToNext();
        }
        cursor.close();
        return detailsList;
    }

    @Override
    public String getCloudantApiKey() {
        return mContext.getString(R.string.default_api_key);
    }

    @Override
    public String getCloudantDatabaseName() {
        return mContext.getString(R.string.eligible_couple_dbname);
    }

    @Override
    public String getCloudantApiSecret() {
        return mContext.getString(R.string.default_api_password);
    }
}
