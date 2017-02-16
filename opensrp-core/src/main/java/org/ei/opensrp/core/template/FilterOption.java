package org.ei.opensrp.core.template;

import org.ei.opensrp.view.contract.SmartRegisterClient;
import org.ei.opensrp.view.dialog.DialogOption;

public interface FilterOption extends DialogOption {
    String getCriteria();
    boolean filter(SmartRegisterClient client);
}
