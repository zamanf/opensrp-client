package org.ei.opensrp.vaccinator.fragment;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonObjectSort;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.db.Client;
import org.ei.opensrp.vaccinator.household.HouseholdDetailActivity;
import org.ei.opensrp.vaccinator.household.HouseholdSmartClientsProvider;
import org.ei.opensrp.vaccinator.woman.DetailActivity;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.contract.ANCSmartRegisterClient;
import org.ei.opensrp.view.contract.ChildSmartRegisterClient;
import org.ei.opensrp.view.contract.ECSmartRegisterClient;
import org.ei.opensrp.view.contract.FPSmartRegisterClient;
import org.ei.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.dialog.AllClientsFilter;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;
import org.ei.opensrp.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeECSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativePNCSmartRegisterViewHolder;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    public void setupListView(ChildSmartRegisterClient client, NativeChildSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {
        /// Log.d("child View : ", client.dateOfBirth());
    }

    @Override
    public void setupListView(ANCSmartRegisterClient client, NativeANCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(FPSmartRegisterClient client, NativeFPSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }

    @Override
    public void setupListView(PNCSmartRegisterClient client, NativePNCSmartRegisterViewHolder viewHolder, View.OnClickListener clientSectionClickListener) {

    }
}
