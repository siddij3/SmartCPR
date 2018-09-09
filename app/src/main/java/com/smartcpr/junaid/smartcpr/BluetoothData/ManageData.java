package com.smartcpr.junaid.smartcpr.BluetoothData;

import android.util.Log;

import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SimpleMathOps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        //Log.d(TAG, "formatData: " + string);

        for (int i = 0; i < strArrayData.length; i++) {
            strArrayData[i] = strArrayData[i].trim();
            inputtedLine[i] = Float.parseFloat(strArrayData[i]);
        }

        //Log.d(TAG, "formatData: " + Arrays.toString(inputtedLine));

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


    public static float[] setAcceleration(float[][] array2D, float offsetVal, int txyz,  float GRAVITY) {
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