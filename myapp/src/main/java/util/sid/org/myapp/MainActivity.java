package util.sid.org.myapp;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.qualcomm.snapdragon.sdk.face.FacialProcessingConstants;

import org.ei.opensrp.view.activity.SecuredActivity;
import org.ei.opensrp.view.controller.NavigationController;

public class MainActivity extends SecuredActivity {

    public FacialProcessingConstants mFacialProcessingConstants;
    private Button btn_client, btn_bt;
    protected NavigationController mNavigationController;

    @Override
    protected void onCreation() {
        setContentView(R.layout.activity_main);

        mNavigationController = new NavigationControllerINA(this, anmController);

        btn_client = (Button) findViewById(R.id.btn_clients);
        btn_bt = (Button) findViewById(R.id.btn_bluetooth);

        btn_client.setOnClickListener(btnListener);
        btn_client.setOnClickListener(btnListener);

    }

    @Override
    protected void onResumption() {

    }

    private View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btn_clients :
                    mNavigationController.startECSmartRegistry();
                    break;
                case R.id.btn_bluetooth :
//                    mNavigationController.startECSmartRegistry();
                    start_bluetooth();
                    break;
            }
        }
    };

    private void start_bluetooth() {

        Intent intent = new Intent(this, SIDBluetooth.class);
        startActivity(intent);
    }

}

