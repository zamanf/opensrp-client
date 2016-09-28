package org.ei.opensrp.immunization.field;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObject;
import org.ei.opensrp.immunization.R;
import org.ei.opensrp.util.IntegerUtil;
import org.ei.opensrp.util.Utils;
import org.ei.opensrp.view.template.DetailActivity;
import org.ei.opensrp.view.template.DetailFragment;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.ei.opensrp.util.Utils.addAsInts;
import static org.ei.opensrp.util.Utils.fillValue;
import static org.ei.opensrp.util.Utils.getColor;
import static org.ei.opensrp.util.VaccinatorUtils.calculateEndingBalance;
import static org.ei.opensrp.util.VaccinatorUtils.calculateStartingBalance;
import static org.ei.opensrp.util.VaccinatorUtils.calculateWasted;
import static org.ei.opensrp.util.VaccinatorUtils.getTotalUsed;
import static org.ei.opensrp.util.VaccinatorUtils.getWasted;
import static org.ei.opensrp.util.VaccinatorUtils.providerDetails;
import static org.ei.opensrp.util.Utils.addToRow;
import static org.ei.opensrp.util.Utils.getDataRow;
import static org.ei.opensrp.util.Utils.getValue;

public class FieldMonitorMonthlyDetailFragment extends DetailFragment {
    @Override
    protected int layoutResId() {
        return R.layout.field_detail_monthly_activity;
    }

    @Override
    protected String pageTitle() {
        return "Report Detail (Monthly)";
    }

    @Override
    protected String titleBarId() {
        return getValue(providerDetails(), "provider_id", false);
    }

    @Override
    protected Integer profilePicContainerId() { return null; }

    @Override
    protected Integer defaultProfilePicResId() { return null; }

    @Override
    protected String bindType() {
        return "stock";
    }

    @Override
    protected boolean allowImageCapture() {
        return false;
    }

