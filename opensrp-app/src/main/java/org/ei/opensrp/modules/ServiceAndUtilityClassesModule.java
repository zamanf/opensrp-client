package org.ei.opensrp.modules;

import android.graphics.Typeface;

import org.ei.opensrp.service.ANMService;
import org.ei.opensrp.service.ActionService;
import org.ei.opensrp.service.AlertService;
import org.ei.opensrp.service.AllFormVersionSyncService;
import org.ei.opensrp.service.BeneficiaryService;
import org.ei.opensrp.service.ChildService;
import org.ei.opensrp.service.DrishtiService;
import org.ei.opensrp.service.EligibleCoupleService;
import org.ei.opensrp.service.FormPathService;
import org.ei.opensrp.service.FormSubmissionService;
import org.ei.opensrp.service.FormSubmissionSyncService;
import org.ei.opensrp.service.HTTPAgent;
import org.ei.opensrp.service.ImageUploadSyncService;
import org.ei.opensrp.service.MotherService;
import org.ei.opensrp.service.PendingFormSubmissionService;
import org.ei.opensrp.service.ServiceProvidedService;
import org.ei.opensrp.service.UserService;
import org.ei.opensrp.service.ZiggyFileLoader;
import org.ei.opensrp.service.ZiggyService;
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
import org.ei.opensrp.util.Cache;
import org.ei.opensrp.util.Session;
import org.ei.opensrp.view.contract.ANCClients;
import org.ei.opensrp.view.contract.ECClients;
import org.ei.opensrp.view.contract.FPClients;
import org.ei.opensrp.view.contract.HomeContext;
import org.ei.opensrp.view.contract.SmartRegisterClients;
import org.ei.opensrp.view.contract.Villages;
import org.ei.opensrp.view.contract.pnc.PNCClients;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by koros on 2/18/16.
 */
@Module(complete = false, injects = {
    PendingFormSubmissionService.class,
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
    VitaminAHandler.class,
    ActionService.class,
    AlertService.class,
    AllFormVersionSyncService.class,
    ANMService.class,
    BeneficiaryService.class,
    ChildService.class,
    DrishtiService.class,
    EligibleCoupleService.class,
    FormPathService.class,
    FormSubmissionService.class,
    FormSubmissionSyncService.class,
    HTTPAgent.class,
    ImageUploadSyncService.class,
    MotherService.class,
    ServiceProvidedService.class,
    UserService.class,
    ZiggyFileLoader.class,
    ZiggyService.class
})
public class ServiceAndUtilityClassesModule {

    @Provides
    @Singleton
    PendingFormSubmissionService providePendingFormSubmissionService() {
            return new PendingFormSubmissionService();
    }

    @Provides
    @Singleton
    ServiceProvidedService provideServiceProvidedService() {
            return new ServiceProvidedService();
    }

    @Provides
    @Singleton
    ECRegistrationHandler provideECRegistrationHandler() {
            return new ECRegistrationHandler();
    }

    @Provides
    @Singleton
    ZiggyFileLoader provideZiggyFileLoader(){
        return new ZiggyFileLoader();
    }

    @Provides
    @Singleton
    FPComplicationsHandler provideFPComplicationsHandler(){
        return new FPComplicationsHandler();
    }

    @Provides
    @Singleton
    FPChangeHandler provideFPChangeHandler(){
        return new FPChangeHandler();
    }

    @Provides
    @Singleton
    RenewFPProductHandler provideRenewFPProductHandler(){
        return new RenewFPProductHandler();
    }

    @Provides
    @Singleton
    ECCloseHandler provideECCloseHandler(){
        return new ECCloseHandler();
    }

    @Provides
    @Singleton
    ANCRegistrationOAHandler provideANCRegistrationOAHandler(){
        return new ANCRegistrationOAHandler();
    }

    @Provides
    @Singleton
    ANCVisitHandler provideANCVisitHandler(){
        return new ANCVisitHandler();
    }

    @Provides
    @Singleton
    ANCCloseHandler provideANCCloseHandler(){
        return new ANCCloseHandler();
    }

    @Provides
    @Singleton
    TTHandler provideTTHandler(){
        return new TTHandler();
    }

    @Provides
    @Singleton
    IFAHandler provideIFAHandler(){
        return new IFAHandler();
    }

