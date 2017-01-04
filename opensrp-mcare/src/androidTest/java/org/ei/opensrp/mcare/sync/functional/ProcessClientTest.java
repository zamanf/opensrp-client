package org.ei.opensrp.mcare.sync.functional;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.apache.commons.lang3.time.DateUtils;
import org.ei.opensrp.clientandeventmodel.Address;
import org.ei.opensrp.clientandeventmodel.Client;
import org.ei.opensrp.clientandeventmodel.DateUtil;
import org.ei.opensrp.clientandeventmodel.Event;
import org.ei.opensrp.clientandeventmodel.Obs;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.commonregistry.CommonRepository;
import org.ei.opensrp.mcare.sync.BaseClientProcessorTest;
import org.ei.opensrp.repository.AllSharedPreferences;
import org.ei.opensrp.sync.ClientProcessor;
import org.ei.opensrp.sync.CloudantDataHandler;
import org.ei.opensrp.util.Session;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keyman on 16/12/2016.
 */
public class ProcessClientTest extends BaseClientProcessorTest {
    AllSharedPreferences allSharedPreferences;
    CloudantDataHandler cloudantDataHandler;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        org.ei.opensrp.Context context = org.ei.opensrp.Context.getInstance();
        context.updateApplicationContext(getContext());

        Session session = context.session();
        session.setPassword("dummyPass");

        // Trigger initRepository method call
        context.formSubmissionService();

        allSharedPreferences = context.allSharedPreferences();