    @Override
    protected void generateView() {
        HashMap provider =  providerDetails();

        TableLayout dt = (TableLayout) currentView.findViewById(R.id.field_detail_info_table1);
        dt.removeAllViews();

        Log.i("ANM", "DETIALS ANM :"+Context.getInstance().anmController().get());

        TableRow tr = getDataRow(getActivity(), "Center", getValue(provider, "provider_location_id", true), null, true);
        dt.addView(tr);
        tr = getDataRow(getActivity(), "UC", getValue(provider, "provider_uc", true), null, true);
        dt.addView(tr);

        TableLayout dt2 = (TableLayout) currentView.findViewById(R.id.field_detail_info_table2);
        dt2.removeAllViews();

        TableRow tr2 = getDataRow(getActivity(), "Monthly Target", getValue(client.getColumnmaps(), "Target_assigned_for_vaccination_at_each_month", false), null, true);
        dt2.addView(tr2);
        tr2 = getDataRow(getActivity(), "Yearly Target", getValue(client.getDetails(), "Target_assigned_for_vaccination_for_the_year", false), null, true);
        dt2.addView(tr2);

        String date_entered = client.getColumnmaps().get("date");

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse(date_entered);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ((TextView)currentView.findViewById(R.id.reporting_period)).setText(new DateTime(date.getTime()).toString("MMMM (yyyy)"));

        final TableLayout tb = (TableLayout) currentView.findViewById(R.id.stock_vaccine_table);
        while (tb.getChildCount() > 2) {
            tb.removeView(tb.getChildAt(tb.getChildCount() - 1));
        }

        int bcgBalanceInHand = Integer.parseInt(getValue(client.getColumnmaps(), "bcg_balance_in_hand", "0", false));
        int bcgReceived = Integer.parseInt(getValue(client.getColumnmaps(), "bcg_received", "0", false));

        int opv_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "opv_balance_in_hand", "0", false));
        int opv_received = Integer.parseInt(getValue(client.getColumnmaps(), "opv_received", "0", false));

        int ipv_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "ipv_balance_in_hand", "0", false));
        int ipv_received = Integer.parseInt(getValue(client.getColumnmaps(), "ipv_received", "0", false));

        int pcv_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "pcv_balance_in_hand", "0", false));
        int pcv_received = Integer.parseInt(getValue(client.getColumnmaps(), "pcv_received", "0", false));

        int penta_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "penta_balance_in_hand", "0", false));
        int penta_received = Integer.parseInt(getValue(client.getColumnmaps(), "penta_received", "0", false));

        int measles_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "measles_balance_in_hand", "0", false));
        int measles_received = Integer.parseInt(getValue(client.getColumnmaps(), "measles_received", "0", false));

        int tt_balance_in_hand = Integer.parseInt(getValue(client.getColumnmaps(), "tt_balance_in_hand", "0", false));
        int tt_received = Integer.parseInt(getValue(client.getColumnmaps(), "tt_received", "0", false));

        //#TODO get Total balance,wasted and received from total variables instead of calculating here.
        int totalBalanceInHand = bcgBalanceInHand + opv_balance_in_hand + ipv_balance_in_hand +
                pcv_balance_in_hand + penta_balance_in_hand + measles_balance_in_hand + tt_balance_in_hand;

        int totalReceived = bcgReceived + opv_received + ipv_received + pcv_received + penta_received +
                measles_received + tt_received ;

        Map<String, String> m = client.getColumnmaps();
        
        int totalUsed = addAsInts(true, m.get("bcg"), m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"), m.get("ipv"),
                m.get("penta1"), m.get("penta2"), m.get("penta3"), m.get("measles1"), m.get("measles2"),
                m.get("pcv1"), m.get("pcv2"), m.get("pcv3"),
                m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5"));

        Log.v(getClass().getName(), "Now getting next month report");

        List<CommonPersonObject> nextMonthRpt = org.ei.opensrp.Context.getInstance().commonrepository("stock").customQueryForCompleteRow("SELECT * FROM stock WHERE report='monthly' AND date LIKE '" + new DateTime(date.getTime()).plusMonths(1).toString("yyyy-MM") + "%' ", null, "stock");

        Log.v(getClass().getName(), "Next month report "+nextMonthRpt);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "BCG", tr, true);
        addToRow(getActivity(), calculateStartingBalance(bcgBalanceInHand, bcgReceived), tr, true);
        addToRow(getActivity(), m.get("bcg"), tr, true);
        addToRow(getActivity(), calculateWasted(bcgBalanceInHand, bcgReceived, IntegerUtil.tryParse(m.get("bcg"),0), nextMonthRpt, "bcg"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(bcgBalanceInHand, bcgReceived, IntegerUtil.tryParse(m.get("bcg"),0)), tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "OPV", tr, true);
        addToRow(getActivity(), calculateStartingBalance(opv_balance_in_hand, opv_received), tr, true);
        addToRow(getActivity(), addAsInts(true, m, "opv0","opv1","opv2","opv3")+"", tr, true);
        addToRow(getActivity(), calculateWasted(opv_balance_in_hand, opv_received, addAsInts(true, m, "opv0","opv1","opv2","opv3"), nextMonthRpt, "opv"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(opv_balance_in_hand, opv_received, addAsInts(true, m, "opv0","opv1","opv2","opv3")), tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "IPV", tr, true);
        addToRow(getActivity(), calculateStartingBalance(ipv_balance_in_hand, ipv_received), tr, true);
        addToRow(getActivity(), m.get("ipv"), tr, true);
        addToRow(getActivity(), calculateWasted(ipv_balance_in_hand, ipv_received, addAsInts(true, m, "ipv"), nextMonthRpt, "ipv"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(ipv_balance_in_hand, ipv_received, addAsInts(true, m, "ipv")), tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "PCV", tr, true);
        addToRow(getActivity(), calculateStartingBalance(pcv_balance_in_hand, pcv_received), tr, true);
        addToRow(getActivity(), addAsInts(true, m, "pcv1","pcv2","pcv3")+"", tr, true);
        addToRow(getActivity(), calculateWasted(pcv_balance_in_hand, pcv_received, addAsInts(true, m, "pcv1","pcv2","pcv3"), nextMonthRpt, "pcv"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(pcv_balance_in_hand, pcv_received, addAsInts(true, m, "pcv1","pcv2","pcv3")), tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "PENTAVALENT", tr, true);
        addToRow(getActivity(), calculateStartingBalance(penta_balance_in_hand, penta_received), tr, true);
        addToRow(getActivity(), addAsInts(true, m, "penta1","penta2","penta3")+"", tr, true);
        addToRow(getActivity(), calculateWasted(penta_balance_in_hand, penta_received, addAsInts(true, m, "penta1","penta2","penta3"), nextMonthRpt, "penta"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(penta_balance_in_hand, penta_received, addAsInts(true, m, "penta1","penta2","penta3")), tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "MEASLES", tr, true);
        addToRow(getActivity(), calculateStartingBalance(measles_balance_in_hand, measles_received), tr, true);
        addToRow(getActivity(), addAsInts(true, m, "measles1","measles2")+"", tr, true);
        addToRow(getActivity(), calculateWasted(measles_balance_in_hand, measles_received, addAsInts(true, m, "measles1","measles2"), nextMonthRpt, "measles"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(measles_balance_in_hand, measles_received, addAsInts(true, m, "measles1","measles2")), tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "TETNUS", tr, true);
        addToRow(getActivity(),calculateStartingBalance(tt_balance_in_hand, tt_received), tr, true);
        addToRow(getActivity(), addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5")+"", tr, true);
        addToRow(getActivity(), calculateWasted(tt_balance_in_hand, tt_received, addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5"), nextMonthRpt, "tt"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(tt_balance_in_hand, tt_received, addAsInts(true, m, "tt1","tt2","tt3","tt4","tt5")), tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "DILUTANTS", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+getValue(client.getColumnmaps(), "dilutants_received", "0" , false)
                +"+"+getValue(client.getColumnmaps(), "dilutants_balance_in_hand", "0" , false), tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "SYRINGES", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+getValue(client.getColumnmaps(), "syringes_received", "0" , false)
                +"+"+getValue(client.getColumnmaps(), "syringes_balance_in_hand", "0" , false), tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        addToRow(getActivity(), "SAFETY BOXES", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+getValue(client.getColumnmaps(), "safety_boxes_received", "0" , false)
                +"+"+getValue(client.getColumnmaps(), "safety_boxes_balance_in_hand", "0" , false), tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        addToRow(getActivity(), "<font color='gray'>"+"N/A", tr, true);
        tb.addView(tr);

        tr = getDataRow(getActivity());
        tr.setBackgroundColor(Utils.getColor(getActivity(), R.color.background_material_light));

        addToRow(getActivity(), "TOTAL", tr, true);
        addToRow(getActivity(), calculateStartingBalance(totalBalanceInHand, totalReceived), tr, true);
        addToRow(getActivity(), totalUsed+"", tr, true);
        addToRow(getActivity(), calculateWasted(totalBalanceInHand, totalReceived, totalUsed, nextMonthRpt,
                "bcg", "opv", "ipv", "pcv", "penta", "measles", "tt"), tr, true);
        addToRow(getActivity(), calculateEndingBalance(totalBalanceInHand, totalReceived, totalUsed), tr, true);
        tb.addView(tr);

        final LinearLayout l = (LinearLayout) currentView.findViewById(R.id.monthly_detail_container);

        View.OnClickListener onclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                l.setLayoutParams(lp);
            }
        };

        tb.setOnClickListener(onclick);
        l.setOnClickListener(onclick);

        TableLayout tbd = (TableLayout) currentView.findViewById(R.id.stock_vaccine_table_daily);
        while (tbd.getChildCount() > 1) {
            tbd.removeView(tbd.getChildAt(tbd.getChildCount() - 1));
        }

        tbd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 135);
                l.setLayoutParams(lp);
            }
        });

        String sql = "SELECT t.*, SUBSTR(t.date,1,7) || '-' || dts.dom AS dom, " +
                "       (select count(id) c from pkwoman where date(tt1) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt1, " +
                "       (select count(id) c from pkwoman where date(tt2) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt2, " +
                "       (select count(id) c from pkwoman where date(tt3) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt3, " +
                "       (select count(id) c from pkwoman where date(tt4) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt4, " +
                "       (select count(id) c from pkwoman where date(tt5) = SUBSTR(t.date,1,7) || '-' || dts.dom) tt5, " +
                "       (select count(id) c from pkchild where date(bcg) = SUBSTR(t.date,1,7) || '-' || dts.dom) bcg, " +
                "       (select count(id) c from pkchild where date(opv0) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv0, " +
                "       (select count(id) c from pkchild where date(opv1) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv1, " +
                "       (select count(id) c from pkchild where date(opv2) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv2, " +
                "       (select count(id) c from pkchild where date(opv3) = SUBSTR(t.date,1,7) || '-' || dts.dom) opv3, " +
                "       (select count(id) c from pkchild where date(ipv) = SUBSTR(t.date,1,7) || '-' || dts.dom) ipv, " +
                "       (select count(id) c from pkchild where date(pcv1) = SUBSTR(t.date,1,7) || '-' || dts.dom) pcv1, " +
                "       (select count(id) c from pkchild where date(pcv2) = SUBSTR(t.date,1,7) || '-' || dts.dom) pcv2, " +
                "       (select count(id) c from pkchild where date(pcv3) = SUBSTR(t.date,1,7) || '-' || dts.dom) pcv3, " +
                "       (select count(id) c from pkchild where date(measles1) = SUBSTR(t.date,1,7) || '-' || dts.dom) measles1, " +
                "       (select count(id) c from pkchild where date(measles2) = SUBSTR(t.date,1,7) || '-' || dts.dom) measles2, " +
                "       (select count(id) c from pkchild where date(penta1) = SUBSTR(t.date,1,7) || '-' || dts.dom) penta1, " +
                "       (select count(id) c from pkchild where date(penta2) = SUBSTR(t.date,1,7) || '-' || dts.dom) penta2, " +
                "       (select count(id) c from pkchild where date(penta3) = SUBSTR(t.date,1,7) || '-' || dts.dom) penta3 " +
                "    FROM stock t " +
                "    JOIN (SELECT '01' dom UNION SELECT '02' UNION SELECT '03' UNION SELECT '04' " +
                    " UNION SELECT '05' UNION SELECT '06' UNION SELECT '07' UNION SELECT '08' " +
                    " UNION SELECT '09' UNION SELECT '10' UNION SELECT '11' UNION SELECT '12' " +
                    " UNION SELECT '13' UNION SELECT '14' UNION SELECT '15' UNION SELECT '16' " +
                    " UNION SELECT '17' UNION SELECT '18' UNION SELECT '19' UNION SELECT '20' " +
                    " UNION SELECT '21' UNION SELECT '22' UNION SELECT '23' UNION SELECT '24' " +
                    " UNION SELECT '25' UNION SELECT '26' UNION SELECT '27' UNION SELECT '28' " +
                    " UNION SELECT '29' UNION SELECT '30' UNION SELECT '31') dts ON dts.dom <= strftime('%d', date(t.date,'start of month','+1 month','-1 day'))" +
                " WHERE t.report='monthly' AND t.date LIKE '" + new DateTime(date.getTime()).toString("yyyy-MM") + "%'";

        Log.v(getClass().getName(), sql);

        List<CommonPersonObject> dl = Context.getInstance().commonrepository("stock").customQueryForCompleteRow(sql, null, "stock");

        Log.v(getClass().getName(), "RESULTSET:"+dl.toString());

        for (CommonPersonObject o: dl){
            Map<String, String> cm = o.getColumnmaps();

            int used = addAsInts(true, cm.get("bcg"),
                    cm.get("opv0"), cm.get("opv1"), cm.get("opv2"), cm.get("opv3"),
                    cm.get("ipv"),
                    cm.get("penta1"), cm.get("penta2"), cm.get("penta3"),
                    cm.get("measles1"), cm.get("measles2"),
                    cm.get("pcv1"), cm.get("pcv2"), cm.get("pcv3"),
                    cm.get("tt1"), cm.get("tt2"), cm.get("tt3"), cm.get("tt4"), cm.get("tt5"));

            tr = getDataRow(getActivity());

            DateTime sdt = new DateTime(cm.get("dom"));
            addToRow(getActivity(), sdt.toString("dd-MMM"), tr, true);
            addToRow(getActivity(), cm.get("bcg"), tr, true);
            addToRow(getActivity(), addAsInts(true, cm.get("opv0"), cm.get("opv1"), cm.get("opv2"), cm.get("opv3"))+"", tr, true);
            addToRow(getActivity(), cm.get("ipv"), tr, true);
            addToRow(getActivity(), addAsInts(true, cm.get("pcv1"), cm.get("pcv2"), cm.get("pcv3"))+"", tr, true);
            addToRow(getActivity(), addAsInts(true, cm.get("penta1"), cm.get("penta2"), cm.get("penta3"))+"", tr, true);
            addToRow(getActivity(), addAsInts(true, cm.get("measles1"), cm.get("measles2"))+"", tr, true);
            addToRow(getActivity(), addAsInts(true, cm.get("tt1"), cm.get("tt2"), cm.get("tt3"), cm.get("tt4"), cm.get("tt5"))+"", tr, true);
            addToRow(getActivity(), used+"", tr, true);

            if (sdt.getDayOfWeek() == DateTimeConstants.SUNDAY){
                for (int i = 0; i < tr.getChildCount(); i++) {
                    tr.getChildAt(i).setBackgroundColor(getColor(getActivity(), R.color.background_material_light));
                }
            }

            tbd.addView(tr);
        }
    }
}
