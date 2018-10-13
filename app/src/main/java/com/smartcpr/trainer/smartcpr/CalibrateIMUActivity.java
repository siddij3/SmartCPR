package com.smartcpr.trainer.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;

import com.smartcpr.junaid.smartcpr.R;
import com.smartcpr.trainer.smartcpr.BluetoothData.ManageData;
import com.smartcpr.trainer.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;
import com.smartcpr.trainer.smartcpr.CalibrateIMUFragments.CompressionsButtonFragment;
import com.smartcpr.trainer.smartcpr.CalibrateIMUFragments.InputUserNameFragment;
import com.smartcpr.trainer.smartcpr.ObjectClasses.Victim;

import java.util.ArrayList;
import java.util.Objects;

public class CalibrateIMUActivity extends AppCompatActivity
        implements CompressionsButtonFragment.CompressionsButtonListener, InputUserNameFragment.EditTextListener  {

    private int adultMinRate;
    private int adultMaxRate;

    private int youthMinRate;
    private int youthMaxRate;

    private int childMinRate;
    private int childMaxRate;

    private int infantMinRate;
    private int infantMaxRate;

    private float accelerationOffsetValue;

    private final static String TAG = "CalibrateIMUActivity";
    private String calibrationResultMessage;

    Victim victim;

    private int txyz;
    private int desiredListSizeSizeForCalibration;

    private String userName;
    private EditText tInputUserName;

    private CalibratedIMUFragment calibratedIMUFragment;
    private CompressionsButtonFragment compressionsButtonFragment;
    private InputUserNameFragment inputUserNameFragment;

    private Handler mHandler;

    Calibrate calibrate;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrating_imu_prompt);
        Log.d(TAG, "onCreate: " + TAG);


        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeSizeForCalibration = getResources().getInteger(R.integer.device_calibration_array_size);

        calibratedIMUFragment = (CalibratedIMUFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_calibrating_imu);
        compressionsButtonFragment = (CompressionsButtonFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_button_begin_compressions);
        inputUserNameFragment = (InputUserNameFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_edit_text_user_name);


        compressionsButtonFragment.makeButtonClickable(false);
        inputUserNameFragment.disableEditText();

        handleMessage();

        calibrate = new Calibrate(mHandler);
        calibrateDevice();

    }

    private void calibrateDevice() {
        new Thread(calibrate).start();
    }

    void handleMessage() {
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                int result = message.what;

                if (result == 1) {
                    calibrationResultMessage = getResources().getString(R.string.calibration_complete_message);
                    sendCalibrationMessage(calibrationResultMessage);

                    inputUserNameFragment.enableEditText();

                    //compressionsButtonFragment.makeButtonClickable(true);

                } else if (result == 0) {
                    calibrationResultMessage = getResources().getString(R.string.calibration_failed_message);
                    sendCalibrationMessage(calibrationResultMessage);

                    calibrateDevice();
                }


            }
        };
    }


    @Override
    public void enableButton(EditText editText) {
        tInputUserName = editText;
        compressionsButtonFragment.makeButtonClickable(true);
    }


    @Override
    public void cprVictim(String strCprVictim) {
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

    private void getInputFromEditText() {
        userName = tInputUserName.getText().toString();
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

        getInputFromEditText();
        bundle.putString(SpectralAnalysisActivity.EXTRA_USER_NAME,
                String.valueOf(userName));

        intent.putExtras(bundle);

        startActivity(intent);
    }


    private void sendCalibrationMessage(String message) {
        calibratedIMUFragment.setCalibratingMessageFeedback(message);

    }


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
