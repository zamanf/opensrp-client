package util.sid.org.myapp;

import android.view.View;
import android.widget.Button;

import com.qualcomm.snapdragon.sdk.face.FacialProcessingConstants;

import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.controller.NavigationController;

public class MainActivity extends SecuredActivity {

    public FacialProcessingConstants mFacialProcessingConstants;
    private Button btn_client;
    protected NavigationController mNavigationController;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_main);

        mNavigationController = new NavigationControllerINA(this, anmController);

        btn_client = (Button) findViewById(R.id.btn_clients);

        btn_client.setOnClickListener(registerListener);

    }

    @Override
    protected void onResumption() {

    }

    private View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_clients :
                    mNavigationController.startECSmartRegistry();
                    break;
            }
        }
    };

}
