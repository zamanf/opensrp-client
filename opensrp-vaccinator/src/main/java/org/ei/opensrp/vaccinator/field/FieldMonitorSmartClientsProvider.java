package org.ei.opensrp.vaccinator.field;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;
import org.ei.opensrp.view.viewHolder.OnClickFormLauncher;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static org.ei.opensrp.util.Utils.addToRow;
import static org.ei.opensrp.util.Utils.*;
import static org.ei.opensrp.util.Utils.getDataRow;
import static org.ei.opensrp.util.Utils.getValue;

public class FieldMonitorSmartClientsProvider implements SmartRegisterClientsProvider {

    private final LayoutInflater inflater;
    private final Context context;
    private final OnClickListener onClickListener;
    AlertService alertService;

    private ByMonthByDay byMonthlyAndByDaily;

    public enum ByMonthByDay {ByMonth, ByDay}

    public FieldMonitorSmartClientsProvider(Context context, OnClickListener onClickListener,
              AlertService alertService, ByMonthByDay byMonthlyAndByDaily) {
        this.onClickListener = onClickListener;
        this.context = context;
        this.alertService = alertService;
        this.byMonthlyAndByDaily = byMonthlyAndByDaily;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private int getTotalWasted(String startDate, String endDate, String type){
        int totalWasted = 0;
        for (HashMap<String, String> v: getWasted(startDate, endDate, type))
        {
            for (String k: v.keySet()) {
                totalWasted += Integer.parseInt(v.get(k) == null?"0":v.get(k));
            }
        }
        return totalWasted;
    }

    private ArrayList<HashMap<String, String>> getWasted(String startDate, String endDate, String type){
        String sqlWasted = "select sum (total_wasted)as total_wasted from stock where `report` ='"+type+"' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    private ArrayList<HashMap<String, String>> getWastedByVaccine(String startDate, String endDate, String type){
        String sqlWasted = "select " +
                " sum(ifnull(bcg_wasted, 0)) bcg, sum(ifnull(opv_wasted, 0)) opv," +
                " sum(ifnull(ipv_wasted, 0)) ipv, sum(ifnull(penta_wasted, 0)) penta," +
                " sum(ifnull(measles_wasted, 0)) measles, sum(ifnull(pcv_wasted, 0)) pcv," +
                " sum(ifnull(tt_wasted, 0)) tt, sum(ifnull(total_wasted, 0)) total" +
                " from stock where `report` ='"+type+"' and `date` between '" + startDate + "' and '" + endDate + "'";
        return org.ei.opensrp.Context.getInstance().commonrepository("stock").rawQuery(sqlWasted);
    }

    private int getTotalUsed(String startDate, String endDate){
        int totalUsed = 0;

        for (HashMap<String, String> v: getUsed(startDate, endDate)) {
            for (String k: v.keySet()) {
                totalUsed += Integer.parseInt(v.get(k) == null?"0":v.get(k));
            }
        }

        return totalUsed;
    }

    private ArrayList<HashMap<String, String>> getUsed(String startDate, String endDate){
        String sqlWoman = "select (" +
                "select count(*) tt1 from pkwoman where tt1 between  '" + startDate + "' and '" + endDate + "') tt1," +
                "(select count(*) tt2 from pkwoman where tt2 between '" + startDate + "' and '" + endDate + "') tt2," +
                "(select count(*) tt3 from pkwoman where tt3 between '" + startDate + "' and '" + endDate + "') tt3," +
                "(select count(*) tt4 from pkwoman where tt4 between '" + startDate + "' and '" + endDate + "') tt4," +
                "(select count(*) tt5 from pkwoman where tt5 between '" + startDate + "' and '" + endDate + "') tt5 " +
                "from pkwoman limit 1; ";

        String sqlChild = "select (" +
                "select count(*) c from pkchild where bcg between '" + startDate + "' and '" + endDate + "') bcg," +
                "(select count(*) c from pkchild where opv0 between '" + startDate + "' and '" + endDate + "') opv0," +
                "(select count(*) c from pkchild where opv1 between '" + startDate + "' and '" + endDate + "') opv1," +
                "(select count(*) c from pkchild where opv2 between '" + startDate + "' and '" + endDate + "') opv2," +
                "(select count(*) c from pkchild where opv3 between '" + startDate + "' and '" + endDate + "') opv3, " +
                "(select count(*) c from pkchild where ipv between '" + startDate + "' and '" + endDate + "') ipv, " +
                "(select count(*) c from pkchild where pcv1 between '" + startDate + "' and '" + endDate + "') pcv1," +
                "(select count(*) c from pkchild where pcv2 between '" + startDate + "' and '" + endDate + "') pcv2," +
                "(select count(*) c from pkchild where pcv3 between '" + startDate + "' and '" + endDate + "') pcv3, " +
                "(select count(*) c from pkchild where measles1 between '" + startDate + "' and '" + endDate + "') measles1, " +
                "(select count(*) c from pkchild where measles2 between '" + startDate + "' and '" + endDate + "') measles2," +
                "(select count(*) c from pkchild where penta1 between '" + startDate + "' and '" + endDate + "') penta1," +
                "(select count(*) c from pkchild where penta2 between '" + startDate + "' and '" + endDate + "') penta2," +
                "(select count(*) c from pkchild where penta3 between '" + startDate + "' and '" + endDate + "') penta3 " +
                "from pkchild limit 1 ;";

        ArrayList<HashMap<String, String>> ttVaccinesUsed = org.ei.opensrp.Context.getInstance().commonrepository("pkwoman").rawQuery(sqlWoman);
        ArrayList<HashMap<String, String>> childVaccinesUsed = org.ei.opensrp.Context.getInstance().commonrepository("pkchild").rawQuery(sqlChild);

        ttVaccinesUsed.addAll(childVaccinesUsed);
        return ttVaccinesUsed;
    }

    //todo refactor above method
    private Map<String, String> getUsedByVaccine(String startDate, String endDate){
        Map<String, String> m = new HashMap<>();
        ArrayList<HashMap<String, String>> al = getUsed(startDate, endDate);
        for (HashMap<String, String> s : al){
            m.putAll(s);
        }
        return m;
    }
    
    @Override
    public View getView(SmartRegisterClient client, View parentView, ViewGroup viewGroup) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        String dateentered = pc.getColumnmaps().get("date");

        DateTime date = null;
        try {
            date = new DateTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateentered).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TableLayout dt = (TableLayout) parentView.findViewById(R.id.stock_vaccine_table);
        dt.removeAllViewsInLayout();
        TableRow tr = getDataRow(context);

        if(byMonthlyAndByDaily.equals(ByMonthByDay.ByMonth)){

            String startDate = date.withDayOfMonth(1).toString("yyyy-MM-dd");
            String endDate = date.withDayOfMonth(1).plusMonths(1).minusDays(1).toString("yyyy-MM-dd");

            addToRow(context, date.toString("MMMM (yyyy)"), tr);
            addToRow(context, pc.getColumnmaps().get("Target_assigned_for_vaccination_at_each_month"), tr);

            int bcgBalanceInHand = Integer.parseInt(getValue(pc.getColumnmaps(), "bcg_balance_in_hand", "0", false));
            int bcgReceived = Integer.parseInt(getValue(pc.getColumnmaps(), "bcg_received", "0", false));

            int opv_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "opv_balance_in_hand", "0", false));
            int opv_received = Integer.parseInt(getValue(pc.getColumnmaps(), "opv_received", "0", false));

