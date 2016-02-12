package org.ei.opensrp.modules;


import org.ei.opensrp.view.activity.ANCDetailActivity;
import org.ei.opensrp.view.activity.ANCSmartRegisterActivity;
import org.ei.opensrp.view.activity.CameraLaunchActivity;
import org.ei.opensrp.view.activity.ChildDetailActivity;
import org.ei.opensrp.view.activity.ChildSmartRegisterActivity;
import org.ei.opensrp.view.activity.ECSmartRegisterActivity;
import org.ei.opensrp.view.activity.EligibleCoupleDetailActivity;
import org.ei.opensrp.view.activity.FPSmartRegisterActivity;
import org.ei.opensrp.view.activity.FormActivity;
import org.ei.opensrp.view.activity.FormWebInterface;
import org.ei.opensrp.view.activity.HomeActivity;
import org.ei.opensrp.view.activity.MicroFormActivity;
import org.ei.opensrp.view.activity.NativeANCSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeChildSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeECSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeFPSmartRegisterActivity;
import org.ei.opensrp.view.activity.NativeHomeActivity;
import org.ei.opensrp.view.activity.NativePNCSmartRegisterActivity;
import org.ei.opensrp.view.activity.PNCDetailActivity;
import org.ei.opensrp.view.activity.PNCSmartRegisterActivity;
import org.ei.opensrp.view.activity.ReportIndicatorCaseListActivity;
import org.ei.opensrp.view.activity.ReportIndicatorDetailActivity;
import org.ei.opensrp.view.activity.ReportIndicatorListViewActivity;
import org.ei.opensrp.view.activity.ReportsActivity;
import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.activity.SecuredFormActivity;
import org.ei.opensrp.view.activity.SecuredNativeSmartRegisterActivity;
import org.ei.opensrp.view.activity.SecuredWebActivity;
import org.ei.opensrp.view.activity.SettingsActivity;
import org.ei.opensrp.view.activity.SmartRegisterActivity;
import org.ei.opensrp.view.activity.VideosActivity;

import dagger.Module;

@Module(complete = false, injects = {
		HomeActivity.class,
		ANCDetailActivity.class,
		ANCSmartRegisterActivity.class,
		CameraLaunchActivity.class,
		ChildDetailActivity.class,
		ChildSmartRegisterActivity.class,
		ECSmartRegisterActivity.class,
		EligibleCoupleDetailActivity.class,
		FormActivity.class,
		FormWebInterface.class,
		FPSmartRegisterActivity.class,
		HomeActivity.class,
		MicroFormActivity.class,
		NativeANCSmartRegisterActivity.class,
		NativeChildSmartRegisterActivity.class,
		NativeECSmartRegisterActivity.class,
		NativeFPSmartRegisterActivity.class,
		NativeHomeActivity.class,
		NativePNCSmartRegisterActivity.class,
		PNCDetailActivity.class,
		PNCSmartRegisterActivity.class,
		ReportIndicatorCaseListActivity.class,
		ReportIndicatorDetailActivity.class,
		ReportIndicatorListViewActivity.class,
		ReportsActivity.class,
		SecuredActivity.class,
		SecuredFormActivity.class,
		SecuredNativeSmartRegisterActivity.class,
		SecuredWebActivity.class,
		SettingsActivity.class,
		SmartRegisterActivity.class,
		VideosActivity.class
})
public class ActivityFragmentModule {

}

