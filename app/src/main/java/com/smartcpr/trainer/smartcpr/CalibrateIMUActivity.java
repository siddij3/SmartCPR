package com.smartcpr.trainer.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.R;
import com.smartcpr.trainer.smartcpr.BluetoothData.ManageData;
import com.smartcpr.trainer.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;
import com.smartcpr.trainer.smartcpr.CalibrateIMUFragments.CompressionsButtonFragment;
import com.smartcpr.trainer.smartcpr.ObjectClasses.Victim;

import java.util.ArrayList;
import java.util.Objects;

/**
 * CalibrateIMUActivity
 *
 * Third activity after user opens app
 * Prompts User for Name to store for future evaluations
 * Calibrates IMU for compressions in a separate thread to mitigate blocking from while loops
 *
 *
 * Primary Functions
 *
 * onCreate: Initializes and instantiates variables, methods and class objects for IMU calibration
 * handleMessage: handles messages from calibration thread
 * cprVictim: sets the details for victim ages, creates instance of victim class (child, youth,
 *              adult) and begins preparations for spectral analysis activity
 *
 *
 *
 * Secondary Functions
 *
 * calibrateDevice:
 *
 *
 *
 * Tertiary Functions
 *
 */

public class CalibrateIMUActivity extends AppCompatActivity
        implements CompressionsButtonFragment.CompressionsButtonListener  {

    private final static String TAG = "CalibrateIMUActivity";

    // Constants for age-dependent CPR depths
    private int adultMinDepth;
    private int adultMaxDepth;

    private int youthMinDepth;
    private int youthMaxDepth;

    private int childMinDepth;
    private int childMaxDepth;

    private int infantMinDepth;
    private int infantMaxDepth;

    // Victim class for victim age
    // Contains depth, depth tolerance and rates
    Victim victim;

    // Offset value for calibrating accelerometer,
    private float accelerationOffsetValue;

    private String calibrationResultMessage;

    // Details for formatting data from IMU - time, x, y, z values and list size
    private int txyz;
    private int desiredListSizeSizeForCalibration;

    // Fragment Objects
    private CalibratedIMUFragment calibratedIMUFragment;
    private CompressionsButtonFragment compressionsButtonFragment;

    Calibrate calibrate;

    private Handler mHandler;


    /**
     * onCreate
     *
     *
     * Method:
     *  Initializes index size of IMU input data, list size for calibration
     *  Initializes Fragment objects for buttons and calibration messages
     *
     *  Instantiates class for handling message from thread, and creates instance of calibration
     *  class
     *
     *
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrating_imu_prompt);
        Log.d(TAG, "onCreate: " + TAG);


        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeSizeForCalibration = getResources().getInteger(R.integer.device_calibration_array_size);

        calibratedIMUFragment = (CalibratedIMUFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_calibrating_imu);
        compressionsButtonFragment = (CompressionsButtonFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_button_begin_compressions);

        compressionsButtonFragment.makeButtonClickable(false);

        handleMessage();

        calibrate = new Calibrate(mHandler);
        calibrateDevice();

    }


    // Starts thread for IMU calibration
    private void calibrateDevice() {
        new Thread(calibrate).start();
    }


    /**
     * handleMessage
     *
     *
     * Method:
     *  Handles message from calibration thread and creates a message depending on the result
     *  A failed calibration loops the calibration
     */

    void handleMessage() {
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                int result = message.what;



                if (result == 1) {
                    calibrationResultMessage = getResources().getString(R.string.calibration_complete_message);
                    sendCalibrationMessage(calibrationResultMessage);

                    compressionsButtonFragment.makeButtonClickable(true);

                } else if (result == 0) {
                    calibrationResultMessage = getResources().getString(R.string.calibration_failed_message);
                    sendCalibrationMessage(calibrationResultMessage);

                    calibrateDevice();
                }


            }
        };

    }

    /**
     * cprVictim
     *
     * Method:
     *  sets details for victims, creates instance of victim class and class function for
     *  starting next activity
     *
     *  Params:
     *      strCprVictim: child, youth or adult.
     */

    @Override
    public void cprVictim(String strCprVictim) {
        Log.d(TAG, "cprVictim: " + strCprVictim);

        setDetails();

        if (Objects.equals(strCprVictim, getString(R.string.victim_adult))) {
            victim = new Victim(getString(R.string.victim_adult), adultMaxDepth, adultMinDepth, 1);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_youth))) {
            victim = new Victim(getString(R.string.victim_adult), youthMaxDepth, youthMinDepth, 1);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_child))) {
            victim = new Victim(getString(R.string.victim_adult), childMaxDepth, childMinDepth, 0.5);

        } else if (Objects.equals(strCprVictim, getString(R.string.victim_infant))) {
            victim = new Victim(getString(R.string.victim_adult), infantMaxDepth, infantMinDepth, 0.5);

        }

        startNextActivity();

    }

    /**
     * setDetails
     *
     *
     * Method:
     *    depending on the training session of the victim, each age group has different
     *    depths for compressions, and this method sets those details.
     *
     */
    private void setDetails() {
        adultMinDepth = getResources().getInteger(R.integer.adult_min);
        adultMaxDepth = getResources().getInteger(R.integer.adult_max);

        youthMinDepth = getResources().getInteger(R.integer.youth_min);
        youthMaxDepth = getResources().getInteger(R.integer.youth_max);

        childMinDepth = getResources().getInteger(R.integer.child_min);
        childMaxDepth = getResources().getInteger(R.integer.child_max);

        infantMinDepth = getResources().getInteger(R.integer.infant_min);
        infantMaxDepth = getResources().getInteger(R.integer.infant_max);

        Log.d(TAG, "setDetails: Details Set");
    }

    /**
     * startNextActivity
     *
     *
     * Method:
     *
     *
     */

    private void startNextActivity() {
        Intent intent = new Intent(CalibrateIMUActivity.this,
                SpectralAnalysisActivity.class);

        Bundle bundle = new Bundle();

        bundle.putString(SpectralAnalysisActivity.EXTRA_VICTIM_MIN_DEPTH,
                String.valueOf(victim.minDepth()));

        bundle.putString(SpectralAnalysisActivity.EXTRA_VICTIM_MAX_DEPTH,
                String.valueOf(victim.maxDepth()));

        bundle.putString(SpectralAnalysisActivity.EXTRA_OFFSET_ACCELERATION_VALUE,
                String.valueOf(accelerationOffsetValue));


        intent.putExtras(bundle);

        startActivity(intent);
    }

    /**
     * sendCalibrationMessage
     *
     *
     * Method:
     *
     *
     */

    private void sendCalibrationMessage(String message) {
        calibratedIMUFragment.setCalibratingMessageFeedback(message);

    }

    /**
     * class: Calibrate (Thread)
     *
     *
     * Primary Functions:
     *  Calibrates IMU by formatting data from IMU, taking acceleration data and averaging to zero
     */

    private class Calibrate implements Runnable {

        final Handler mmHandler;
        Calibrate(Handler handler) {
            mmHandler = handler;

        }

        public void run() {

            float offsetBenchmark = Float.valueOf(getResources().getString(R.string.offset_benchmark_value));
            float offsetFailedVal = Float.valueOf(getResources().getString(R.string.offset_failed_value));


            ArrayList<float[]> formattedDataFromDevice =  ManageData.getData(desiredListSizeSizeForCalibration);

            float[][] accelerometerOffsetData = formattedDataFromDevice.toArray(new float[][]
                    {new float[formattedDataFromDevice.size()]});


            float[] getAccelerationFromOneDimension = ManageData.getAccelerationFromRawData(accelerometerOffsetData, txyz);

            //offsetValue should be passed to next activity
            accelerationOffsetValue = ManageData.offsetAcceleration(getAccelerationFromOneDimension, offsetBenchmark, offsetFailedVal);

            if (accelerationOffsetValue == offsetFailedVal) {

                Log.d(TAG, "run: " + 0);
                Message failedMessage = mmHandler.obtainMessage(0);
                failedMessage.sendToTarget();

            } else {
                Log.d(TAG, "run: " + 1);

                Message successMessage = mmHandler.obtainMessage(1, accelerationOffsetValue);
                successMessage.sendToTarget();
            }

        }
    }

}