        cloudantDataHandler = CloudantDataHandler.getInstance(getContext());

    }

    private void beforeTest() {
        try {
            allSharedPreferences.removeLastSyncDate();

            SQLiteDatabase db = cloudantDataHandler.loadDatabase();
            db.delete("revs", null, null);
            db.delete("docs", null, null);

            trunctateECTable("ec_household");
            trunctateECTable("ec_elco");
            trunctateECTable("ec_mcaremother");
            trunctateECTable("ec_pnc");
            trunctateECTable("ec_mcarechild");

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    @Test
    public void testProcessClientWhenThereAreNoRecordsInCloudant() {
        beforeTest();

        try {

            long lastSyncTimeStamp = allSharedPreferences.fetchLastSyncDate(0);
            assertEquals(0l, lastSyncTimeStamp);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClient();
            assertFalse("No processing should occur", processed);

            lastSyncTimeStamp = allSharedPreferences.fetchLastSyncDate(0);
            assertEquals(0l, lastSyncTimeStamp);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testProcessClientForNewHouseHold() {
        beforeTest();

        String baseEntityId = "TEST12345";

        CommonRepository cr = org.ei.opensrp.Context.getInstance().commonrepository("ec_household");

        try {

            Date now = new Date();
            String firstName = "Tester1";
            String jivhhId = "1324";
            String gobhhId = "1234";

            CommonPersonObject commonPersonObject = cr.findByCaseID(baseEntityId);

            assertNull("householdObject should be null", commonPersonObject);

            createECHousehold(baseEntityId, firstName, jivhhId, gobhhId, now);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClient();
            assertTrue("Processing should occur", processed);

            commonPersonObject = cr.findByCaseID(baseEntityId);
            assertNotNull("householdObject should not be null", commonPersonObject);

            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals(firstName, commonPersonObject.getColumnmaps().get("FWHOHFNAME"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWNHREGDATE"));
            assertEquals(jivhhId, commonPersonObject.getColumnmaps().get("FWJIVHHID"));
            assertEquals(baseEntityId, commonPersonObject.getColumnmaps().get("base_entity_id"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWCENDATE"));
            assertEquals(gobhhId, commonPersonObject.getColumnmaps().get("FWGOBHHID"));


        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            cr.delete(null, null);
        }
    }

    @Test
    public void testProcessClientForNewWomanRegistration() {
        beforeTest();

        String relationalId = "TEST12346";
        String baseEntityId = "TEST12347";

        CommonRepository householdCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_household");
        CommonRepository elcoCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_elco");

        try {

            Date now = new Date();
            String firstName = "Tester2";
            String jivhhId = "1432";
            String gobhhId = "1333";
            String nId = "1112221112221";
            String hus = "Tester Hus";
            String age = "24";

            CommonPersonObject commonPersonObject = householdCr.findByCaseID(relationalId);
            assertNull("householdObject should be null", commonPersonObject);

            commonPersonObject = elcoCr.findByCaseID(baseEntityId);
            assertNull("elcoObject should be null", commonPersonObject);

            createECHousehold(relationalId, firstName, jivhhId, gobhhId, now);
            createECWoman(baseEntityId, firstName, nId, hus, age, jivhhId, gobhhId, now, relationalId);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClient();
            assertTrue("Processing should occur", processed);

            commonPersonObject = householdCr.findByCaseID(relationalId);
            assertNotNull("houseHoldObject should not be null", commonPersonObject);

            // household
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals(firstName, commonPersonObject.getColumnmaps().get("FWHOHFNAME"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWNHREGDATE"));
            assertEquals(jivhhId, commonPersonObject.getColumnmaps().get("FWJIVHHID"));
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("base_entity_id"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWCENDATE"));
            assertEquals(gobhhId, commonPersonObject.getColumnmaps().get("FWGOBHHID"));

            // elco
            commonPersonObject = elcoCr.findByCaseID(baseEntityId);
            assertNotNull("elcoObject should not be null", commonPersonObject);

            assertEquals((short) 0, commonPersonObject.getClosed());
            assertTrue("Dates must be on the same day", DateUtils.isSameDay(now, DateUtil.yyyyMMddTHHmmssSSSZ.parse(commonPersonObject.getColumnmaps().get("WomanREGDATE"))));
            assertEquals(gobhhId, commonPersonObject.getColumnmaps().get("GOBHHID"));
            assertEquals(baseEntityId, commonPersonObject.getColumnmaps().get("base_entity_id"));
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("relational_id"));
            assertEquals(jivhhId, commonPersonObject.getColumnmaps().get("JiVitAHHID"));
            assertEquals(hus, commonPersonObject.getColumnmaps().get("FWHUSNAME"));
            assertEquals(firstName, commonPersonObject.getColumnmaps().get("FWWOMFNAME"));
            assertEquals(nId, commonPersonObject.getColumnmaps().get("FWWOMNID"));
            assertEquals(age, commonPersonObject.getColumnmaps().get("FWWOMAGE"));


        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            householdCr.delete(null, null);
            elcoCr.delete(null, null);
        }
    }

    @Test
    public void testProcessClientForANC() {
        beforeTest();

        String relationalId = "TEST12348";
        String baseEntityId = "TEST12349";

        CommonRepository householdCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_household");
        CommonRepository elcoCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_elco");
        CommonRepository ancCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_mcaremother");

        try {

            Date now = new Date();
            String firstName = "Tester3";
            String jivhhId = "4321";
            String gobhhId = "1332";
            String nId = "1003220012221";
            String hus = "Tester Husb";
            String age = "28";

            CommonPersonObject commonPersonObject = householdCr.findByCaseID(relationalId);
            assertNull("householdObject should be null", commonPersonObject);

            commonPersonObject = elcoCr.findByCaseID(baseEntityId);
            assertNull("elcoObject should be null", commonPersonObject);

            createECHousehold(relationalId, firstName, jivhhId, gobhhId, now);
            createECWoman(baseEntityId, firstName, nId, hus, age, jivhhId, gobhhId, now, relationalId);
            createECAnc(baseEntityId, now);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClient();
            assertTrue("Processing should occur", processed);

            commonPersonObject = householdCr.findByCaseID(relationalId);
            assertNotNull("householdObject should not be null", commonPersonObject);

            // household
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals(firstName, commonPersonObject.getColumnmaps().get("FWHOHFNAME"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWNHREGDATE"));
            assertEquals(jivhhId, commonPersonObject.getColumnmaps().get("FWJIVHHID"));
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("base_entity_id"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWCENDATE"));
            assertEquals(gobhhId, commonPersonObject.getColumnmaps().get("FWGOBHHID"));

            // elco
            commonPersonObject = elcoCr.findByCaseID(baseEntityId);
            assertNotNull("elcoObject should not be null", commonPersonObject);
            assertEquals((short) 1, commonPersonObject.getClosed());
            assertEquals(baseEntityId, commonPersonObject.getColumnmaps().get("base_entity_id"));

            // anc
            commonPersonObject = ancCr.findByCaseID(baseEntityId);
            assertNotNull("Object should not be null", commonPersonObject);
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals("0", commonPersonObject.getColumnmaps().get("FWHR_PSR"));
            assertEquals(DateUtil.yyyyMMdd.format(minusThreeMonths(now)), commonPersonObject.getColumnmaps().get("FWPSRLMP"));
            assertEquals("0", commonPersonObject.getColumnmaps().get("FWHRP"));
            assertEquals(baseEntityId, commonPersonObject.getColumnmaps().get("base_entity_id"));
            assertEquals("1", commonPersonObject.getColumnmaps().get("FWVG"));


        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            householdCr.delete(null, null);
            elcoCr.delete(null, null);
            ancCr.delete(null, null);
        }
    }

    @Test
    public void testProcessClientForPNC() {
        beforeTest();

        String relationalId = "TEST12350";
        String baseEntityId = "TEST12351";

        CommonRepository householdCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_household");
        CommonRepository elcoCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_elco");
        CommonRepository ancCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_mcaremother");
        CommonRepository pncCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_pnc");

        try {

            Date now = new Date();
            String firstName = "Tester4";
            String jivhhId = "4332";
            String gobhhId = "4332";
            String nId = "1001230017771";
            String hus = "Tester Husba";
            String age = "22";

            CommonPersonObject commonPersonObject = householdCr.findByCaseID(relationalId);
            assertNull("householdObject should be null", commonPersonObject);

            commonPersonObject = elcoCr.findByCaseID(baseEntityId);
            assertNull("elcoObject should be null", commonPersonObject);

            createECHousehold(relationalId, firstName, jivhhId, gobhhId, now);
            createECWoman(baseEntityId, firstName, nId, hus, age, jivhhId, gobhhId, now, relationalId);
            createECAnc(baseEntityId, now);
            createECPnc(baseEntityId, now);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClient();
            assertTrue("Processing should occur", processed);

            commonPersonObject = householdCr.findByCaseID(relationalId);
            assertNotNull("householdObject should not be null", commonPersonObject);

            // household
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals(firstName, commonPersonObject.getColumnmaps().get("FWHOHFNAME"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWNHREGDATE"));
            assertEquals(jivhhId, commonPersonObject.getColumnmaps().get("FWJIVHHID"));
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("base_entity_id"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWCENDATE"));
            assertEquals(gobhhId, commonPersonObject.getColumnmaps().get("FWGOBHHID"));

            // elco
            commonPersonObject = elcoCr.findByCaseID(baseEntityId);
            assertNotNull("elcoObject should not be null", commonPersonObject);
            assertEquals((short) 1, commonPersonObject.getClosed());
            assertEquals(baseEntityId, commonPersonObject.getColumnmaps().get("base_entity_id"));

            // anc
            commonPersonObject = ancCr.findByCaseID(baseEntityId);
            assertNotNull("ancObject should not be null", commonPersonObject);
            assertEquals((short) 1, commonPersonObject.getClosed());
            assertEquals(baseEntityId, commonPersonObject.getColumnmaps().get("base_entity_id"));

            // pnc
            commonPersonObject = pncCr.findByCaseID(baseEntityId);
            assertNotNull("pncObject should not be null", commonPersonObject);
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals(DateUtil.yyyyMMddHHmmss.format(now), commonPersonObject.getColumnmaps().get("FWBNFDTOO"));
            assertEquals(nId, commonPersonObject.getColumnmaps().get("FWWOMNID"));
            assertEquals("3", commonPersonObject.getColumnmaps().get("FWBNFSTS"));
            assertEquals(baseEntityId, commonPersonObject.getColumnmaps().get("base_entity_id"));


        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            householdCr.delete(null, null);
            elcoCr.delete(null, null);
            ancCr.delete(null, null);
            pncCr.delete(null, null);
        }
    }

    @Test
    public void testProcessClientForChild() {
        beforeTest();

        String hhId = "TEST12352";
        String relationalId = "TEST12353";
        String baseEntityId = "TEST12354";

        CommonRepository householdCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_household");
        CommonRepository elcoCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_elco");
        CommonRepository ancCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_mcaremother");
        CommonRepository pncCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_pnc");
        CommonRepository childCr = org.ei.opensrp.Context.getInstance().commonrepository("ec_mcarechild");

        try {

            Date now = new Date();
            String firstName = "Tester4";
            String jivhhId = "4332";
            String gobhhId = "4332";
            String nId = "1001230017771";
            String hus = "Tester Husba";
            String age = "22";
            String childName = "Test Child";

            CommonPersonObject commonPersonObject = pncCr.findByCaseID(relationalId);
            assertNull("pncObject should be null", commonPersonObject);

            commonPersonObject = childCr.findByCaseID(baseEntityId);
            assertNull("childObject should be null", commonPersonObject);

            createECHousehold(hhId, firstName, jivhhId, gobhhId, now);
            createECWoman(relationalId, firstName, nId, hus, age, jivhhId, gobhhId, now, hhId);
            createECAnc(relationalId, now);
            createECPnc(relationalId, now);
            createECChild(baseEntityId, childName, now, relationalId);

            Boolean processed = ClientProcessor.getInstance(getContext()).processClient();
            assertTrue("Processing should occur", processed);

            commonPersonObject = householdCr.findByCaseID(hhId);
            assertNotNull("householdObject should not be null", commonPersonObject);

            // household
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals(firstName, commonPersonObject.getColumnmaps().get("FWHOHFNAME"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWNHREGDATE"));
            assertEquals(jivhhId, commonPersonObject.getColumnmaps().get("FWJIVHHID"));
            assertEquals(hhId, commonPersonObject.getColumnmaps().get("base_entity_id"));
            assertEquals(DateUtil.yyyyMMdd.format(now), commonPersonObject.getColumnmaps().get("FWCENDATE"));
            assertEquals(gobhhId, commonPersonObject.getColumnmaps().get("FWGOBHHID"));

            // elco
            commonPersonObject = elcoCr.findByCaseID(relationalId);
            assertNotNull("elcoObject should not be null", commonPersonObject);
            assertEquals((short) 1, commonPersonObject.getClosed());
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("base_entity_id"));

            // anc
            commonPersonObject = ancCr.findByCaseID(relationalId);
            assertNotNull("ancObject should not be null", commonPersonObject);
            assertEquals((short) 1, commonPersonObject.getClosed());
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("base_entity_id"));

            // pnc
            commonPersonObject = pncCr.findByCaseID(relationalId);
            assertNotNull("pncObject should not be null", commonPersonObject);
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("base_entity_id"));

            // chikd
            commonPersonObject = childCr.findByCaseID(baseEntityId);
            assertNotNull("childObject should not be null", commonPersonObject);
            assertEquals((short) 0, commonPersonObject.getClosed());
            assertEquals("1900-01-01T00:00:00.000+0300", commonPersonObject.getColumnmaps().get("FWBNFDTOO"));
            assertEquals("1", commonPersonObject.getColumnmaps().get("FWBNFGEN"));
            assertEquals("1", commonPersonObject.getColumnmaps().get("FWBNFCHLDVITSTS"));
            assertEquals(relationalId, commonPersonObject.getColumnmaps().get("relational_id"));

        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            householdCr.delete(null, null);
            elcoCr.delete(null, null);
            ancCr.delete(null, null);
            pncCr.delete(null, null);
            childCr.delete(null, null);
        }
    }


    private Event createEvent(String baseEntityId, Date encounterDate, String eventType, String locationId, String providerId, String entityType, String formSubmissionId, Obs... observations) {
        Event e = (Event) new Event()
                .withBaseEntityId(baseEntityId)
                .withEventDate(encounterDate)
                .withEventType(eventType)
                .withLocationId(locationId)
                .withProviderId(providerId)
                .withEntityType(entityType)
                .withFormSubmissionId(formSubmissionId)
                .withDateCreated(new Date());
        if (observations != null) {
            for (Obs obs : observations) {
                e.addObs(obs);
            }
        }
        return e;
    }

    private Client createClient(String baseEntityId, String firstName, String middleName, String lastName, DateTime birthdate, boolean birthdateApprox, DateTime deathdate, boolean deathdateApprox, String gender, List<Address> addresses, Map<String, Object> attributes, Map<String, String> identifiers, Map<String, List<String>> relationships) {
        Client c = (Client) new Client(baseEntityId)
                .withFirstName(firstName)
                .withMiddleName(middleName)
                .withLastName(lastName)
                .withBirthdate((birthdate != null ? birthdate.toDate() : null), birthdateApprox)
                .withDeathdate(deathdate != null ? deathdate.toDate() : null, deathdateApprox)
                .withGender(gender).withDateCreated(new Date());

        c.withAddresses(addresses)
                .withAttributes(attributes);

        if (identifiers != null) {
            c.withIdentifiers(identifiers);
        }

        if (relationships != null) {
            c.setRelationships(relationships);
        }
        return c;
    }

    private List<Object> addToList(Object... o) {
        List<Object> l = new ArrayList<>();
        for (Object object : o) {
            l.add(object);
        }
        return l;
    }

    private List<String> addToStringList(String... o) {
        List<String> l = new ArrayList<>();
        for (String string : o) {
            l.add(string);
        }
        return l;
    }


    private void trunctateECTable(String tableName) {
        CommonRepository cr = org.ei.opensrp.Context.getInstance().commonrepository(tableName);
        cr.delete(null, null);
    }

    private void createECHousehold(String baseEntityId, String firstName, String jivhhId, String gobhhId, Date now) {
        try {
            Obs obsStart = new Obs("concept", "start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMddHHmmss.format(now)), new ArrayList<>(), null, "start");

            Obs obsEnd = new Obs("concept", "end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMddHHmmss.format(now)), new ArrayList<>(), null, "end");

            Obs obsDate = new Obs("concept", "date", "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMdd.format(now)), new ArrayList<>(), null, "FWNHREGDATE");

            Obs obsText = new Obs("concept", "text", "5611AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("03"), new ArrayList<>(), null, "FWNHHMBRNUM");

            Obs obsFwa = new Obs("concept", "calculate", "163229AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("FWA"), new ArrayList<>(), null, "user_type");

            Event e = createEvent(baseEntityId, now, "New Household Registration", "TEST54321", "tester", "household", baseEntityId, obsStart, obsEnd, obsDate, obsText, obsFwa);
            cloudantDataHandler.createEventDocument(new org.ei.opensrp.cloudant.models.Event(e));

            createECClient(baseEntityId, firstName, "1", jivhhId, gobhhId, null, null);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    private void createECWoman(String baseEntityId, String firstName, String nid, String hus, String age, String jivhhId, String gobhhId, Date now, String relationalId) {
        try {
            Obs obsAge = new Obs("concept", "calculate", "1532AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(age), new ArrayList<>(), null, "FWWOMAGE");

            Obs obsSel1 = new Obs("concept", "select one", "160600AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), new ArrayList<>(), null, "FWCWOMSTRMEN");

            Obs obsSel2 = new Obs("concept", "select one", "162994AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), new ArrayList<>(), null, "FWCWOMHUSLIV");

            Obs obsSel3 = new Obs("concept", "select one", "162959AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), new ArrayList<>(), null, "FWCWOMHUSSTR");

            Obs obsEli = new Obs("concept", "calculate", "162699AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), new ArrayList<>(), null, "FWELIGIBLE2");

            Obs obsSelAll = new Obs("concept", "select all that apply", "163087AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("163084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), new ArrayList<>(), null, "FWWOMANYID");

            Obs obsId = new Obs("concept", "calculate", "163084AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(nid), new ArrayList<>(), null, "FWWOMRETYPENID_CONCEPT");

            Obs obsText = new Obs("concept", "text", "161135AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(hus), new ArrayList<>(), null, "FWHUSNAME");

            Event e = createEvent(baseEntityId, now, "New Woman Registration", "TEST54321", "tester", "household", baseEntityId, obsAge, obsSel1, obsSel2, obsSel3, obsEli, obsSelAll, obsId, obsText);
            cloudantDataHandler.createEventDocument(new org.ei.opensrp.cloudant.models.Event(e));


            Map<String, String> identifiers = new HashMap<>();
            identifiers.put("NID", nid);

            Map<String, List<String>> relationships = new HashMap<>();
            relationships.put("householdHead", addToStringList(relationalId));

            createECClient(baseEntityId, firstName, "2", jivhhId, gobhhId, identifiers, relationships);

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    private void createECAnc(String baseEntityId, Date now) {
        try {
            Obs obsStart = new Obs("concept", "start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMddHHmmss.format(now)), new ArrayList<>(), null, "start");

            Obs obsEnd = new Obs("concept", "end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMddHHmmss.format(now)), new ArrayList<>(), null, "end");

            Obs obsDate = new Obs("concept", "date", "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMdd.format(now)), new ArrayList<>(), null, "FWPSRDATE");

            Obs obsSel1 = new Obs("concept", "select one", "163088AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("162961AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("01"), null, "FWPSRSTS");

            Obs obsCal1 = new Obs("concept", "calculate", "163229AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("FWA"), new ArrayList<>(), null, "user_type");

            Obs obsDate1 = new Obs("concept", "date", "1427AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMdd.format(minusThreeMonths(now))), new ArrayList<>(), null, "FWPSRLMP");

            Obs obsSel2 = new Obs("concept", "select one", "162942AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("1"), null, "FWPSRPREGSTS");

            Obs obsSel3 = new Obs("concept", "select one", "163085AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("122933AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("1"), null, "FWPSRPREGWTD");

            Obs obsSel4 = new Obs("concept", "select one", "163086AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("122933AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("1"), null, "FWPSRHUSPREGWTD");

            Obs obsSel5 = new Obs("concept", "select one", "162970AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1065AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("1"), null, "FWPSREVRPREG");

            Obs obsSel6 = new Obs("concept", "select one", "162956AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("0"), null, "FWPSRVDGMEM");

            Obs obsText1 = new Obs("concept", "text", "1545AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("01"), new ArrayList<>(), null, "FWPSRWOMEDU");

            Obs obsSel7 = new Obs("concept", "select one", "159741AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("159577AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("4"), null, "FWPSRHHLAT");

            Obs obsText2 = new Obs("concept", "text", "162962AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("90"), new ArrayList<>(), null, "FWPSRHHRICE");

            Obs obsSel8 = new Obs("concept", "select one", "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "148834AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", addToList("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("0"), null, "FWPSRANM");

            Obs obsSel9 = new Obs("concept", "select one", "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "113859AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", addToList("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("0"), null, "FWPSRHBP");

            Obs obsSel10 = new Obs("concept", "select one", "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "119477AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", addToList("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("0"), null, "FWPSRDBT");

            Obs obsSel11 = new Obs("concept", "select one", "1729AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", "124719AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", addToList("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("0"), null, "FWPSRTHY");

            Obs obsDec = new Obs("concept", "decimal", "1343AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("99"), new ArrayList<>(), null, "FWPSRMUAC");

            Obs obsCal2 = new Obs("concept", "calculate", "163090AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1"), new ArrayList<>(), null, "FWVG");

            Obs obsCal3 = new Obs("concept", "calculate", "163092AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("0"), new ArrayList<>(), null, "FWHRP");

            Obs obsCal4 = new Obs("concept", "calculate", "163091AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("0"), new ArrayList<>(), null, "FWHR_PSR");

            Obs obsCal5 = new Obs("concept", "calculate", "163093AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1"), new ArrayList<>(), null, "FWFLAGVALUE");

            Obs obsSel12 = new Obs("concept", "select one", "163089AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1066AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("0"), null, "FWPSRPHONE");


            Event e = createEvent(baseEntityId, now, "Pregnancy Surveillance and Registration", "TEST54321", "tester", "elco", baseEntityId, obsStart, obsEnd, obsDate, obsSel1, obsCal1, obsDate1, obsSel2, obsSel3, obsSel4, obsSel5, obsSel6, obsText1, obsSel7, obsText2, obsSel8, obsSel9, obsSel10, obsSel11, obsDec, obsCal2, obsCal3, obsCal4, obsCal5, obsSel12);
            cloudantDataHandler.createEventDocument(new org.ei.opensrp.cloudant.models.Event(e));


        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    private void createECPnc(String baseEntityId, Date now) {
        try {
            Obs obsStart = new Obs("concept", "start", "163137AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMddHHmmss.format(now)), new ArrayList<>(), null, "start");

            Obs obsEnd = new Obs("concept", "end", "163138AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMddHHmmss.format(now)), new ArrayList<>(), null, "end");

            Obs obsDate = new Obs("concept", "date", "160753AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMdd.format(now)), new ArrayList<>(), null, "FWBNFDATE");

            Obs obsCal1 = new Obs("concept", "calculate", "1438AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("13"), new ArrayList<>(), null, "FWGESTATIONALAGE");

            Obs obsCal2 = new Obs("concept", "calculate", "5596AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMdd.format(plusFiveMonths(now))), new ArrayList<>(), null, "FWEDD");

            Obs obsSel1 = new Obs("concept", "select one", "161641AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("151849AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("3"), null, "FWBNFSTS");

            Obs obsHid = new Obs("concept", "hidden", "163229AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("FWA"), new ArrayList<>(), null, "user_type");

            Obs obsSel2 = new Obs("concept", "select one", "1856AAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("160429AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("1"), null, "FWBNFWOMVITSTS");

            Obs obsDt = new Obs("concept", "dateTime", "5599AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(DateUtil.yyyyMMddHHmmss.format(now)), new ArrayList<>(), null, "FWBNFDTOO");

            Obs obsInt = new Obs("concept", "integer", "160601AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("2"), new ArrayList<>(), null, "FWBNFLB");


            Event e = createEvent(baseEntityId, now, "Birth Notification Followup form", "TEST54321", "tester", "mcaremother", baseEntityId, obsStart, obsEnd, obsDate, obsCal1, obsCal2, obsSel1, obsHid, obsSel2, obsDt, obsInt);
            cloudantDataHandler.createEventDocument(new org.ei.opensrp.cloudant.models.Event(e));


        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    private void createECChild(String baseEntityId, String childName, Date now, String relationalId) {
        try {
            Obs obsSel1 = new Obs("concept", "select one", "1587AAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("1534AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("1"), null, "FWBNFGEN");

            Obs obsSel2 = new Obs("concept", "select one", "159926AAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList("160429AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"), addToList("1"), null, "FWBNFCHLDVITSTS");

            Obs obsNull = new Obs("concept", "null", "1586AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, addToList(childName), new ArrayList<>(), null, "FWBNFCHILDNAME");

            Event e = createEvent(baseEntityId, now, "Child Vital Status", "TEST54321", "tester", "mcaremother", baseEntityId, obsSel1, obsSel2, obsNull);
            cloudantDataHandler.createEventDocument(new org.ei.opensrp.cloudant.models.Event(e));


            Map<String, List<String>> relationships = new HashMap<>();
            relationships.put("mother", addToStringList(relationalId));

            createECClient(baseEntityId, null, null, null, null, null, relationships);

        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    private void createECClient(String baseEntityId, String firstName, String gender, String jivhhId, String gobhhId, Map<String, String> identifiers, Map<String, List<String>> relationships) {
        try {
            Map<String, String> addressFields = new HashMap<>();
            addressFields.put("address1", "RAMJIBAN");
            Address address = new Address("usual_residence", null, null, addressFields, null, null, null, "RANGPUR", "Bangladesh");
            address.setCityVillage("SUNDARGANJ");
            address.setCountyDistrict("GAIBANDHA");

            List<Address> addresses = new ArrayList<>();
            addresses.add(address);

            Map<String, Object> attributes = new HashMap<>();
            attributes.put("GoB_HHID", gobhhId);
            attributes.put("JiVitA_HHID", jivhhId);

            Client c = createClient(baseEntityId, firstName, null, ".", new DateTime(DateUtil.parseDate("1900-01-01T00:32:44.000+0300")), false, null, false, gender, addresses, attributes, identifiers, relationships);
            cloudantDataHandler.createClientDocument(new org.ei.opensrp.cloudant.models.Client(c));
        } catch (Exception e) {
            Log.e(getClass().getName(), "Exception", e);
        }
    }

    private Date minusThreeMonths(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -3);
        return c.getTime();
    }

    private Date plusFiveMonths(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, 5);
        return c.getTime();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


}
