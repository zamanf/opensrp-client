package org.ei.opensrp.modules;

import org.ei.opensrp.router.ActionRouter;
import org.ei.opensrp.service.formSubmissionHandler.ANCCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCInvestigationsHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCRegistrationHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCRegistrationOAHandler;
import org.ei.opensrp.service.formSubmissionHandler.ANCVisitHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildIllnessHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildImmunizationsHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildRegistrationECHandler;
import org.ei.opensrp.service.formSubmissionHandler.ChildRegistrationOAHandler;
import org.ei.opensrp.service.formSubmissionHandler.DeliveryOutcomeHandler;
import org.ei.opensrp.service.formSubmissionHandler.DeliveryPlanHandler;
import org.ei.opensrp.service.formSubmissionHandler.ECCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.ECEditHandler;
import org.ei.opensrp.service.formSubmissionHandler.ECRegistrationHandler;
import org.ei.opensrp.service.formSubmissionHandler.FPChangeHandler;
import org.ei.opensrp.service.formSubmissionHandler.FPComplicationsHandler;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionHandler;
import org.ei.opensrp.service.formSubmissionHandler.FormSubmissionRouter;
import org.ei.opensrp.service.formSubmissionHandler.HBTestHandler;
import org.ei.opensrp.service.formSubmissionHandler.IFAHandler;
import org.ei.opensrp.service.formSubmissionHandler.PNCCloseHandler;
import org.ei.opensrp.service.formSubmissionHandler.PNCRegistrationOAHandler;
import org.ei.opensrp.service.formSubmissionHandler.PNCVisitHandler;
import org.ei.opensrp.service.formSubmissionHandler.RenewFPProductHandler;
import org.ei.opensrp.service.formSubmissionHandler.TTHandler;
import org.ei.opensrp.service.formSubmissionHandler.VitaminAHandler;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by koros on 2/15/16.
 */
@Module(complete = false, injects = {
    ANCCloseHandler.class,
    ANCInvestigationsHandler.class,
    ANCRegistrationHandler.class,
    ANCRegistrationOAHandler.class,
    ANCVisitHandler.class,
    ChildCloseHandler.class,
    ChildIllnessHandler.class,
    ChildImmunizationsHandler.class,
    ChildRegistrationECHandler.class,
    ChildRegistrationOAHandler.class,
    DeliveryOutcomeHandler.class,
    DeliveryPlanHandler.class,
    ECCloseHandler.class,
    ECEditHandler.class,
    ECRegistrationHandler.class,
    FormSubmissionHandler.class,
    FormSubmissionRouter.class,
    FPChangeHandler.class,
    FPComplicationsHandler.class,
    HBTestHandler.class,
    IFAHandler.class,
    PNCCloseHandler.class,
    PNCRegistrationOAHandler.class,
    PNCVisitHandler.class,
    RenewFPProductHandler.class,
    TTHandler.class,
    VitaminAHandler.class
})
public class FormSubmissionHandlerModule {

    @Provides
    @Singleton
    ActionRouter provideActionRouter() {
            return new ActionRouter();
    }
}







