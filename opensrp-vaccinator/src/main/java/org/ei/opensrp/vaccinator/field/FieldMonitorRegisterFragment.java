package org.ei.opensrp.vaccinator.field;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.ei.opensrp.Context;
import org.ei.opensrp.adapter.SmartRegisterPaginatedAdapter;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.cursoradapter.CursorCommonObjectFilterOption;
import org.ei.opensrp.cursoradapter.CursorSortOption;
import org.ei.opensrp.cursoradapter.SmartRegisterCursorBuilder;
import org.ei.opensrp.cursoradapter.SmartRegisterPaginatedCursorAdapter;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.application.common.DateSort;
import org.ei.opensrp.vaccinator.application.common.ReportFilterOption;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.customControls.CustomFontTextView;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.SearchFilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.fragment.SecuredNativeSmartRegisterFragment;
import org.ei.opensrp.view.template.DetailActivity;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

import java.util.HashMap;

import util.VaccinatorUtils;

import static android.view.View.TEXT_ALIGNMENT_CENTER;

/**
 * Created by Safwan on 2/15/2016.
 */
public class FieldMonitorRegisterFragment extends SecuredNativeSmartRegisterFragment {
    private SmartRegisterClientsProvider clientProvider;

    private final ClientActionHandler clientActionHandler = new ClientActionHandler();

    public FieldMonitorRegisterFragment(){
        super(null);
    }

    @SuppressLint("ValidFragment")
    public FieldMonitorRegisterFragment(FormController formController) {
        super(formController);
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.DefaultOptionsProvider() {
            @Override
            public SearchFilterOption searchFilterOption() {
                return null;
            }

            @Override
            public ServiceModeOption serviceMode() {
                return new StockDailyServiceModeOption(clientProvider);
            }

            @Override
            public FilterOption villageFilter() {
                return new CursorCommonObjectFilterOption("no village filter", "");
            }

            @Override
            public SortOption sortOption(){ return new DateSort("Reporting Period", "date DESC"); }

            @Override
            public String nameInShortFormForTitle() {
                return Context.getInstance().getStringResource(R.string.stock_register_title);
            }
        };
    }

    @Override
    protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
        return new SecuredNativeSmartRegisterActivity.NavBarOptionsProvider() {

            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[]{};
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[]{
                        new StockDailyServiceModeOption(null),
                        new StockMonthlyServiceModeOption(null)
                };
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[]{
                        new DateSort("Reporting Period", "date DESC")};
            }

            @Override
            public String searchHint() {
                return Context.getInstance().getStringResource(R.string.str_field_search_hint);
            }
        };
    }

    @Override
    protected void onServiceModeSelection(ServiceModeOption serviceModeOption, View view) {
        super.onServiceModeSelection(serviceModeOption, view);

        add2ndColumnHeaderView(serviceModeOption);
    }

    protected void add2ndColumnHeaderView(ServiceModeOption serviceModeOption) {
        if (serviceModeOption.name().toLowerCase().contains("dail")){
            LinearLayout cl = (LinearLayout) mView.findViewById(org.ei.opensrp.R.id.clients_upper_header_layout);
            cl.setVisibility(View.VISIBLE);
            cl.removeAllViewsInLayout();

            cl.addView(addHeaderItem("", 2));
            cl.addView(addHeaderItem("Vaccine Stock Used or Consumed over the Period", 7));
            cl.addView(addHeaderItem("TOTAL", 2));
        }
    }

    private CustomFontTextView addHeaderItem(String text, int weight){
        CustomFontTextView header = new CustomFontTextView(getActivity(), null, org.ei.opensrp.R.style.CustomFontTextViewStyle_ListView_Medium);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, weight);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.setMargins(3,0,0,0);
        header.setLayoutParams(lp);
        header.setGravity(TEXT_ALIGNMENT_CENTER);
        header.setText(text);
        header.setPadding(weight*3,0,0,1);
        header.setTextSize(21);
        header.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        header.setBackgroundColor(Color.LTGRAY);

        return header;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        if(getCurrentServiceModeOption() == null || getCurrentServiceModeOption().name().toLowerCase().contains("daily")) {
            clientProvider = clientsProvider(FieldMonitorSmartClientsProvider.ByMonthByDay.ByDay);
        }
        else {
            clientProvider =  clientsProvider(FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth);
        }
        return clientProvider;
    }

    private SmartRegisterClientsProvider clientsProvider(FieldMonitorSmartClientsProvider.ByMonthByDay type) {
        if (type.equals(FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth)) {
            setCurrentSearchFilter(new ReportFilterOption(FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth, ""));
            clientProvider = new FieldMonitorSmartClientsProvider(
                    getActivity().getApplicationContext(), clientActionHandler, context.alertService(), FieldMonitorSmartClientsProvider.ByMonthByDay.ByMonth);
        }
        else {
            setCurrentSearchFilter(new ReportFilterOption(FieldMonitorSmartClientsProvider.ByMonthByDay.ByDay, ""));
            clientProvider = new FieldMonitorSmartClientsProvider(
                    getActivity().getApplicationContext(), clientActionHandler, context.alertService(), FieldMonitorSmartClientsProvider.ByMonthByDay.ByDay);
        }
        return clientProvider;
    }

    @Override
    protected void onInitialization() {

        mView.findViewById(org.ei.opensrp.R.id.filter_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.village).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.sort_selection).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.sorted_by).setVisibility(View.GONE);

        mView.findViewById(org.ei.opensrp.R.id.label_sorted_by).setVisibility(View.GONE);
    }

    @Override
    protected void startRegistration() {
        HashMap<String, String> overrides = new HashMap<>();
        overrides.putAll(VaccinatorUtils.providerDetails());
        startForm(getRegistrationForm(overrides), "", overrides);
    }

    protected String getRegistrationForm(HashMap<String, String> overridemap) {
        return "vaccine_stock_position";
    }

    @Override
    protected void onResumption() {
        super.onResumption();
        getDefaultOptionsProvider();

        add2ndColumnHeaderView(getCurrentServiceModeOption());
    }//end of method

    @Override
    protected SmartRegisterPaginatedAdapter adapter() {
        return new SmartRegisterPaginatedCursorAdapter(getActivity(),
                new SmartRegisterCursorBuilder("stock", getCurrentServiceModeOption().name().toLowerCase().contains("dail")?"report='daily'":"report='monthly'", (CursorSortOption) getDefaultOptionsProvider().sortOption())
                , clientsProvider());
    }

    @Override
    protected void onCreation() {    }

    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.stock_detail_holder:
                    if (getCurrentServiceModeOption().name().toLowerCase().contains("month")) {
                        DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(R.id.client_details_tag), FieldMonitorMonthlyDetailActivity.class);
                    }
                    else {
                        DetailActivity.startDetailActivity(getActivity(), (CommonPersonObjectClient) view.getTag(R.id.client_details_tag), FieldMonitorDailyDetailActivity.class);
                    }

                    getActivity().finish();

                    break;
            }
        }
    }
}
