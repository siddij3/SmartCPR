package com.smartcpr.junaid.smartcpr.BluetoothData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BluetoothDeviceData {

    private static final String TAG = "BluetoothDeviceData";
    private static final List<String> listDeviceData = Collections.synchronizedList(new ArrayList<String>());

    public static synchronized void appendToList(String string) {
        listDeviceData.add(string);
    }

    public static synchronized List<String> getList() {
        return listDeviceData;
    }

    public static synchronized void returnEmptyList() {
        listDeviceData.clear();
    }


}
