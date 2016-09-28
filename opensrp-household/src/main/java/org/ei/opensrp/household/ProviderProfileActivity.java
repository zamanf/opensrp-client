package org.ei.opensrp.household;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


import static org.ei.opensrp.util.Utils.convertDateFormat;
import static org.ei.opensrp.util.Utils.getDataRow;
import static org.ei.opensrp.util.Utils.getValue;
import static org.ei.opensrp.util.Utils.nonEmptyValue;

public class ProviderProfileActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(org.ei.opensrp.vaccinator.R.layout.provider_profile);

        HashMap<String, String> providerdt = VaccinatorUtils.providerDetails();

        ((TextView)findViewById(org.ei.opensrp.vaccinator.R.id.detail_heading)).setText("Provider Details");

        String programId = nonEmptyValue(providerdt, true, false, "provider_id");
        ((TextView)findViewById(org.ei.opensrp.vaccinator.R.id.details_id_label)).setText(programId);

        ((TextView)findViewById(org.ei.opensrp.vaccinator.R.id.detail_today)).setText(convertDateFormat(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), true));

        //BASIC INFORMATION
        TableLayout dt = (TableLayout) findViewById(org.ei.opensrp.vaccinator.R.id.report_detail_info_table1);

        dt.addView(getDataRow(this, "ID", programId, null));
        dt.addView(getDataRow(this, "Name", getValue(providerdt, "provider_name", true), null));
        dt.addView(getDataRow(this, "Team Identifier", getValue(providerdt, "provider_identifier", false), null));
        dt.addView(getDataRow(this, "Team", getValue(providerdt, "provider_team", true), null));

        dt.addView(getDataRow(this, "Province", getValue(providerdt, "provider_province", true), null));
        dt.addView(getDataRow(this, "City", getValue(providerdt, "provider_city", true), null));
        dt.addView(getDataRow(this, "Town", getValue(providerdt, "provider_town", true), null));
        dt.addView(getDataRow(this, "UC", getValue(providerdt, "provider_uc", true), null));
        dt.addView(getDataRow(this, "Center", getValue(providerdt, "provider_location_id", true), null));
    }
}
