package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;

import java.util.ArrayList;

public class CalibrateIMUActivity extends AppCompatActivity {

    private final static String TAG = "CalibrateIMUActivity";

    private int txyz;

    private int desiredListSizeSizeForCalibration;

    private float accelerationOffsetValue;

    private CalibratedIMUFragment calibratedIMUFragment;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrating_imu_prompt);
        Log.d(TAG, "onCreate: " + TAG);

        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeSizeForCalibration = getResources().getInteger(R.integer.device_calibration_array_size);

        calibratedIMUFragment = (CalibratedIMUFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_calibrating_imu);

        calibrateDevice();

    }

    private void calibrateDevice() {
        float offsetBenchmark = 0.2f;
        float offsetFailedVal = 100f;


        ArrayList<float[]> formattedDataFromDevice =  ManageData.getData(desiredListSizeSizeForCalibration);

        float[][] accelerometerOffsetData = formattedDataFromDevice.toArray(new float[][]
                {new float[formattedDataFromDevice.size()]});


        float[] getAccelerationFromOneDimension = ManageData.getAccelerationFromRawData(accelerometerOffsetData, txyz);

        //offsetValue should be passed to next activity
        accelerationOffsetValue = ManageData.offsetAcceleration(getAccelerationFromOneDimension, offsetBenchmark, offsetFailedVal);

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

    private void startNext() {
        Intent intent = new Intent(CalibrateIMUActivity.this,
                SpectralAnalysisActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(SpectralAnalysisActivity.EXTRA_OFFSET_ACCELERATION_VALUE,
                String.valueOf(accelerationOffsetValue));


        intent.putExtras(bundle);
    }

    private void sendCalibrationMessage(String message) {
        calibratedIMUFragment.setCalibratingMessageFeedback(message);

    }

}
