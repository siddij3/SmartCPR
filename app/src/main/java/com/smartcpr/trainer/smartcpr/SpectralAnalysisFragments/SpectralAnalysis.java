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


/**
 * SpectralAnalysis (Thread)
 *
 * Note: Complex is a custom class; each object is a 2-element array with [0] real and [1] imaginary
 *
 * SpectralAnalysis (Constructor): Initializer of variables needed to run an instance of the thread
 *            Params:
 *                    txyz: index for z-axis acceleration
 *                    desiredListSizeForCompression: Desired list size for number of data points
 *                                                  in each set
 *                    accelerationOffset: Offset to set averaged stationary acceleration to 0
 *                    handler: For sending messages between threads (this thread to main thread)
 *                    GRAVITY: 9.8 m/s^2
 *
 *
 * run: Performs spectral analysis algorithm as described in paper
 *
 * "A New Method for Feedback on the Quality of Chest Compressions during
 * Cardiopulmonary Resuscitation" Digna M. González-Otero, Jesus Ruiz, Sofía Ruiz de Gauna,
 * Unai Irusta, Unai Ayala, and Erik Alonso
 *
 * Implementation of algorithm completed by Junaid Siddiqui, November 2017 under the supervision
 * of Dist. Prof. Jamal Deen from McMaster University,
 * and the help of Chris Williams, BAsc UWaterloo
 *
 *
 */
public class SpectralAnalysis implements Runnable {
    private final static String TAG = "SpectralAnalysisThread";


    // variables for spectral analysis. I think the names are intuitive

    // Raw data from the IMU
    private float[][] accelerometerRawData;

    // specifies the size of the data-set for compressions and calculations (not too long or short)
    private final int desiredListSizeForCompression;
    private final float accelerationOffset;
    private final int txyz;

    private final float GRAVITY;

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
        // Organizes different steps of the spectral analysis process containing in-between data
        double[] hanningAppliedValues;
        Complex[] baseComplexArray;
        Complex[] complexArrayFFTValues;
        Complex[] fftPolarSingle;

        // Final data holders (fftSmooth vs freqBins) (y vs x)
        double[] fftSmooth;
        double[] freqBins;
        int[] peakIndexes;

        // frequency intervals
        double Fs;

        // Number of datapoints in set
        int N;

        // Used for finding final depth and rate values
        double[] amplitudes;
        double[] thetaAngles;
        double fundamentalFrequency;

        double depth;
        double rate;

        // Begin Spectral Analysis
        while (true) {
            ArrayList<float[]> formattedDataFromDevice = ManageData.getData(
                                                        desiredListSizeForCompression);

            accelerometerRawData = formattedDataFromDevice.toArray(new float[][]
                                    {new float[formattedDataFromDevice.size()]});

            // breaks IMU data into acceleration and time for easy using
            float[] acceleration = ManageData.setAcceleration(accelerometerRawData,
                                                                accelerationOffset,
                                                                txyz,
                                                                GRAVITY);
            float[] time = ManageData.getScaledTimeArray(accelerometerRawData);

            // Time (s) and freqency (Hz) things
            N = time.length;
            Fs = 1/ time[1];

            freqBins = SpectralMathOps.scaleFrequencyBins(N, Fs);

            // TODO make this more efficient by putting them into fewer functions

            // Lotta things bud. Does Hanning Window, FFT, uses one half of the double
            // polar symmetric FFT, then takes the absolute values and scales it (multiply by 2)
            // then applies a peak detection algorithm to find first 3 notable peaks

            hanningAppliedValues = SimpleMathOps.applyHanningWindow(acceleration, N);

            baseComplexArray = FastFourierTransform.baseComplexArrayWithWindow(hanningAppliedValues, N);
            complexArrayFFTValues = FastFourierTransform.simpleFFT(baseComplexArray);

            fftPolarSingle = FastFourierTransform.fftDoubleToSingle(complexArrayFFTValues, N, 2);

            fftSmooth = FastFourierTransform.smoothFFTValues(fftPolarSingle, N);

            peakIndexes = SpectralMathOps.peaks(fftSmooth);

          //  Log.d(TAG, "fftSmooth: " + Arrays.toString(fftSmooth));
          //  Log.d(TAG, "f_bins: " + Arrays.toString(freqBins));
          //  Log.d(TAG, "peaksIndex: " + Arrays.toString(peakIndexes));

            // Uses peaks to find compression fundamental frequency and another algorithm
            // to find the depths of the compressions
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

            // Sends depth and rate to main thread

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
