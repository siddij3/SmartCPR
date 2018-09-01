package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;

import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.FastFourierTransform;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SpectralMathOps;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Complex;
import com.smartcpr.junaid.smartcpr.R;
import com.smartcpr.junaid.smartcpr.SpectralAnalysisActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpectralAnalysis extends Thread {
    private final static String TAG = "SpectralAnalysisThread";

    ArrayList<float[]> formattedDataFromDevice;

    float[][] accelerometerRawData;

    private int desiredListSizeForCompression;
    private float accelerationOffset;

    private float[] time;
    private float[] acceleration;

    private static float GRAVITY;
    private static float OFFSET_BENCHMARK;
    private static float OFFSET_FAILED_VALUE;


    private int txyz;

    public SpectralAnalysis(int txyz,
                            float accelerationOffset,
                            float offsetBenchmark,
                            float offsetFailed,
                            float gravity) {



        this.txyz = txyz;
        this.accelerationOffset = accelerationOffset;

        GRAVITY = gravity;
        OFFSET_BENCHMARK = offsetBenchmark;
        OFFSET_FAILED_VALUE = offsetFailed;


        ExecuteAnalysis executeAnalysis = new ExecuteAnalysis();
        executeAnalysis.start();

    }

    class ExecuteAnalysis extends Thread {

        ExecuteAnalysis() {
            ManageData.clearList();

        }

    public void run() {
        double[] hanningAppliedValues;
        Complex[] baseComplexArray;
        Complex[] complexArrayFFTValues;
        Complex[] fftPolarSingle;

        double[] fftSmooth;
        double[] freqBins;
        int[] peakIndexes;

        double Fs;

        int N;

        double[] amplitudes;
        double[] thetaAngles;
        double fundamentalFrequency;

        double depth;
        double rate;

        //while (true) {}
        formattedDataFromDevice = ManageData.getData(desiredListSizeForCompression);
        ManageData.clearList();

        accelerometerRawData = formattedDataFromDevice.toArray(new float[][]
                {new float[formattedDataFromDevice.size()]});


        acceleration = ManageData.setAcceleration(accelerometerRawData, accelerationOffset, txyz, GRAVITY);
        time = ManageData.getScaledTimeArray(accelerometerRawData);

        N = time.length;
        Fs = 1/time[1];



        freqBins = SpectralMathOps.scaleFrequencyBins(N, Fs);


        hanningAppliedValues = SimpleMathOps.applyHanningWindow(acceleration, N);


        baseComplexArray = FastFourierTransform.baseComplexArrayWithWindow(hanningAppliedValues, N);
        complexArrayFFTValues = FastFourierTransform.simpleFFT(baseComplexArray);

        fftPolarSingle = FastFourierTransform.fftDoubleToSingle(complexArrayFFTValues, N, 2);
        fftSmooth = FastFourierTransform.smoothFFTValues(fftPolarSingle, N);

        peakIndexes = new int[] {102, 205};  //SpecctralMathOps.getPeaksIndexes(fftSmooth, 2);

        amplitudes = SpectralMathOps.peaksAmplitudesFromTransform(fftSmooth, peakIndexes);

        thetaAngles = SpectralMathOps.phaseAngles(peakIndexes, fftPolarSingle);
        fundamentalFrequency = SpectralMathOps.fundamentalFrequency(peakIndexes, freqBins);

        depth = SpectralMathOps.compressionDepth(amplitudes, peakIndexes.length, time, fundamentalFrequency, thetaAngles);
        rate = SpectralMathOps.compressionRate(fundamentalFrequency);

        Log.d(TAG, "run: " + depth);
        Log.d(TAG, "run: " + rate);

    }




    }


}
