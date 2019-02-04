package com.smartcpr.trainer.smartcpr.BluetoothData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * BluetoothDeviceData
 *
 * Global activity to synchronize data obtained from IMU onto a single list
 *
 * Used in calibration and spectral analysis as a way to not block threads but have
 * consistent information throughout the app
 *
 */
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
