package com.smartcpr.trainer.smartcpr;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.R;
import com.smartcpr.trainer.smartcpr.ScanDevicesFragments.ListDevicesFragment;
import com.smartcpr.trainer.smartcpr.ScanDevicesFragments.ScanButtonFragment;

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

        Log.d(TAG,"ScanDevicesActivity" );
    }

    /**
     * addDevice
     *
     *
     * Method:
     *  Adds the details of a bluetooth device to a list defined in the fragment to
     *  display it as a list for the user to identify which BT device they want to connect to
     *
     * Params:
     *  BTdevice - Identifier for BT device with MAC address and Name that appears to BT
     *
     *
     */


    @Override
    public void addDevice(BluetoothDevice BTdevice) {
        ListDevicesFragment listDevicesFragment = (ListDevicesFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_list);
        listDevicesFragment.addDevicesToList(BTdevice);
    }

    /**
     * bluetoothDeviceBonded
     *
     *
     * Method:
     *  Using Android intents, hands off the details to the next activity (DeviceDetails)
     *  that displays whether the device is paired and which one
     *
     * Params:
     *  isBluetoothDeviceBonded - Boolean value that checks whether or not the Bluetooth
     *    Device is bonded to the phone or not
     *
     *  mBluetoothDevice - Identifier for BT device with MAC address and Name that appears to BT
     *
     */

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