            int ipv_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "ipv_balance_in_hand", "0", false));
            int ipv_received = Integer.parseInt(getValue(pc.getColumnmaps(), "ipv_received", "0", false));

            int pcv_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "pcv_balance_in_hand", "0", false));
            int pcv_received = Integer.parseInt(getValue(pc.getColumnmaps(), "pcv_received", "0", false));

            int penta_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "penta_balance_in_hand", "0", false));
            int penta_received = Integer.parseInt(getValue(pc.getColumnmaps(), "penta_received", "0", false));

            int measles_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "measles_balance_in_hand", "0", false));
            int measles_received = Integer.parseInt(getValue(pc.getColumnmaps(), "measles_received", "0", false));

            int tt_balance_in_hand = Integer.parseInt(getValue(pc.getColumnmaps(), "tt_balance_in_hand", "0", false));
            int tt_received = Integer.parseInt(getValue(pc.getColumnmaps(), "tt_received", "0", false));

            //#TODO get Total balance,wasted and received from total variables instead of calculating here.
            int balanceInHand = bcgBalanceInHand + opv_balance_in_hand + ipv_balance_in_hand +
                    pcv_balance_in_hand + penta_balance_in_hand + measles_balance_in_hand + tt_balance_in_hand;

            int received = bcgReceived + opv_received + ipv_received + pcv_received + penta_received +
                    measles_received + tt_received ;

            addToRow(context, received+"", tr);
            addToRow(context, (getTotalUsed(startDate, endDate))+"", tr);
            addToRow(context, getTotalWasted(startDate, endDate, "monthly")+"", tr);
            addToRow(context, balanceInHand+"", tr);

            tr.setTag(R.id.client_details_tag, client);
        }
        else if(byMonthlyAndByDaily.equals(ByMonthByDay.ByDay)){
            String startDate = date.toString("yyyy-MM-dd");
            String endDate = date.toString("yyyy-MM-dd");

            Map<String, String> m = getUsedByVaccine(startDate, endDate);
            //ArrayList<HashMap<String, String>> wl = getWastedByVaccine(startDate, startDate, "daily");
            //HashMap<String, String> w = wl.size() > 0 ? wl.get(0) : new HashMap<String, String>();

            addToRow(context, "<b>"+date.toString("dd-MM-yyyy")+"<b>", tr, 2);

            addToRow(context, m.get("bcg"), tr);
            //addToRow(context, w.get("bcg"), tr);

            addToRow(context, addAsInts(true, m.get("opv0"), m.get("opv1"), m.get("opv2"), m.get("opv3"))+"", tr);
            //addToRow(context, w.get("opv"), tr);

            addToRow(context, addAsInts(true, m.get("ipv"))+"", tr);
            //addToRow(context, w.get("ipv"), tr);

            addToRow(context, addAsInts(true, m.get("penta1"), m.get("penta2"), m.get("penta3"))+"", tr);
            //addToRow(context, w.get("penta"), tr);

            addToRow(context, addAsInts(true, m.get("measles1"), m.get("measles2"))+"", tr);
            //addToRow(context, w.get("measles"), tr);

            addToRow(context, addAsInts(true, m.get("pcv1"), m.get("pcv2"), m.get("pcv3"))+"", tr);
            //addToRow(context, w.get("pcv"), tr);

            addToRow(context, addAsInts(true, m.get("tt1"), m.get("tt2"), m.get("tt3"), m.get("tt4"), m.get("tt5"))+"", tr);
            //addToRow(context, w.get("tt"), tr);

            addToRow(context, getTotalUsed(startDate, endDate) + "", tr);
//            addToRow(context, w.get("total") + "", tr);
            addToRow(context, getTotalWasted(startDate, endDate, "daily") + "", tr);

            tr.setTag(R.id.client_details_tag, client);
        }

        tr.setId(R.id.stock_detail_holder);
        tr.setOnClickListener(onClickListener);
        dt.addView(tr);
        //parentView.setLayoutParams(clientViewLayoutParams);
        return parentView;
    }


    @Override
    public SmartRegisterClients getClients() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption,
               SearchFilterOption searchFilter, SortOption sortOption) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {
        Log.i("", "NOTHING TO DO IN CLIENT PROVIDER WHEN SERVICE CHANGES");
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        throw new UnsupportedOperationException("Operation not supported");
    }

    @Override
    public View inflateLayoutForAdapter() {
        ViewGroup view = (ViewGroup) inflater().inflate(R.layout.smart_register_field_client, null);
        return view;
    }

    public LayoutInflater inflater() {
        return inflater;
    }
}
