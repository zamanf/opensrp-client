package org.ei.opensrp.mcare.elco;

import android.view.View;

import org.ei.opensrp.Context;
import org.ei.opensrp.mcare.R;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.contract.ANCSmartRegisterClient;
import org.ei.opensrp.view.contract.ChildSmartRegisterClient;
import org.ei.opensrp.view.contract.FPSmartRegisterClient;
import org.ei.opensrp.view.contract.pnc.PNCSmartRegisterClient;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.viewHolder.NativeANCSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeChildSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativeFPSmartRegisterViewHolder;
import org.ei.opensrp.view.viewHolder.NativePNCSmartRegisterViewHolder;

import static org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity.ClientsHeaderProvider;

public class ElcoServiceModeOption extends ServiceModeOption {

    public ElcoServiceModeOption(SmartRegisterClientsProvider provider) {
        super(provider);
    }

    @Override
    public String name() {
        return Context.getInstance().applicationContext().getResources().getString(R.string.all_eligible_couples);
    }

    @Override
    public ClientsHeaderProvider getHeaderProvider() {
        return new ClientsHeaderProvider() {
            @Override
            public int count() {
<<<<<<< HEAD
                return 4;
=======
                return 5;
>>>>>>> fc57a485ae9e44237dc69626e10ad144281a146a
            }

            @Override
            public int weightSum() {
<<<<<<< HEAD
                return 20;
=======
                return 40;
>>>>>>> fc57a485ae9e44237dc69626e10ad144281a146a
            }

            @Override
            public int[] weights() {
<<<<<<< HEAD
                return new int[]{6,5,3,6};
=======
                return new int[]{12,9,5,7,7};
>>>>>>> fc57a485ae9e44237dc69626e10ad144281a146a
            }

            @Override
            public int[] headerTextResourceIds() {
                return new int[]{
                        R.string.elco_profile, R.string.elco_unique_id, R.string.elco_lmp,
<<<<<<< HEAD
                        R.string.elco_psrf_due_date};
=======
                        R.string.elco_psrf_due_date,R.string.mis_elco_due};
>>>>>>> fc57a485ae9e44237dc69626e10ad144281a146a
            }
        };
    }

    @Override
    public void setupListView(ChildSmartRegisterClient client,
                              NativeChildSmartRegisterViewHolder viewHolder,
                              View.OnClickListener clientSectionClickListener) {

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
