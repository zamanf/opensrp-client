package org.ei.opensrp.vaccinator.fragment;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.FilterOption;

public class ReportFilterOption implements FilterOption {
    private final String criteria;
    public ReportFilterOption(String criteria){
        this.criteria=criteria;
    }
    @Override
    public boolean filter(SmartRegisterClient client) {
        CommonPersonObjectClient currentclient = (CommonPersonObjectClient) client;
        if(currentclient.getDetails().get("report") != null
                && currentclient.getDetails().get("report").toLowerCase().contains(criteria.toLowerCase())) {
            return true;
        }
        else if(currentclient.getDetails().get("report") != null
                && currentclient.getDetails().get("report").toLowerCase().contains(criteria.toLowerCase())) {
            return true;
        }
        return false;
    }

    @Override
    public String name() {
        return "";
    }
}
