package com.smartcpr.junaid.smartcpr;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothStream;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;
import com.smartcpr.junaid.smartcpr.ScanDevicesFragments.ListDevicesFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class CalibrateIMUActivity extends AppCompatActivity {

    private final static String TAG = "CalibrateIMUActivity";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);
        Log.d(TAG, "onCreate: " + TAG);

        CalibratedIMUFragment calibratedIMUFragment = (CalibratedIMUFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_calibrating_imu);


        BluetoothStream bluetoothStream = new BluetoothStream(getApplicationContext());

        //Move this to another class? No
        calibratedIMUFragment.setCalibratingMessageFeedback(calibratingDevice());



    }

    private Boolean calibratingDevice() {

        //Do crazy shit here

        return false;

    }


}
