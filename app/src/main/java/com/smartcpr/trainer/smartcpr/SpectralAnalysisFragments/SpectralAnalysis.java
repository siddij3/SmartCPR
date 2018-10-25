package com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smartcpr.trainer.smartcpr.BluetoothData.ManageData;
import com.smartcpr.trainer.smartcpr.MathOperationsClasses.FastFourierTransform;
import com.smartcpr.trainer.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.trainer.smartcpr.MathOperationsClasses.SpectralMathOps;
import com.smartcpr.trainer.smartcpr.ObjectClasses.Complex;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SpectralAnalysis implements Runnable {
    private final static String TAG = "SpectralAnalysisThread";

    private float[][] accelerometerRawData;

    private final int desiredListSizeForCompression;
    private final float accelerationOffset;

    private final float GRAVITY;

    private final int txyz;

    private final Handler mHandler;


    public SpectralAnalysis(int txyz, int desiredListSizeForCompression, float accelerationOffset,
                            Handler handler, float GRAVITY) {

        this.txyz = txyz;
        this.mHandler= handler;
        this.desiredListSizeForCompression = desiredListSizeForCompression;
        this.accelerationOffset = accelerationOffset;
        this.GRAVITY = GRAVITY;

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

        while (true) {
            ArrayList<float[]> formattedDataFromDevice = ManageData.getData(desiredListSizeForCompression);

            accelerometerRawData = formattedDataFromDevice.toArray(new float[][]
                    {new float[formattedDataFromDevice.size()]});


            float[] acceleration = ManageData.setAcceleration(accelerometerRawData, accelerationOffset, txyz, GRAVITY);
            float[] time = ManageData.getScaledTimeArray(accelerometerRawData);


            N = time.length;
            Fs = 1/ time[1];

            freqBins = SpectralMathOps.scaleFrequencyBins(N, Fs);


            hanningAppliedValues = SimpleMathOps.applyHanningWindow(acceleration, N);

            baseComplexArray = FastFourierTransform.baseComplexArrayWithWindow(hanningAppliedValues, N);
            complexArrayFFTValues = FastFourierTransform.simpleFFT(baseComplexArray);

            fftPolarSingle = FastFourierTransform.fftDoubleToSingle(complexArrayFFTValues, N, 2);

            fftSmooth = FastFourierTransform.smoothFFTValues(fftPolarSingle, N);

            peakIndexes = SpectralMathOps.peaks(fftSmooth);

          //  Log.d(TAG, "fftSmooth: " + Arrays.toString(fftSmooth));
          //  Log.d(TAG, "f_bins: " + Arrays.toString(freqBins));

          //  Log.d(TAG, "peaksIndex: " + Arrays.toString(peakIndexes));


            if (peakIndexes.length > 1) {
                thetaAngles = SpectralMathOps.phaseAngles(peakIndexes, fftPolarSingle);

                amplitudes = SpectralMathOps.peaksAmplitudesFromTransform(fftSmooth, peakIndexes);

                fundamentalFrequency = SpectralMathOps.fundamentalFrequency(peakIndexes, freqBins);

                Log.d(TAG, "run: fcc ASDF " + fundamentalFrequency);
                


                depth = SpectralMathOps.compressionDepth(amplitudes, peakIndexes.length, time, fundamentalFrequency, thetaAngles);
                Log.d(TAG, "depth: ASDF " + depth);
                rate = SpectralMathOps.compressionRate(fundamentalFrequency);

            } else if (peakIndexes.length == 1) {
                thetaAngles = new double[peakIndexes.length];
                thetaAngles[0] = SpectralMathOps.phaseAngles(peakIndexes[0], fftPolarSingle);

                amplitudes = new double[peakIndexes.length];
                amplitudes[0] = SpectralMathOps.peaksAmplitudesFromTransform(fftSmooth, peakIndexes[0]);

                fundamentalFrequency = SpectralMathOps.fundamentalFrequency(peakIndexes[0], freqBins);

                Log.d(TAG, "run: Amplitude " + fftSmooth[peakIndexes[0]]);


                depth = SpectralMathOps.compressionDepth(amplitudes[0], time, fundamentalFrequency, thetaAngles[0]);
                rate = SpectralMathOps.compressionRate(fundamentalFrequency);


            } else {
                depth = 0;
                rate = 0;
            }

            //Write to record here

            if (Double.isNaN(depth)) {
                depth = 0.0;
            }

            String msg = depth + "," + rate;

            Message message =  mHandler.obtainMessage(0, msg);

            message.sendToTarget();
        }

    }


    //TODO do something with this?
    boolean isDeviceIdle(float[] acceleration) {
        double tmp = SimpleMathOps.getMaxValue(acceleration) - SimpleMathOps.getMinValue(acceleration);


        return Math.abs(tmp) <= 0.9;

    }



}