package com.smartcpr.junaid.smartcpr.BluetoothData;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BluetoothDeviceData {

    private static final String TAG = "BluetoothDeviceData";
    private static final List<String> listDeviceData = Collections.synchronizedList(new ArrayList<String>());

    public static synchronized void appendToList(String string) {
        //Log.d(TAG, "appendToList: ");
        listDeviceData.add(string);
    }

    public static synchronized List<String> getList() {
        Log.d(TAG, "getList: ");
        return listDeviceData;
    }

    public static synchronized void returnEmptyList() {
        Log.d(TAG, "returnEmptyList: ");
        listDeviceData.clear();
    }


}
