package org.ei.opensrp.test.vaksinator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import org.ei.opensrp.Context;
import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.commonregistry.CommonPersonObjectClients;
import org.ei.opensrp.test.R;

/**
 * Created by Iq on 09/06/16, modified by Marwan on 14/07/16
 */
public class VaksinatorRecapitulationActivity extends Activity {

    //image retrieving
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    //  private static KmsCalc  kmsCalc;

    //image retrieving

    public static CommonPersonObjectClients controller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = Context.getInstance();
        setContentView(R.layout.smart_register_jurim_client_reporting);

        TextView hbUnder7 = (TextView) findViewById(R.id.hbunder7recap);
        TextView hbOver7 = (TextView) findViewById(R.id.hbover7recap);
        TextView bcg = (TextView) findViewById(R.id.bcgrecap);
        TextView pol1 = (TextView) findViewById(R.id.pol1recap);
        TextView hb2 = (TextView) findViewById(R.id.hb2recap);
        TextView pol2 = (TextView) findViewById(R.id.pol2recap);
        TextView hb3 = (TextView) findViewById(R.id.hb3recap);
        TextView pol3 = (TextView) findViewById(R.id.pol3recap);
        TextView hb1 = (TextView) findViewById(R.id.hb1recap);
        TextView pol4 = (TextView) findViewById(R.id.pol4recap);
        TextView measles = (TextView) findViewById(R.id.measlesrecap);
        TextView diedu30 = (TextView) findViewById(R.id.diedunder30recap);
        TextView diedo30 = (TextView) findViewById(R.id.diedover30recap);
        TextView move = (TextView) findViewById(R.id.movingrecap);
        TextView ket = (TextView) findViewById(R.id.ket);



        ImageButton backButton = (ImageButton) findViewById(R.id.btn_back_to_home);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(VaksinatorRecapitulationActivity.this, VaksinatorSmartRegisterActivity.class));
                overridePendingTransition(0, 0);
            }
        });

        System.out.println(controller.size());
        ket.setText(controller.toString());
    }
}
