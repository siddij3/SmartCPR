package com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smartcpr.trainer.smartcpr.BluetoothData.ManageData;
import com.smartcpr.trainer.smartcpr.MathOperationsClasses.FastFourierTransform;
import com.smartcpr.trainer.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.trainer.smartcpr.MathOperationsClasses.SpectralMathOps;
import com.smartcpr.trainer.smartcpr.ObjectClasses.Complex;

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

            Log.d(TAG, "fftSmooth: " + Arrays.toString(fftSmooth));
            Log.d(TAG, "freqbins: " + Arrays.toString(freqBins));

            Log.d(TAG, "peaksindex: " + Arrays.toString(peakIndexes));


            if (peakIndexes.length > 1) {
                thetaAngles = SpectralMathOps.phaseAngles(peakIndexes, fftPolarSingle);

                amplitudes = SpectralMathOps.peaksAmplitudesFromTransform(fftSmooth, peakIndexes);

                fundamentalFrequency = SpectralMathOps.fundamentalFrequency(peakIndexes, freqBins);

                Log.d(TAG, "run: fcc " + fundamentalFrequency);
                


                depth = SpectralMathOps.compressionDepth(amplitudes, peakIndexes.length, time, fundamentalFrequency, thetaAngles);
                Log.d(TAG, "depth: " + depth);
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
            Log.d(TAG, "run: \n");

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
