package com.smartcpr.junaid.smartcpr;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothDeviceData;
import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothDeviceManager;
import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CalibrateIMUActivity extends AppCompatActivity {

    private final static String TAG = "CalibrateIMUActivity";

    private BluetoothDeviceManager bluetoothStream;

    private ManageData manageData;
    private int txyz;
    private int desiredListSizeSizeForCalibration;

    float accelerationOffsetValue;


    CalibratedIMUFragment calibratedIMUFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrating_imu_prompt);
        Log.d(TAG, "onCreate: " + TAG);

        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeSizeForCalibration = getResources().getInteger(R.integer.device_calibration_array_size);

        calibratedIMUFragment = (CalibratedIMUFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_calibrating_imu);

        manageData = new ManageData();
        Log.d(TAG, "onCreate: manageData");

        connectToDevice();
    }

    private void connectToDevice() {
        bluetoothStream = new BluetoothDeviceManager();
        BluetoothDevice mBluetoothDevice = bluetoothStream.getBluetoothDevice();

        //Log.d(TAG, "connectToDevice: Key Time Start");

        boolean isSocketConnected =  bluetoothStream.connectSocket(mBluetoothDevice);


        //Run this whole bluetooth initializing thing on a seperate thread, or part of it on a seperate thread
        if (isSocketConnected) {

            BluetoothSocket mmSocket = bluetoothStream.getBluetoothSocket();
            bluetoothStream.getInputStream(mmSocket);

            Log.d(TAG, "connectToDevice: InputStreamObtained");
            bluetoothStream.start();
        }

        calibrateDevice();
    }

    private void calibrateDevice() {
        List<String> listRawDeviceData =  BluetoothDeviceData.getList();


        ArrayList<float[]> formattedDataFromDevice =  manageData.getData(desiredListSizeSizeForCalibration, listRawDeviceData);

        for (int i = 0; i < 38; i++) {
            Log.d(TAG, "calibrateDevice: " + Arrays.toString(formattedDataFromDevice.get(i)));
        }

        float[][] accelerometerOffsetData = formattedDataFromDevice.toArray(new float[][]
                {new float[formattedDataFromDevice.size()]});


        float[] getAccelerationFromOneDimension = manageData.getAccelerationFromRawData(accelerometerOffsetData, txyz);
        float offsetBenchmark = 0.2f;
        float offsetFailedVal = 100f;

        //offsetValue should be passed to next activity
        accelerationOffsetValue = manageData.offsetAcceleration(getAccelerationFromOneDimension, offsetBenchmark, offsetFailedVal);

        String calibrationResultMessage;
        if (accelerationOffsetValue == offsetFailedVal) {
            calibrationResultMessage = getResources().getString(R.string.calibration_failed_message);
            Log.d(TAG, "calibrateDevice: " + calibrationResultMessage);

            sendCalibrationMessage(calibrationResultMessage);
            calibrateDevice();
        }

        calibrationResultMessage = getResources().getString(R.string.calibration_complete_message);
        Log.d(TAG, "calibrateDevice: " + calibrationResultMessage);
        sendCalibrationMessage(calibrationResultMessage);


    }

    private void sendCalibrationMessage(String message) {
        calibratedIMUFragment.setCalibratingMessageFeedback(message);

    }

}
