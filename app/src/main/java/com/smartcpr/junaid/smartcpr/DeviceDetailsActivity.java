package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothDeviceManager;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;
import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.DeviceDetailsFragment;
import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.TrainButtonFragment;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Victim;

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
        TrainButtonFragment.TrainButtonListener{

    private final static String TAG = "DeviceDetailsActivity";

    public static final String EXTRA_BLUETOOTH_DEVICE_NAME
            = "com.smartcpr.junaid.smartcpr.bluetoothdevicename";

    public static final String EXTRA_BLUETOOTH_DEVICE_PHYSICAL_ADDRESS
            = "com.smartcpr.junaid.smartcpr.bluetoothdevicephysicaladdress";

    private int adultMinRate;
    private int adultMaxRate;

    private int youthMinRate;
    private int youthMaxRate;

    private int childMinRate;
    private int childMaxRate;

    private int infantMinRate;
    private int infantMaxRate;

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

    private void setDetails () {
        adultMinRate = getResources().getInteger(R.integer.adult_min);
        adultMaxRate = getResources().getInteger(R.integer.adult_max);

        youthMinRate = getResources().getInteger(R.integer.youth_min);
        youthMaxRate = getResources().getInteger(R.integer.youth_max);

        childMinRate = getResources().getInteger(R.integer.child_min);
        childMaxRate = getResources().getInteger(R.integer.child_max);

        infantMinRate = getResources().getInteger(R.integer.infant_min);
        infantMaxRate = getResources().getInteger(R.integer.infant_max);

        Log.d(TAG, "setDetails: Details Set");
    }


    @Override
    public void cprVictim(String strCprVictim) {
        //TODO This probably wont transfer the victim over to subsequent activities. Keep this in mind
        setDetails();
        Log.d(TAG, "cprVictim: " + strCprVictim);
        Victim victim;

        if (Objects.equals(strCprVictim, getString(R.string.victim_adult))) {
            victim = new Victim(getString(R.string.victim_adult), adultMaxRate, adultMinRate, 1);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_youth))) {
            victim = new Victim(getString(R.string.victim_adult), youthMaxRate, youthMinRate, 1);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_child))) {
            victim = new Victim(getString(R.string.victim_adult), childMaxRate, childMinRate, 0.5);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_infant))) {
            victim = new Victim(getString(R.string.victim_adult), infantMaxRate, infantMinRate, 0.5);

        }

        connectToDevice();


        Intent intent = new Intent(DeviceDetailsActivity.this,
              CalibrateIMUActivity.class);

        //TODO may have to bundle victim to next activity
        Log.d(TAG, "cprVictim: Starting Spectral Analysis Activity");
        startActivity(intent);

    }

    private void connectToDevice() {
        BluetoothDeviceManager bluetoothStream = new BluetoothDeviceManager();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
