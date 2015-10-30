package org.ei.opensrp.vaccinator.field;

import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Oct-15.
 */
public class FieldMonitorSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {


    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return null;
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return null;
    }

    @Override
    protected SmartRegisterClientsProvider clientsProvider() {
        return null;
    }

    @Override
    protected void onInitialization() {

    }

    @Override
    protected void startRegistration() {

    }

}
