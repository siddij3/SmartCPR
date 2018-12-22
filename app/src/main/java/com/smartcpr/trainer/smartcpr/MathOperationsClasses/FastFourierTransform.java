package com.smartcpr.trainer.smartcpr.MathOperationsClasses;
import com.smartcpr.trainer.smartcpr.ObjectClasses.Complex;


/**
 * FastFourierTransform
 *
 * Note: Complex is a custom class; each object is a 2-element array with [0] real and [1] imaginary
 *
 * simpleFFT: Performs main function of FFT (Princeton)
 *            Params:
 *                    x: An array of complex numbers (array of 2-element arrays)
 *
 * setArraySizeExponentOfTwo: I legit don't know why I have it. I don't use it
 *                             cuz the sample-sizes are hard-coded powers of 2 *
 *                            Params:
 *                                  arraySize: the size of the array for FFT input.
 *
 *
 *
 *  baseComplexArrayWithWindow: Converts values from acceleration data into a complex array for FFT
 *                             Params:
 *                                   hanningAppliedValues: acceleration values with hanning window
 *                                                          applied as per processing requirements
 *                                   N: size of array (set to a power of 2)
 *
 *  fftDoubleToSingle: Scales FFT values using first half of the calculated values given the
 *                     symmetry that FFT produces
 *                     Params:
 *                             complexArrayFFTValues: Values straight from simpleFFT
 *                             N: size of array (set to a power of 2)
 *                             scalingValue: Used to multiply the amplitudes of the FFT values by 2
 *
 *  smoothFFTValues: Takes the absolute values of each element of the first half the items in the
 *                    transformed domain (FFT values)
 *                    Params:
 *                          fftPolarSingle: first half of symmetrical values in transformed domain
 *                          N:  size of array of acceleration values (set to a power of 2)
 *
 *
 * show: Shows items in the complex or transformed domain
 *      Params:
 *              x: x items in the array
 *              title: identifier for debugging
 *
 *
 * Source: https://introcs.cs.princeton.edu/java/97data/FFT.java.html  for simpleFFT and Complex #s
 */

public class FastFourierTransform {
    public static Complex[] simpleFFT(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) return new Complex[] { x[0] };

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) { throw new IllegalArgumentException("N is not a power of 2"); }

        // fft of even terms
        Complex[] even = new Complex[N/2];
        for (int k = 0; k < N/2; k++) {
            even[k] = x[2*k];
        }
        Complex[] q = simpleFFT(even);

        // fft of odd terms
        Complex[] odd  = even;  // reuse the array
        for (int k = 0; k < N/2; k++) {
            odd[k] = x[2*k + 1];
        }
        Complex[] r = simpleFFT(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N/2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k]       = q[k].plus(wk.times(r[k]));
            y[k + N/2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    public static int setArraySizeExponentOfTwo(int arraySize) {
        float getExponentBase = (float) (Math.log(arraySize)/Math.log(2.0));

        if(Math.floor(getExponentBase) == getExponentBase) {
            return arraySize;
        } else {

            return (int) Math.pow(2, Math.round(getExponentBase));
        }


    }

    public static Complex[] baseComplexArrayWithWindow(double hanningAppliedValues[], int N) {
        Complex[] baseComplexArray = new Complex[N];

        for (int i = 0; i < N; i++)
            baseComplexArray[i] = new Complex(hanningAppliedValues[i], 0);

        return baseComplexArray;
    }

    public static Complex[] fftDoubleToSingle(Complex[] complexArrayFFTValues, int N, int scalingValue) {
        Complex[] fftPolarSingle = new Complex[N/2];

        for (int i = 0 ; i < (N/2) ; i++) {
            fftPolarSingle[i] = complexArrayFFTValues[i+1].divides(new Complex(N, 0)).scale(scalingValue);
        }

        return fftPolarSingle;

    }

    public static double[] smoothFFTValues(Complex[] fftPolarSingle,int N) {
        double[] fftSmooth = new double[N / 2];

        for (int i = 0; i < (N / 2); i++) {
            fftSmooth[i] = fftPolarSingle[i].abs();
            //fftSmooth[i] *= 2;

        }

        return fftSmooth;

    }


    // display an array of Complex numbers to standard output
    public static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (Complex aX : x)
            System.out.println(aX);

        System.out.println();
    }

    public static void show(double[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (double aX : x)
            System.out.println(aX);

        System.out.println();
    }

}