    @Provides
    @Singleton
    HBTestHandler provideHBTestHandler(){
        return new HBTestHandler();
    }

    @Provides
    @Singleton
    DeliveryOutcomeHandler provideDeliveryOutcomeHandler(){
        return new DeliveryOutcomeHandler();
    }

    @Provides
    @Singleton
    PNCRegistrationOAHandler providePNCRegistrationOAHandler(){
        return new PNCRegistrationOAHandler();
    }

    @Provides
    @Singleton
    PNCCloseHandler providePNCCloseHandler(){
        return new PNCCloseHandler();
    }

    @Provides
    @Singleton
    PNCVisitHandler providePNCVisitHandler(){
        return new PNCVisitHandler();
    }

    @Provides
    @Singleton
    ChildImmunizationsHandler provideChildImmunizationsHandler(){
        return new ChildImmunizationsHandler();
    }

    @Provides
    @Singleton
    ChildRegistrationECHandler provideChildRegistrationECHandler(){
        return new ChildRegistrationECHandler();
    }

    @Provides
    @Singleton
    ChildRegistrationOAHandler provideChildRegistrationOAHandler(){
        return new ChildRegistrationOAHandler();
    }

    @Provides
    @Singleton
    ChildCloseHandler provideChildCloseHandler(){
        return new ChildCloseHandler();
    }

    @Provides
    @Singleton
    ChildIllnessHandler provideChildIllnessHandler(){
        return new ChildIllnessHandler();
    }

    @Provides
    @Singleton
    VitaminAHandler provideVitaminAHandler(){
        return new VitaminAHandler();
    }

    @Provides
    @Singleton
    DeliveryPlanHandler provideDeliveryPlanHandler(){
        return new DeliveryPlanHandler();
    }

    @Provides
    @Singleton
    ECEditHandler provideECEditHandler(){
        return new ECEditHandler();
    }

    @Provides
    @Singleton
    ANCInvestigationsHandler provideANCInvestigationsHandler(){
        return new ANCInvestigationsHandler();
    }

    @Provides
    @Singleton
    FormSubmissionRouter provideFormSubmissionRouter(){
        return new FormSubmissionRouter();
    }

    @Provides
    @Singleton
    FormSubmissionSyncService provideFormSubmissionSyncService(){
        return new FormSubmissionSyncService();
    }

    @Provides
    @Singleton
    AllFormVersionSyncService provideAllFormVersionSyncService(){
        return new AllFormVersionSyncService();
    }

    @Provides
    @Singleton
    ActionService provideActionService(){
        return new ActionService();
    }

    @Provides
    @Singleton
    Session provideSession(){
        return new Session();
    }

    @Provides
    @Singleton
    @Named("SmartRegisterClientsCache")
    public Cache<SmartRegisterClients> provideSmartRegisterClients() {
        return new Cache<SmartRegisterClients >();
    }

    @Provides
    @Singleton
    @Named("ListCache")
    public Cache<String> provideListCache() {
        return new Cache<String>();
    }

    @Provides
    @Singleton
    @Named("HomeContextCache")
    public Cache<HomeContext> provideHomeContextCache() {
        return new Cache<HomeContext>();
    }

    @Provides
    @Singleton
    @Named("ECClientsCache")
    public Cache<ECClients> provideECClientsCache() {
        return new Cache<ECClients>();
    }

    @Provides
    @Singleton
    @Named("FPClientsCache")
    public Cache<FPClients> provideFPClientsCache() {
        return new Cache<FPClients>();
    }

    @Provides
    @Singleton
    @Named("ANCClientsCache")
    public Cache<ANCClients> provideANCClientsCache() {
        return new Cache<ANCClients>();
    }

    @Provides
    @Singleton
    @Named("VillagesCache")
    public Cache<Villages> provideVillagesCache() {
        return new Cache<Villages>();
    }

    @Provides
    @Singleton
    @Named("PNCClientsCache")
    public Cache<PNCClients> providePNCClientsCache() {
        return new Cache<PNCClients>();
    }



    @Provides
    @Singleton
    @Named("TypefaceCache")
    public Cache<Typeface> provideTypefaceCache() {
        return new Cache<Typeface>();
    }


}
