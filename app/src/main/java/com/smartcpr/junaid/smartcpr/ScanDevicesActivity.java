package com.smartcpr.junaid.smartcpr;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

public class ScanDevicesActivity extends AppCompatActivity implements ScanButtonFragment.ScanButtonListener {
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

