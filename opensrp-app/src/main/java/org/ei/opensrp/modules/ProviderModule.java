package org.ei.opensrp.modules;

import org.ei.opensrp.provider.ANCSmartRegisterClientsProvider;
import org.ei.opensrp.provider.ChildSmartRegisterClientsProvider;
import org.ei.opensrp.provider.ECSmartRegisterClientsProvider;
import org.ei.opensrp.provider.FPSmartRegisterClientsProvider;
import org.ei.opensrp.provider.PNCSmartRegisterClientsProvider;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;

import dagger.Module;

/**
 * Created by koros on 2/15/16.
 */
@Module(complete = false, injects = {
    ANCSmartRegisterClientsProvider.class,
    ChildSmartRegisterClientsProvider.class,
    ECSmartRegisterClientsProvider.class,
    FPSmartRegisterClientsProvider.class,
    PNCSmartRegisterClientsProvider.class,
    SmartRegisterClientsProvider.class

})
public class ProviderModule {
}





