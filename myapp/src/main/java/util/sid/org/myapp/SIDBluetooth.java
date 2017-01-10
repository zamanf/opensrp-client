package util.sid.org.myapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by wildan on 1/10/17.
 */
public class SIDBluetooth extends Activity {

    private TextView tv_Status;
    private Button btn_Activate;
    private Button btn_Paired;
    private Button btn_Scan;

    private ProgressDialog pd_proggressDlg;
    private ArrayList<BluetoothDevice> arr_devices = new ArrayList<BluetoothDevice>();
    private BluetoothAdapter mBluetoothAdapter;

    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        setContentView(R.layout.activity_bluetooth);

        tv_Status = (TextView) findViewById(R.id.tv_status);
        btn_Activate = (Button) findViewById(R.id.btn_activate);
        btn_Paired = (Button) findViewById(R.id.btn_paired);
        btn_Scan = (Button) findViewById(R.id.btn_scan);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        pd_proggressDlg = new ProgressDialog(this);

        pd_proggressDlg.setMessage("Scanning ...");
        pd_proggressDlg.setCancelable(false);
        pd_proggressDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                mBluetoothAdapter.cancelDiscovery();
            }
        });

        if(mBluetoothAdapter == null){
            showUnsupported();
        }else{
            btn_Paired.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    if (pairedDevices == null || pairedDevices.size() == 0){
                        showToast("No Paired Device Found.");
                    } else {
                        ArrayList<BluetoothDevice> lists = new ArrayList<BluetoothDevice>();
                        lists.addAll(pairedDevices);
                        Intent intent = new Intent(SIDBluetooth.this, DevicesListActivity.class);
                        intent.putParcelableArrayListExtra("device.list", lists);
                        startActivity(intent);
                    }
                }
            });

            btn_Scan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mBluetoothAdapter.startDiscovery();
                }
            });

            btn_Activate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mBluetoothAdapter.isEnabled()){
                        mBluetoothAdapter.disable();
                        showDisabled();
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 1000);
                    }
                }
            });

            if (mBluetoothAdapter.isEnabled()){
                showEnabled();
            } else {
                showDisabled();
            }
        }

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);


        registerReceiver(mReceiver, filter);

    }

    protected void onPause(){
        super.onPause();
        if (mBluetoothAdapter != null){
            if (mBluetoothAdapter.isDiscovering()){
                mBluetoothAdapter.cancelDiscovery();
            }
        }
    }

    protected void onDestroy(){
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private void showUnsupported() {
        tv_Status.setText("Bluetooth is Unsupported by this Device");
        tv_Status.setTextColor(Color.RED);

        btn_Activate.setText("Enable");
        btn_Activate.setEnabled(true);

        btn_Paired.setEnabled(false);
        btn_Scan.setEnabled(false);

    }


    private void showToast(String s) {
        Toast.makeText(SIDBluetooth.this, s, Toast.LENGTH_SHORT).show();
    }

    private void showDisabled() {
        tv_Status.setText("Bluetooth is Off");
        tv_Status.setTextColor(Color.RED);

        btn_Activate.setText("Disabled");
        btn_Activate.setEnabled(true);

        btn_Paired.setEnabled(true);
        btn_Scan.setEnabled(true);


    }

    private void showEnabled() {
        tv_Status.setText("Bluetooth is Off");
        tv_Status.setTextColor(Color.RED);

        btn_Activate.setText("Enable");
        btn_Activate.setEnabled(true);

        btn_Paired.setEnabled(false);
        btn_Scan.setEnabled(false);


    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON){
                    showToast("Enabled");

                    showEnabled();
                }
            } else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                arr_devices = new ArrayList<BluetoothDevice>();

                pd_proggressDlg.show();

            } else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                pd_proggressDlg.dismiss();

                Intent inewIntent = new Intent(SIDBluetooth.this, DevicesListActivity.class);
                inewIntent.putParcelableArrayListExtra("device.list", arr_devices);
                startActivity(inewIntent);
            } else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                arr_devices.add(device);

                showToast("Found Device"+ device.getName());
            }
        }
    };


}
