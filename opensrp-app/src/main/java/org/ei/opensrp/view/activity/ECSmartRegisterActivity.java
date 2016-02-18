package org.ei.opensrp.view.activity;

import org.ei.opensrp.view.controller.ECSmartRegisterController;

public class ECSmartRegisterActivity extends SmartRegisterActivity {



    @Override
    protected void onSmartRegisterInitialization() {
        webView.addJavascriptInterface(new ECSmartRegisterController(), "context");
        webView.loadUrl("file:///android_asset/www/smart_registry/ec_register.html");
    }
}
