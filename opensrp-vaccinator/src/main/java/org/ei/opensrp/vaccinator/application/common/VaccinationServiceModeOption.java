package org.ei.opensrp.vaccinator.application.common;

import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.template.SmartRegisterClientsProvider;

public class VaccinationServiceModeOption extends ServiceModeOption {

    private String name;
    private int[] headerTextResourceIds;
    private int[] columnWeights;

    public VaccinationServiceModeOption(SmartRegisterClientsProvider provider, String name, int[] headerTextResourceIds, int[] columnWeights){
        super(provider);
        this.name = name;
        this.headerTextResourceIds = headerTextResourceIds;
        this.columnWeights = columnWeights;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public SecuredNativeSmartRegisterActivity.ClientsHeaderProvider getHeaderProvider() {
        return new SecuredNativeSmartRegisterActivity.ClientsHeaderProvider() {
            @Override
            public int count() {
                return headerTextResourceIds.length;
            }

            @Override
            public int weightSum() {
                int sum = 0;
                for(int i = 0; i < columnWeights.length; i++){
                    sum += columnWeights[i];
                }
                return sum;
            }

            @Override
            public int[] weights() {
                return columnWeights;
            }

            @Override
            public int[] headerTextResourceIds() {
                return headerTextResourceIds;
            }
        };
    }
}
