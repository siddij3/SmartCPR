package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CompressionsButtonFragment;
import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.CalibrateButtonFragment;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Victim;

import java.util.ArrayList;
import java.util.Objects;

public class CalibrateIMUActivity extends AppCompatActivity
        implements CompressionsButtonFragment.CompressionsButtonListener  {

    private int adultMinRate;
    private int adultMaxRate;

    private int youthMinRate;
    private int youthMaxRate;

    private int childMinRate;
    private int childMaxRate;

    private int infantMinRate;
    private int infantMaxRate;


    private final static String TAG = "CalibrateIMUActivity";

    Victim victim;

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

    private void startNextActivity() {
        Intent intent = new Intent(CalibrateIMUActivity.this,
                SpectralAnalysisActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString(SpectralAnalysisActivity.EXTRA_OFFSET_ACCELERATION_VALUE,
                String.valueOf(accelerationOffsetValue));

        bundle.putString(SpectralAnalysisActivity.EXTRA_VICTIM_MIN_DEPTH,
                String.valueOf(victim.minDepth()));

        bundle.putString(SpectralAnalysisActivity.EXTRA_VICTIM_MAX_DEPTH,
                String.valueOf(victim.maxDepth()));


        intent.putExtras(bundle);

        startActivity(intent);
    }

    @Override
    public void cprVictim(String strCprVictim) {
        //TODO This probably wont transfer the victim over to subsequent activities. Keep this in mind
        Log.d(TAG, "cprVictim: " + strCprVictim);

        setDetails();

        if (Objects.equals(strCprVictim, getString(R.string.victim_adult))) {
            victim = new Victim(getString(R.string.victim_adult), adultMaxRate, adultMinRate, 1);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_youth))) {
            victim = new Victim(getString(R.string.victim_adult), youthMaxRate, youthMinRate, 1);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_child))) {
            victim = new Victim(getString(R.string.victim_adult), childMaxRate, childMinRate, 0.5);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_infant))) {
            victim = new Victim(getString(R.string.victim_adult), infantMaxRate, infantMinRate, 0.5);

        }

        startNextActivity();

    }

    private void setDetails() {
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


    private void sendCalibrationMessage(String message) {
        calibratedIMUFragment.setCalibratingMessageFeedback(message);

    }

}
