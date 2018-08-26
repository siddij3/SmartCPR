package com.smartcpr.junaid.smartcpr;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothDataMainStream;
import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothStream;
import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class CalibrateIMUActivity extends AppCompatActivity {

    private final static String TAG = "CalibrateIMUActivity";

    private BluetoothDataMainStream bluetoothStream;

    private InputStream mmInStream;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);
        Log.d(TAG, "onCreate: " + TAG);

        CalibratedIMUFragment calibratedIMUFragment = (CalibratedIMUFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_calibrating_imu);

        connectToDevice();
    }

    private void connectToDevice() {
        bluetoothStream = new BluetoothDataMainStream();
        BluetoothDevice mBluetoothDevice = bluetoothStream.getBluetoothDevice();

        Log.d(TAG, "connectToDevice: Key Time Start");

        boolean isSocketConnected =  bluetoothStream.connectSocket(mBluetoothDevice);

        if (isSocketConnected) {

            BluetoothSocket mmSocket = bluetoothStream.getBluetoothSocket();

            mmInStream = bluetoothStream.getInputStream(mmSocket);
            Log.d(TAG, "connectToDevice: InputStreamObtained");
            bluetoothStream.start();
        }
        Log.d(TAG, "connectToDevice: Key Time");

        calibrateDevice();
    }


    private void calibrateDevice() {

        // String st;
        //        String tmp = "";
        //
        //        while ((st = br.readLine()) != null ) {
        //            tmp +=  st + ";";
        //        }

  //      String[] rawData = string.split(";;");
//        ArrayList<float[]> formattedData =  new ArrayList<float[]>() ;

        //for (int i = 0; i < rawData.length; i++) {
         //   formattedData.add(formatData(rawData[i]));
            //System.out.println(Arrays.toString(formattedData.get(i)));
       // }


        ///ArrayList<float[]> formattedData = calibrate.getData(stationaryFile);

    }


}
