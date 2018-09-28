package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.FastFourierTransform;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SpectralMathOps;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Complex;

import java.util.ArrayList;
import java.util.Arrays;

public class SpectralAnalysis implements Runnable {
    private final static String TAG = "SpectralAnalysisThread";

    private ArrayList<float[]> formattedDataFromDevice;

    private float[][] accelerometerRawData;

    private int desiredListSizeForCompression;
    private float accelerationOffset;

    private float[] time;
    private float[] acceleration;

    private static float GRAVITY;

    private int txyz;

    private Handler mHandler;


    public SpectralAnalysis(int txyz, float gravity, int desiredListSizeForCompression, float accelerationOffset, Handler handler) {

        this.txyz = txyz;
        this.mHandler= handler;
        this.desiredListSizeForCompression = desiredListSizeForCompression;
        this.accelerationOffset = accelerationOffset;
        GRAVITY = gravity;

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
            formattedDataFromDevice = ManageData.getData(desiredListSizeForCompression);

            accelerometerRawData = formattedDataFromDevice.toArray(new float[][]
                    {new float[formattedDataFromDevice.size()]});


            acceleration = ManageData.setAcceleration(accelerometerRawData, accelerationOffset, txyz, GRAVITY);
            time = ManageData.getScaledTimeArray(accelerometerRawData);


            //if (isDeviceIdle(acceleration))
              //  continue;


            N = time.length;
            Fs = 1/time[1];

            freqBins = SpectralMathOps.scaleFrequencyBins(N, Fs);


            hanningAppliedValues = SimpleMathOps.applyHanningWindow(acceleration, N);

            baseComplexArray = FastFourierTransform.baseComplexArrayWithWindow(hanningAppliedValues, N);
            complexArrayFFTValues = FastFourierTransform.simpleFFT(baseComplexArray);

            fftPolarSingle = FastFourierTransform.fftDoubleToSingle(complexArrayFFTValues, N, 2);

            fftSmooth = FastFourierTransform.smoothFFTValues(fftPolarSingle, N);

            peakIndexes = SpectralMathOps.peaks(fftSmooth);

            if (peakIndexes.length > 1) {
                thetaAngles = SpectralMathOps.phaseAngles(peakIndexes, fftPolarSingle);

                amplitudes = SpectralMathOps.peaksAmplitudesFromTransform(fftSmooth, peakIndexes);

                fundamentalFrequency = SpectralMathOps.fundamentalFrequency(peakIndexes, freqBins);

                Log.d(TAG, "run: fcc " + fundamentalFrequency);

                Log.d(TAG,  Arrays.toString(fftSmooth));
                Log.d(TAG,  Arrays.toString(freqBins));

                for (int i = 0; i < peakIndexes.length ; i++) {
                    Log.d(TAG, "run: Amplitude " + fftSmooth[peakIndexes[i]]);

                    Log.d(TAG, "run: Frequency "+ String.valueOf(freqBins[peakIndexes[i]]));
                }

                depth = SpectralMathOps.compressionDepth(amplitudes, peakIndexes.length, time, fundamentalFrequency, thetaAngles);
                rate = SpectralMathOps.compressionRate(fundamentalFrequency);

            } else if (peakIndexes.length == 1) {
                thetaAngles = new double[peakIndexes.length];
                thetaAngles[0] = SpectralMathOps.phaseAngles(peakIndexes[0], fftPolarSingle);

                amplitudes = new double[peakIndexes.length];
                amplitudes[0] = SpectralMathOps.peaksAmplitudesFromTransform(fftSmooth, peakIndexes[0]);

                fundamentalFrequency = SpectralMathOps.fundamentalFrequency(peakIndexes[0], freqBins);

                Log.d(TAG, "run: Amplitude " + fftSmooth[peakIndexes[0]]);
                Log.d(TAG, "run: Frequency "+ String.valueOf(freqBins[peakIndexes[0]]));


                depth = SpectralMathOps.compressionDepth(amplitudes[0], time, fundamentalFrequency, thetaAngles[0]);
                rate = SpectralMathOps.compressionRate(fundamentalFrequency);



            } else {
                depth = 0;
                rate = 0;
            }


            Message messageDepth =  mHandler.obtainMessage(0, depth);
            Message messageRate =  mHandler.obtainMessage(1, rate);

            messageDepth.sendToTarget();
            messageRate.sendToTarget();
        }
    }



    boolean isDeviceIdle(float[] acceleration) {
        double tmp = SimpleMathOps.getMaxValue(acceleration) - SimpleMathOps.getMinValue(acceleration);


        return Math.abs(tmp) <= 0.9;

    }



}
