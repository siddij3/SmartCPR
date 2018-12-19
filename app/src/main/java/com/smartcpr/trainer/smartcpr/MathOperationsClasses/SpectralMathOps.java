package com.smartcpr.trainer.smartcpr.MathOperationsClasses;

import android.util.Log;

import com.smartcpr.trainer.smartcpr.ObjectClasses.Complex;

import java.util.ArrayList;


public class SpectralMathOps {

    private final static  String   TAG = "SpectralMathOps";

    public static double[] scaleFrequencyBins(int N, double frequency) {
        double[] freqBin = new double[N /2];

        for (int i = 0; i < N/2; i ++)
            freqBin[i] = i*frequency/N;

        return freqBin;

    }

    public static double fundamentalFrequency(int[] indexes, double[] freqBins) {
        return freqBins[indexes[0]];
    }

    public static double fundamentalFrequency(int indexes, double[] freqBins) {
        return freqBins[indexes];
    }

    public static int[] peaks(double[] fftSmooth) {

        int min_dist = 3;
        int numPeaks = 0;

        double maxVal = SimpleMathOps.getMaxValue(fftSmooth);


        ArrayList<Integer> roots = new ArrayList<>();

        int[] cyclical = new int[3];

        int distBetweenPeaks = 2;

        // Traverse all the values until 3 peaks which fulfill the criteria are
        // found using a cyclical array.
        for (int i = 0; i < fftSmooth.length && numPeaks < 3; i++) {
            int temp = cyclical[1];
            cyclical[1] = cyclical[2];
            cyclical[0] = temp;
            cyclical[2] = i;
            if (cyclical[1] > cyclical[0] && cyclical[1] < cyclical[2]) {

                if ((fftSmooth[cyclical[1]] > fftSmooth[cyclical[0]] + 0.2) &&
                        (fftSmooth[cyclical[1]] > fftSmooth[cyclical[2]] + 0.2) &&
                        (distBetweenPeaks > min_dist) &&
                        fftSmooth[cyclical[1]] > maxVal/4 ) {

                    roots.add(cyclical[1]);
                    numPeaks++;
                    distBetweenPeaks = 0;
                } else {
                    distBetweenPeaks++;
                }
            }
        }

        int[] peaks = convertIntegers(roots);

        if (roots.size() == 0)
            return new int[1];

        return peaks;
    }

    // Credits to: https://stackoverflow.com/a/718558
    private static int[] convertIntegers(ArrayList<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i);
        }
        return ret;
    }

    public static double[] phaseAngles(int[] indexes, Complex[] fftPolarSingle) {
        int len = indexes.length;

        Complex[] z = new Complex[len];
        double[] theta = new double[len];

        if (len > 1) {
            for (int i = 0; i < len; i++) {
                z[i] = fftPolarSingle[indexes[i]];
                theta[i] = Math.atan2(z[i].im(), z[i].re());
            }
        }

        return theta;

    }


    public static double phaseAngles(int indexes, Complex[] fftPolarSingle) {
        Complex z = fftPolarSingle[indexes];

        return Math.atan2(z.im(), z.re());
    }

    public static double[] peaksAmplitudesFromTransform(double[] fftSmooth, int[] peakIndex) {
        double[] amplitude = new double[peakIndex.length];

        for (int i = 0; i < peakIndex.length; i++)
            amplitude[i] = fftSmooth[peakIndex[i]];

        return amplitude;
    }

    public static double peaksAmplitudesFromTransform(double[] fftSmooth, int peakIndex) {

        return fftSmooth[peakIndex];
    }

    public static double compressionDepth(double[] amplitude, int lenHarmonics, float[] time, double fundamentalFrequency, double[] thetaAngles) {
        int lenTime = time.length;
        final double PI = Math.PI;

        double[] A_k = new double[lenHarmonics];

        double[] tmp = new double[lenHarmonics];
        double[] S_k = new double[lenHarmonics];

        double[] sofT = new double[lenTime];

        double[] phiAngle = new double[lenHarmonics];

        for (int i = 0; i < lenHarmonics; i ++){
            A_k [i] = amplitude[i];

            //Log.d("SpectralAnalysisThread", "compressionDepth: A_k " + A_k[i]);

            tmp[i] = ((i+1)) * 2.0 * PI * fundamentalFrequency ;
            tmp[i] = Math.pow(tmp[i], 2);

          //  Log.d("SpectralAnalysisThread", "compressionDepth: tmp " + tmp[i]);

            S_k[i] = 100*A_k[i]/tmp[i];

          //  Log.d("SpectralAnalysisThread", "compressionDepth:  S-K " + S_k[i]);

            phiAngle[i] = PI + thetaAngles[i];

        }

        for (int i =0; i < lenHarmonics; i++)  {
            for (int j =0; j < lenTime; j++) {
                if (i == 0)
                    sofT[j] = S_k[i] * Math.cos(2 * PI * (i+1) *fundamentalFrequency * time[j] + phiAngle[i]);
                else
                    sofT[j] += S_k[i] * Math.cos(2 * PI * (i+1) *fundamentalFrequency * time[j] + phiAngle[i]);
            }

        }


        return SimpleMathOps.getMaxValue(sofT) - SimpleMathOps.getMinValue(sofT);
    }

    public static double compressionDepth(double amplitude, float[] time, double fundamentalFrequency, double thetaAngles) {
        int lenTime = time.length;
        final double PI = Math.PI;

        double A_k;

        double tmp;
        double S_k;

        double phiAngle;

        double[] sofT = new double[lenTime];

        A_k = amplitude;



        tmp = ( 2.0 * PI * fundamentalFrequency) ;
        tmp = Math.pow(tmp, 2);

        S_k = 100*(A_k/(tmp));

        phiAngle = PI + thetaAngles;

        for (int j =0; j < lenTime; j++)
            sofT[j] = S_k * Math.cos(2 * PI  *fundamentalFrequency * time[j] + phiAngle);


        return SimpleMathOps.getMaxValue(sofT) - SimpleMathOps.getMinValue(sofT);
    }


   public static double compressionRate(double fcc) {return fcc*60; }
}
