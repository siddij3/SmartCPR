package com.smartcpr.junaid.smartcpr.MathOperationsClasses;

public class SpectralAnalysisOps {

    public static double[] applyHanningWindow(float[] acceleration, int windowSize) {
        double[] window = new double[windowSize];
        double[] hanningApplied = new double[windowSize];

        for (int i = 0; i < windowSize; i ++) {
            double tmp = (0.5 - 0.5*Math.cos(2.0 * Math.PI * i/ (windowSize - 1)));

            window[i] = tmp;
        }

        double coherentGain = SimpleMathOps.getSumOfArray(window) /  windowSize;
        System.out.println(coherentGain);


        for (int i = 0; i < windowSize; i++) {
            hanningApplied[i] = acceleration[i] * window[i] / coherentGain;
        }

        return hanningApplied;
    }
}
