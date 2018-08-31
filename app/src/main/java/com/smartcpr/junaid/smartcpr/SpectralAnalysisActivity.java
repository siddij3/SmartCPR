package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CompressionsButtonFragment;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.FastFourierTransform;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Complex;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Victim;
import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.CompressionRateFragment;

import java.util.ArrayList;
import java.util.Objects;

public class SpectralAnalysisActivity extends AppCompatActivity
        implements CompressionsButtonFragment.CompressionsButtonListener {

    private final static String TAG = "SpectralAnalysisActive";

    private int adultMinRate;
    private int adultMaxRate;

    private int youthMinRate;
    private int youthMaxRate;

    private int childMinRate;
    private int childMaxRate;

    private int infantMinRate;
    private int infantMaxRate;


    public static final String EXTRA_OFFSET_ACCELERATION_VALUE
            = "com.smartcpr.junaid.smartcpr.offsetaccelerationvalue";

    private static float offsetAcceleration;

    private int txyz;
    private int desiredListSizeForCompression;

    float[] time;
    float[] acceleration;

    CompressionRateFragment compressionRateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);
        Log.d(TAG, "onCreate: ");

        offsetAcceleration = Float.valueOf(unPackBundle());

        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeForCompression = getResources().getInteger(R.integer.compression_list_size);


    }

    @Override
    public void cprVictim(String strCprVictim) {
        //TODO This probably wont transfer the victim over to subsequent activities. Keep this in mind
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

    }

    private String unPackBundle() {
        //Unpacks bundled info from ScanDevicesActivity
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        return Objects.requireNonNull(bundle).getString(EXTRA_OFFSET_ACCELERATION_VALUE);

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


    private void getIMUData() {
        ArrayList<float[]> formattedDataFromDevice =  ManageData.getData(desiredListSizeForCompression);

        float[][] accelerometerRawData = formattedDataFromDevice.toArray(new float[][]
                {new float[formattedDataFromDevice.size()]});


        acceleration = ManageData.getAccelerationFromRawData(accelerometerRawData, txyz);
        time = ManageData.getScaledTimeArray(accelerometerRawData);


        if (offsetAcceleration == 1){

        }
    }

    private void performSpectralAnalysis(float[] time, float[] acceleration) {
        int N = time.length;

        //May not need this thing
        N = FastFourierTransform.setArraySizeExponentOfTwo(N);
        Log.d(TAG, "performSpectralAnalysis: Array Size " + N);


        double hanningAppliedValues[] = SimpleMathOps.applyHanningWindow(acceleration, N);


        Complex[] baseComplexArray = FastFourierTransform.baseComplexArrayWithWindow(hanningAppliedValues, N);
        Complex[] complexArrayFFTValues = FastFourierTransform.simpleFFT(baseComplexArray);

        Complex[] fftPolarSingle = FastFourierTransform.fftDoubleToSingle(complexArrayFFTValues, N, 2);
        double[] fftSmooth = FastFourierTransform.smoothFFTValues(fftPolarSingle, N);


        //        double[] amplitudes = MathOps.peaksFromTransform(fftSmooth, peaks);
        //
        //        double[] thetaAngles = MathOps.phaseAngles(peaks, fftPolarSingle);
        //        double fundamentalFrequency = MathOps.getfundamentalFrequency(peaks, freqBins);
        //
        //        double depth = MathOps.compressionDepth(amplitudes, peaks.length, scaledTime, fundamentalFrequency, thetaAngles);
        //        double rate = MathOps.compressionRate(fundamentalFrequency);

    }


}
