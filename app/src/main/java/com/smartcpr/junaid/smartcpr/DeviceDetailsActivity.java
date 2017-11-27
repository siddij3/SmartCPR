package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.DeviceDetailsFragment;
import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.TrainButtonFragment;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Victim;

public class DeviceDetailsActivity extends AppCompatActivity implements TrainButtonFragment.TrainButtonListener{

    private final static String TAG = "DeviceDetailsActivity";

    public static final String EXTRA_BLUETOOTH_DEVICE_NAME = "com.smartcpr.junaid.smartcpr.bluetoothdevicename";
    public static final String EXTRA_BLUETOOTH_DEVICE_PHYSICAL_ADDRESS = "com.smartcpr.junaid.smartcpr.bluetoothdevicephysicaladdress";

    private DeviceDetailsFragment deviceDetailsFragment;

    Victim victim;

    private int adultMinRate;
    private int adultMaxRate;

    private int youthMinRate;
    private int youthMaxRate;

    private int childMinRate;
    private int childMaxRate;

    private int infantMinRate;
    private int infantMaxRate;

    private String mBTDeviceName;
    private String mBTDevicePhysicalAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_details);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mBTDeviceName =
                bundle.getString(EXTRA_BLUETOOTH_DEVICE_NAME);
        mBTDevicePhysicalAddress =
                bundle.getString(EXTRA_BLUETOOTH_DEVICE_PHYSICAL_ADDRESS);

        Log.d(TAG, "onCreate: DeviceName "  + mBTDeviceName);
        Log.d(TAG, "onCreate: Address "  + mBTDevicePhysicalAddress);

        deviceDetailsFragment = (DeviceDetailsFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_device_details);
        deviceDetailsFragment.setDetailsText(mBTDeviceName, mBTDevicePhysicalAddress);
        Log.d(TAG, deviceDetailsFragment.toString());

    }




    @Override
    public void cprVictim(String strCprVictim) {
        Log.d(TAG, "cprVictim: " + strCprVictim.toString());

        Intent intent = new Intent(DeviceDetailsActivity.this,
              SpectralAnalysisActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString(SpectralAnalysisActivity.EXTRA_VICTIM_AGE, strCprVictim.toString());

        intent.putExtras(bundle);

        Log.d(TAG, "cprVictim: Starting Spectral Analysis Activity");
        startActivity(intent);

    }
}
