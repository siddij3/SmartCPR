package com.smartcpr.junaid.smartcpr.MathOperationsClasses;

import com.smartcpr.junaid.smartcpr.ObjectClasses.Complex;

public class SpectralAnalysis {

    static double[] scaleFrequencyBins(int N, double frequency) {
        double[] freqBin = new double[(int)N/2];

        for (int i = 0; i < N/2; i ++)
            freqBin[i] = i*frequency/N;

        return freqBin;

    }

    static double getfundamentalFrequency(int[] indexes, double[] freqBins) {
        return freqBins[indexes[0]];
    }

    static double[] phaseAngles(int[] indexes, Complex[] fftPolarSingle) {
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

    static double[] peaksFromTransform(double[] fftSmooth, int[] peakIndex) {
        double[] amplitude = new double[peakIndex.length];

        for (int i = 0; i < peakIndex.length; i++)
            amplitude[i] = fftSmooth[peakIndex[i]];

        return amplitude;
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

            tmp[i] = ((i+1) * 2.0 * PI * fundamentalFrequency) ;
            tmp[i] *= tmp[i];

            S_k[i] = 100*(A_k[i]/tmp[i]);

            phiAngle[i] = PI + thetaAngles[i];

        }

        tmp = new double[lenHarmonics];

        for (int i =0; i < lenHarmonics; i++)  {
            for (int j =0; j < lenTime; j++) {
                if (i == 0)
                    sofT[j] = S_k[i] * Math.cos(2 * PI * (i+1) *fundamentalFrequency * time[j] + phiAngle[i]);
                else
                    sofT[j] += S_k[i] * Math.cos(2 * PI * (i+1) *fundamentalFrequency * time[j] + phiAngle[i]);
            }

        }


        double depth = SimpleMathOps.getMaxValue(sofT) - SimpleMathOps.getMinValue(sofT);

        return depth;
    }


    static double compressionRate(double fcc) {return fcc*60; }
}
