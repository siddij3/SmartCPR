package com.smartcpr.junaid.smartcpr.BluetoothData;

import android.content.Context;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SimpleMathOps;

import java.util.ArrayList;
import java.util.List;

public class ManageData {

    private static final String TAG = "ManageDataClass";

    public ManageData() {

    }

    public ArrayList<float[]> getData(int desiredListSize, List<String> listRawDeviceData) {

        while (listRawDeviceData.size() <= desiredListSize) {
            //Log.d(TAG, "getData: list is not long enough");
        }

        ArrayList<float[]> formattedData =  new ArrayList<float[]>() ;

        for (int i = 1; i < desiredListSize; i++) {
            formattedData.add(formatData(listRawDeviceData.get(i)));
        }

        return formattedData;
    }


    private float[] formatData(String string) {
        String[] strArrayData = string.split(",");
        float[] inputtedLine = new float[strArrayData.length];
        Log.d(TAG, "formatData: " + string);

        try {
            for (int i = 0; i < strArrayData.length; i++) {
                strArrayData[i] = strArrayData[i].trim();
                inputtedLine[i] = Float.parseFloat(strArrayData[i]);
            }
            Log.d(TAG, "formatData: FormattedData");
            return inputtedLine;
        }
        catch (NumberFormatException e) {
            Log.e(TAG, "formatData: ",  e);
            return null;
        }
    }

    public float[] getAccelerationFromRawData(float[][] array2D, int txyz) {
        float[] rawAcceleration = new float[array2D.length];

        int i = 0;
        for (float[] floater : array2D) {
            Log.d(TAG, "getAccelerationFromRawData: " + floater[0] );
            Log.d(TAG, "getAccelerationFromRawData: " + floater[1] );
            Log.d(TAG, "getAccelerationFromRawData: " + floater[2] );
            Log.d(TAG, "getAccelerationFromRawData: " + floater[3] );
            rawAcceleration[i] = floater[txyz];
            i++;

        }

        return rawAcceleration;
    }

    public float offsetAcceleration(float[] rawAcceleration, float offsetBenchmark, float offsetFailedVal) {

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


    float[] setAcceleration(float[] rawAcceleration, float offsetVal, float GRAVITY) {

        float[] acceleration = new float[rawAcceleration.length];

        int i = 0;

        for (float floater : rawAcceleration) {
            acceleration[i] = (rawAcceleration[i] - offsetVal) * GRAVITY;
            i++;
        }

        return acceleration;
    }


    int[] getTimeArray(float[][] array2D) {

        int[] timeArray = new int[array2D.length];

        //System.out.println(timeArray.length);
        int i = 0;
        for (float[] floater : array2D) {
            timeArray[i] = (int) floater[0];
            i++;
        }
        //System.out.println(Arrays.toString(timeArray));

        return timeArray;
    }


    float[] scaleTime(float[][] array2D) {
        float[] time = new float[array2D.length];

        float zeroTime = array2D[0][0];

        int i = 0;
        for (float[] floater : array2D) {
            time[i] = (floater[0] - zeroTime) / 1000;
            i++;
        }

        return time;


    }


}