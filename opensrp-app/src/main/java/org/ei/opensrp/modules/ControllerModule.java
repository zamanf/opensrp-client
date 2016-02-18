package org.ei.opensrp.modules;

import org.ei.opensrp.view.controller.ANCDetailController;
import org.ei.opensrp.view.controller.ANCSmartRegisterController;
import org.ei.opensrp.view.controller.ANMController;
import org.ei.opensrp.view.controller.ANMLocationController;
import org.ei.opensrp.view.controller.ChildDetailController;
import org.ei.opensrp.view.controller.ChildSmartRegisterController;
import org.ei.opensrp.view.controller.ECSmartRegisterController;
import org.ei.opensrp.view.controller.EligibleCoupleDetailController;
import org.ei.opensrp.view.controller.FPSmartRegisterController;
import org.ei.opensrp.view.controller.FormController;
import org.ei.opensrp.view.controller.HomeController;
import org.ei.opensrp.view.controller.NativeAfterANMDetailsFetchListener;
import org.ei.opensrp.view.controller.NativeUpdateANMDetailsTask;
import org.ei.opensrp.view.controller.NavigationController;
import org.ei.opensrp.view.controller.PNCDetailController;
import org.ei.opensrp.view.controller.PNCSmartRegisterController;
import org.ei.opensrp.view.controller.ProfileNavigationController;
import org.ei.opensrp.view.controller.ReportIndicatorCaseListViewController;
import org.ei.opensrp.view.controller.ReportIndicatorDetailViewController;
import org.ei.opensrp.view.controller.ReportIndicatorListViewController;
import org.ei.opensrp.view.controller.ReportsController;
import org.ei.opensrp.view.controller.UpdateANMDetailsTask;
import org.ei.opensrp.view.controller.UpdateController;
import org.ei.opensrp.view.controller.VideosController;
import org.ei.opensrp.view.controller.VillageController;

import dagger.Module;

/**
 * Created by koros on 2/15/16.
 */
@Module(complete = false, injects = {
        ANCDetailController.class,
        ANCSmartRegisterController.class,
        ANMController.class,
        ANMLocationController.class,
        ChildDetailController.class,
        ChildSmartRegisterController.class,
        ECSmartRegisterController.class,
        EligibleCoupleDetailController.class,
        FormController.class,
        FPSmartRegisterController.class,
        HomeController.class,
        NativeAfterANMDetailsFetchListener.class,
        NativeUpdateANMDetailsTask.class,
        NavigationController.class,
        PNCDetailController.class,
        PNCSmartRegisterController.class,
        ProfileNavigationController.class,
        ReportIndicatorCaseListViewController.class,
        ReportIndicatorDetailViewController.class,
        ReportIndicatorListViewController.class,
        ReportsController.class,
        UpdateANMDetailsTask.class,
        UpdateController.class,
        VideosController.class,
        VillageController.class
})
public class ControllerModule {
}