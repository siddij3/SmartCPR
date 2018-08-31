package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothDeviceManager;
import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.CalibrateButtonFragment;
import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.DeviceDetailsFragment;

import java.util.Objects;


/**
 * DeviceDetailsActivity Activity
 *
 * First activity after user opens app
 * Scan Button scans nearby Bluetooth Devices and lists them
 * Tapping the Device allows user to pair the device
 *
 * Primary Functions
 *
 * onCreate: Unpacks bundled device details from ScanDevicesActivity
 *            and verifies the device details by showing the name and MAC address in large text
 * setDetails: Initializes CPR depth specifications for different age groups as per
 *
 * cprVictim: Takes Info of paired Bluetooth device and bundles it
 *                          DeviceDetailsActivity
 *
 */

public class DeviceDetailsActivity extends AppCompatActivity implements
        CalibrateButtonFragment.CalibrateButtonListener {

    private final static String TAG = "DeviceDetailsActivity";

    public static final String EXTRA_BLUETOOTH_DEVICE_NAME
            = "com.smartcpr.junaid.smartcpr.bluetoothdevicename";

    public static final String EXTRA_BLUETOOTH_DEVICE_PHYSICAL_ADDRESS
            = "com.smartcpr.junaid.smartcpr.bluetoothdevicephysicaladdress";



    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        //Unpacks bundled info from ScanDevicesActivity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String mBTDeviceName = Objects.requireNonNull(bundle).getString(EXTRA_BLUETOOTH_DEVICE_NAME);
        String mBTDevicePhysicalAddress = bundle.getString(EXTRA_BLUETOOTH_DEVICE_PHYSICAL_ADDRESS);

        Log.d(TAG, "onCreate: DeviceName "  + mBTDeviceName);
        Log.d(TAG, "onCreate: Address "  + mBTDevicePhysicalAddress);

        //Shows Device Details in UI
        DeviceDetailsFragment deviceDetailsFragment = (DeviceDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_device_details);
        Log.d(TAG, deviceDetailsFragment.toString());
        deviceDetailsFragment.setDetailsText(mBTDeviceName, mBTDevicePhysicalAddress);

    }




    @Override
    public void connectDevice() {
        BluetoothDeviceManager bluetoothStream = new BluetoothDeviceManager();

        Intent intent = new Intent(DeviceDetailsActivity.this,
                CalibrateIMUActivity.class);

        //TODO may have to bundle victim to next activity
        Log.d(TAG, "cprVictim: Starting Spectral Analysis Activity");
        startActivity(intent);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
