package com.smartcpr.junaid.smartcpr.MathOperationsClasses;

import com.smartcpr.junaid.smartcpr.ObjectClasses.Complex;

public class SimpleMathOps {
    public static float getMaxValue(float[] array) {
        float maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }


    static double getMaxValue(double[] array) {
        double maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
            }
        }
        return maxValue;
    }


    static double getMinValue(double[] array) {
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }

    // getting the miniumum value
    public static float getMinValue(float[] array) {
        float minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
            }
        }
        return minValue;
    }

    public static float getMeanValue(float[] array) {
        float sum = 0;
        for (int i = 1; i < array.length; i++) {
            sum += array[i];
        }
        return sum / array.length;
    }

    static float getSumofArray(double[] emptyWindow) {
        float sum = 0;
        for (double val : emptyWindow) {
            //System.out.println(val);
            sum += val;
        }

        return sum;
    }

    public static double[] applyHanningWindow(float[] acceleration, int windowSize) {
        double[] window = new double[windowSize];
        double[] hanningApplied = new double[windowSize];

        for (int i = 0; i < windowSize; i++) {
            double tmp = (0.5 - 0.5 * Math.cos(2.0 * Math.PI * i / (windowSize - 1)));

            window[i] = tmp;
        }

        double coherentGain = getSumofArray(window) / windowSize;

        for (int i = 0; i < windowSize; i++) {
            hanningApplied[i] = acceleration[i] * window[i] / coherentGain;
        }

        return hanningApplied;
    }

    public static double[] peakIndexes() {
        return new double[0];
    }


}
