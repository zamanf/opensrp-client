package org.ei.opensrp.vaccinator.application.common;

import org.apache.commons.lang3.StringUtils;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.vaccinator.field.FieldMonitorSmartClientsProvider;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.SearchFilterOption;

public class ReportFilterOption implements SearchFilterOption {
    private String filter;
    private FieldMonitorSmartClientsProvider.ByMonthByDay type;

    public ReportFilterOption(FieldMonitorSmartClientsProvider.ByMonthByDay type, String filter){
        this.filter=filter;
        this.type = type;
    }

    @Override
    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public String getCriteria() {
        String c =  type == FieldMonitorSmartClientsProvider.ByMonthByDay.ByDay?" report='daily' ":" report='monthly' ";
        if(StringUtils.isNotBlank(filter)){
            c += " AND date LIKE '"+filter+"%'";
        }
        return c;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
        if(currentclient.getColumnmaps().get("report").toLowerCase().contains(filter.toLowerCase())) {
            return true;
        }
        else if(currentclient.getColumnmaps().get("report").toLowerCase().contains(filter.toLowerCase())) {
            return true;
        }
        return false;
    }

    @Override
    public String name() {
        return "";
    }
}
