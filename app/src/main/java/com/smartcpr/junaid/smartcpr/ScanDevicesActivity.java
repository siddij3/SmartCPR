package com.smartcpr.junaid.smartcpr;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.ScanDevicesFragments.ListDevicesFragment;
import com.smartcpr.junaid.smartcpr.ScanDevicesFragments.ScanButtonFragment;
/**
 * ScanDevicesActivity Ac
 *
 * First activity after user opens app
 * Scan Button scans nearby Bluetooth Devices and lists them
 * Tapping the Device allows user to pair the device
 *
 * Primary Functions
 *
 * addDevice: Adds scanned device to list in UI
 * bluetoothDeviceBonded: Takes Info of paired Bluetooth device and bundles it
 *                          DeviceDetailsActivity
 *
 */

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
    public void bluetoothDeviceBonded(Boolean isBluetoothDeviceBonded, BluetoothDevice mBluetoothDevice) {

        //Creates DeviceDetailsActivity activity and passes bluetooth data to the it
        if (isBluetoothDeviceBonded) {
            Intent intent = new Intent(ScanDevicesActivity.this,
                    DeviceDetailsActivity.class);

            Bundle bundle = new Bundle();
            bundle.putString(DeviceDetailsActivity.EXTRA_BLUETOOTH_DEVICE_NAME,
                                mBluetoothDevice.getName());
            bundle.putString(DeviceDetailsActivity.EXTRA_BLUETOOTH_DEVICE_PHYSICAL_ADDRESS,
                                mBluetoothDevice.getAddress());

            intent.putExtras(bundle);

            Log.d(TAG, "bluetoothDeviceBonded: Passing info through an intent before starting new activity");
            startActivity(intent);
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

