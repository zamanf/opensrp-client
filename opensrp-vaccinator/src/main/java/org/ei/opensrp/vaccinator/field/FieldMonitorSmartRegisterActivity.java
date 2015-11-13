package org.ei.opensrp.vaccinator.field;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import org.ei.opensrp.commonregistry.CommonPersonObjectController;
import org.ei.opensrp.provider.SmartRegisterClientsProvider;
import org.ei.opensrp.vaccinator.R;
import org.ei.opensrp.vaccinator.child.ChildSmartClientsProvider;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.dialog.DialogOption;
import org.ei.opensrp.view.dialog.FilterOption;
import org.ei.opensrp.view.dialog.ServiceModeOption;
import org.ei.opensrp.view.dialog.SortOption;

/**
 * Created by muhammad.ahmed@ihsinformatics.com on 19-Oct-15.
 */
public class FieldMonitorSmartRegisterActivity extends SecuredNativeSmartRegisterActivity {


    private CommonPersonObjectController controllerByMonth;
    private CommonPersonObjectController controllerByDay;
    private SmartRegisterClientsProvider clientProvider = null;
    private DefaultOptionsProvider defaultOptionProvider;
    private NavBarOptionsProvider navBarOptionsProvider;
    private final ClientActionHandler clientActionHandler = new ClientActionHandler();
    private boolean sortbymonth=true;


    @Override
    protected DefaultOptionsProvider getDefaultOptionsProvider() {
        return new DefaultOptionsProvider() {
            @Override
            public ServiceModeOption serviceMode() {
                return null;
            }

            @Override
            public FilterOption villageFilter() {
                return null;
            }

            @Override
            public SortOption sortOption() {
                return null;
            }

            @Override
            public String nameInShortFormForTitle() {
                return null;
            }
        };
    }

    @Override
    protected NavBarOptionsProvider getNavBarOptionsProvider() {
        return new NavBarOptionsProvider() {
            @Override
            public DialogOption[] filterOptions() {
                return new DialogOption[0];
            }

            @Override
            public DialogOption[] serviceModeOptions() {
                return new DialogOption[0];
            }

            @Override
            public DialogOption[] sortingOptions() {
                return new DialogOption[0];
            }

            @Override
            public String searchHint() {
                return null;
            }
        };
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
    protected SmartRegisterClientsProvider clientsProvider() {

        if(sortbymonth) {
          //  if (clientProvider == null) {
                clientProvider = new ChildSmartClientsProvider(
                        this, clientActionHandler, controllerByMonth, context.alertService());
            //}
        }
        else{
            clientProvider = new ChildSmartClientsProvider(
                    this, clientActionHandler, controllerByMonth, context.alertService());


        }
        return clientProvider;
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


    private class ClientActionHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {

        }
    }

}
