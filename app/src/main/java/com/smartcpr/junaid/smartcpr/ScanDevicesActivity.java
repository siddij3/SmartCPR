package com.smartcpr.junaid.smartcpr;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ScanDevicesActivity extends AppCompatActivity implements ScanButtonFragment.ScanButtonListener,
                                                                ListDevicesFragment.ListDevicesListener {
    private final static String TAG = "ScanDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);

        Log.d(TAG,"onActivityResultCalled" );
    }

    @Override
    public void addDevice(BluetoothDevice BTdevice) {
        ListDevicesFragment listDevicesFragment = (ListDevicesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_list);
        listDevicesFragment.addDevicesToList(BTdevice);
    }

    @Override
    public void bluetoothDeviceBonded(Boolean isBluetoothDeviceBonded) {
        if (isBluetoothDeviceBonded) {
            Intent myIntent = new Intent(ScanDevicesActivity.this,
                    DeviceDetailsActivity.class);
            startActivity(myIntent);

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Stopping Activity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onStop: Pausing Activity");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onStop: Destroying Activity");

    }
}

