package com.smartcpr.junaid.smartcpr;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by junaid on 11/15/17.
 */




public class BluetoothData {
}
/*    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "SmartCPR";

    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private final BluetoothAdapter mBluetoothAdapter;
    Context mContext;


    private BluetoothDevice mBluetoothDevice;
    private UUID deviceUUID;
    ProgressDialog mProgressDialog;



    public BluetoothData(Context context) {
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mContext = context;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mConnectThread = new ConnectThread(mDevice);
        mConnectThread.start();
    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000- 00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
*/