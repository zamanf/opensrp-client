package org.ei.opensrp.vaccinator.field;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import org.ei.opensrp.commonregistry.CommonPersonObjectClient;
import org.ei.opensrp.vaccinator.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by engrmahmed14@gmail.com on 12/13/15.
 */
public class FieldMonitorMonthlyDetailActivity extends Activity {

    public static Map<String, String> usedVaccines;
    public static CommonPersonObjectClient fieldclient;
    public static HashMap<String, String> wastedVaccines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.field_detail_monthly_activity);

        TextView vaccinatorIdTextView =(TextView)findViewById(R.id.fielddetail_vaccinatorid_monthly);
        TextView vaccinatorNameTextView =(TextView)findViewById(R.id.fielddetail_vaccinator_name_monthly);
        TextView centerIdTextView =(TextView)findViewById(R.id.fielddetail_centerid_monthly);
        TextView ucTextView =(TextView)findViewById(R.id.fielddetail_uc_monthly);

        TextView bcgUsedTextView =(TextView)findViewById(R.id.fielddetail_bcg_used_monthly);
        TextView bcgWastedTextView =(TextView)findViewById(R.id.fielddetail_bcg_inhand_monthly);


        String bcgWasted=fieldclient.getDetails().get("bcg_balance_in_hand")!=null?fieldclient.getDetails().get("bcg_balance_in_hand"):"N/A";
        bcgWastedTextView.setText(bcgWasted);
        bcgUsedTextView.setText(usedVaccines.get("bcg")!=null?usedVaccines.get("bcg"):"N/A");



        TextView opvUsedTextView =(TextView)findViewById(R.id.fielddetail_opv_used);
        TextView opvWastedTextView =(TextView)findViewById(R.id.fielddetail_opv_inhand_monthly);
        opvWastedTextView.setText(fieldclient.getDetails().get("opv_balance_in_hand")!=null?fieldclient.getDetails().get("opv_balance_in_hand"):"N/A");
        opvUsedTextView.setText(usedVaccines.get("opv")!=null?usedVaccines.get("opv"):"N/A");

        TextView ipvUsedTextView =(TextView)findViewById(R.id.fielddetail_ipv_used);
        TextView ipvWastedTextView =(TextView)findViewById(R.id.fielddetail_ipv_inhand_monthly);
        ipvWastedTextView.setText(fieldclient.getDetails().get("ipv_balance_in_hand")!=null?fieldclient.getDetails().get("ipv_balance_in_hand"):"N/A");
        ipvUsedTextView.setText(usedVaccines.get("ipv")!=null?usedVaccines.get("ipv"):"N/A");



        TextView pentavalentUsedTextView =(TextView)findViewById(R.id.fielddetail_pentavalent_used);
        TextView pentavalentWastedTextView =(TextView)findViewById(R.id.fielddetail_pentavalent_inhand_monthly);
        pentavalentWastedTextView.setText(fieldclient.getDetails().get("penta_balance_in_hand")!=null?fieldclient.getDetails().get("penta_balance_in_hand"):"N/A");
        pentavalentUsedTextView.setText(usedVaccines.get("pentavalent")!=null?usedVaccines.get("pentavalent"):"N/A");




        TextView measlesUsedTextView =(TextView)findViewById(R.id.fielddetail_measles_used);
        TextView measlesWastedTextView =(TextView)findViewById(R.id.fielddetail_measles_inhand_monthly);
        measlesWastedTextView.setText(fieldclient.getDetails().get("measles_balance_in_hand")!=null?fieldclient.getDetails().get("measles_balance_in_hand"):"N/A");
        measlesUsedTextView.setText(usedVaccines.get("measles")!=null?usedVaccines.get("measles"):"N/A");


        TextView safetyboxesUsedTextView =(TextView)findViewById(R.id.fielddetail_safetyboxes_used);
        TextView safetyboxesWastedTextView =(TextView)findViewById(R.id.fielddetail_safetyboxes_inhand_monthly);
        safetyboxesWastedTextView.setText(fieldclient.getDetails().get("safety_boxes_balance_in_hand")!=null?fieldclient.getDetails().get("safety_boxes_balance_in_hand"):"N/A");
        safetyboxesUsedTextView.setText(usedVaccines.get("safety_boxes")!=null?usedVaccines.get("safety_boxes"):"N/A");



        TextView syringesUsedTextView =(TextView)findViewById(R.id.fielddetail_syringes_used);
        TextView syringesWastedTextView =(TextView)findViewById(R.id.fielddetail_syringes_inhand_monthly);
        syringesWastedTextView.setText(fieldclient.getDetails().get("syringes_balance_in_hand")!=null?fieldclient.getDetails().get("syringes_balance_in_hand"):"N/A");
        syringesUsedTextView.setText(usedVaccines.get("syringes")!=null?usedVaccines.get("syringes"):"N/A");



        TextView tetanusUsedTextView =(TextView)findViewById(R.id.fielddetail_tetanus_used);
        TextView tetanusWastedTextView =(TextView)findViewById(R.id.fielddetail_tetanus_inhand_monthly);
        tetanusWastedTextView.setText(fieldclient.getDetails().get("tt_balance_in_hand")!=null?fieldclient.getDetails().get("tt_balance_in_hand"):"N/A");
        tetanusUsedTextView.setText(usedVaccines.get("tt")!=null?usedVaccines.get("tt"):"N/A");




        TextView dilutantsUsedTextView =(TextView)findViewById(R.id.fielddetail_dilutants_used);
        TextView dilutantsWastedTextView =(TextView)findViewById(R.id.fielddetail_dilutants_inhand_monthly);
        dilutantsWastedTextView.setText(fieldclient.getDetails().get("dilutants_balance_in_hand")!=null?fieldclient.getDetails().get("dilutants_balance_in_hand"):"N/A");
        dilutantsUsedTextView.setText(usedVaccines.get("dilutants")!=null?usedVaccines.get("dilutants"):"N/A");



        TextView pcvUsedTextView =(TextView)findViewById(R.id.fielddetail_pcv_used);
        TextView pcvWastedTextView =(TextView)findViewById(R.id.fielddetail_pcv_inhand_monthly);
        pcvWastedTextView.setText(fieldclient.getDetails().get("pcv_balance_in_hand")!=null?fieldclient.getDetails().get("pcv_balance_in_hand"):"N/A");
        pcvUsedTextView.setText(usedVaccines.get("pcv")!=null?usedVaccines.get("pcv"):"N/A");




    }
}
