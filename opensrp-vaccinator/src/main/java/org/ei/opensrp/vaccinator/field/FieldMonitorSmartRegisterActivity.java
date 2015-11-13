package org.ei.opensrp.vaccinator.field;

import android.view.Window;
import android.view.WindowManager;

import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Oct-15.
 */
public class FieldMonitorSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {


    private CommonPersonObjectController controllerByMonth;
    private CommonPersonObjectController controllerByDay;

    private DefaultOptionsProvider defaultOptionProvider;
    private NavBarOptionsProvider navBarOptionsProvider;


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
        controllerByMonth = new CommonPersonObjectController(context.allCommonsRepositoryobjects("vaccine_field"),
                context.allBeneficiaries(), context.listCache(),
                context.personObjectClientsCache(), "vaccinator_name", "vaccine_field","report","monthly",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails
                , "date_formatted",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails );

        controllerByDay = new CommonPersonObjectController(context.allCommonsRepositoryobjects("vaccine_field"),
                context.allBeneficiaries(), context.listCache(),
                context.personObjectClientsCache(), "vaccinator_name", "vaccine_field","report","daily",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails
                , "date_formatted",
                CommonPersonObjectController.ByColumnAndByDetails.byDetails.byDetails );
    }

    @Override
    protected void startRegistration() {


        //formController.startFormActivity();
    }

    @Override
    protected void onCreation() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreation();


        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        onInitialization();

        defaultOptionProvider = getDefaultOptionsProvider();
        navBarOptionsProvider = getNavBarOptionsProvider();

        setupViews();
    }

}
