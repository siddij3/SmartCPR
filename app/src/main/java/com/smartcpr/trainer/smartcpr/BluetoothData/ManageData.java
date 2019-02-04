package com.smartcpr.trainer.smartcpr.BluetoothData;

import android.util.Log;

import com.smartcpr.trainer.smartcpr.MathOperationsClasses.SimpleMathOps;

import java.util.ArrayList;
import java.util.List;

/**
 * ManageData
 *
 * Functions to Manage data from IMU. Used in calibration and spectral analysis as a way to not
 * block threads but have consistent information throughout the app
 *
 * Functions:
 *
 *  getData: Using desired list size, loops collecting data streams (blocking loop)
 *           and passes it off to format data function, then clears the main synchronized BT list.
 *           formatted data is returned as a list of array items of length 4 {time, x, y, z}
 *           Params:
 *                    desiredListSize - makes the data with the desired length
 *
 *  formatData: Splits the raw IMU data and breaks it into an array with time, x, y and z values
 *              Params:
 *                      string - a raw line of IMU data eg. "123, 0.4, 1.0, -3.9"
 *                      returns { 123, 0.4, 1.0, -3.9 } of type float
 *
 *  getAccelerationFromRawData: Gets the z - dimension acceleration and puts it into an array,
 *                              returns an array of type float.
 *                              Params:
 *                                      array2D - 2D array of formatted data from IMU
 *                                      txyz - index size for z acceleration item
 *
 *  setAcceleration: Sets the base acceleration to 0 and multiplies it by 9.8 for
 *                   a relative value with gravity included (why, I still don't know, but it works)
 *                   Uses acceleration for Fast Fourier Transform and then spectral analysis
 *                   Params:
 *                           array2D - 2D array of formatted data from IMU
 *                           txyz - index size for z acceleration item
 *                           offsetVal - acceleration offset value
 *                           GRAVITY -  9.8 m/s^2
 *
 *  offsetAcceleration: Uses accumulated acceralation data and averages it to then subtract
 *                      to mitigate noise acceleration data
 *                      Params:
 *                              rawAcceleration - raw z-acceleration from IMU
 *                              offsetBenchmark - Used if the IMU is moving or not. relatively
 *                                                low benchmark
 *                              offsetFailedVal - Used to notify main function if the IMU is moving
 *
 *  getScaledTimeArray: Gets first item of the list for time, substracts then divides by 1000
 *                      to get seconds from milliseconds
 *                      Params:
 *                          array2D - 2D array of formatted data from IMU
 *
 */


public class ManageData {

    private static final String TAG = "ManageDataClass";

    public static void clearList() {
        BluetoothDeviceData.returnEmptyList();
    }

    public static ArrayList<float[]> getData(int desiredListSize) {
        List<String> listRawDeviceData =  BluetoothDeviceData.getList();

        //Eliminating item at first index due to incomplete line reduces array size for fftB
        desiredListSize++;

        if (listRawDeviceData.size() <= desiredListSize) {
            do {
            } while (listRawDeviceData.size() <= desiredListSize);
        }

        ArrayList<float[]> formattedData = new ArrayList<>() ;

        for (int i = 1; i < desiredListSize; i++)
            formattedData.add(formatData(listRawDeviceData.get(i)));

        BluetoothDeviceData.returnEmptyList();

        return formattedData;
    }

    private static float[] formatData(String string) {
        String[] strArrayData = string.split(",");
        float[] inputtedLine = new float[strArrayData.length];

        for (int i = 0; i < strArrayData.length; i++) {
            strArrayData[i] = strArrayData[i].trim();
            inputtedLine[i] = Float.parseFloat(strArrayData[i]);
        }

        return inputtedLine;
    }

    public static float[] getAccelerationFromRawData(float[][] array2D, int txyz) {
        float[] rawAcceleration = new float[array2D.length];

        int i = 0;
        for (float[] floater : array2D) {
            rawAcceleration[i] = floater[txyz];
            i++;
        }

        return rawAcceleration;
    }

    public static float[] setAcceleration(float[][] array2D, float offsetVal, int txyz, float GRAVITY) {
        float[] acceleration = new float[array2D.length];

        int i = 0;
        for (float[] floater : array2D) {
            acceleration[i] = (floater[txyz] - offsetVal)*GRAVITY;
            i++;
        }

        return acceleration;
    }

    public static float offsetAcceleration(float[] rawAcceleration, float offsetBenchmark, float offsetFailedVal) {

        float maxValue = SimpleMathOps.getMaxValue(rawAcceleration);
        float minValue = SimpleMathOps.getMinValue(rawAcceleration);
        float tmp = maxValue - minValue;

        //System.out.println(Arrays.toString(rawAcceleration));

        if (tmp > offsetBenchmark) {
            //System.out.println(Arrays.toString(rawAcceleration));
            Log.d(TAG, "offsetAcceleration: " + offsetFailedVal);
            return offsetFailedVal;
        }

        Log.d(TAG, "offsetAcceleration: offsetProperly");

        return SimpleMathOps.getMeanValue(rawAcceleration);
    }

   public static float[] getScaledTimeArray(float[][] array2D) {

       float[] timeArray = new float[array2D.length];

       float zeroTime = array2D[0][0];


       int i = 0;
        for (float[] floater : array2D) {
            timeArray[i] = (floater[0] - zeroTime) / 1000;

            i++;
        }

        return timeArray;
    }

}