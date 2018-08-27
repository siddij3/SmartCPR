package com.smartcpr.junaid.smartcpr.BluetoothData;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

/**
 * BlueToothStream Class
 * Subclasses: ConnectThread
 *             ConnectedThread
 *
 * Opens Bluetooth Socket with IMU and reads streamed lines of data
 *
 *
 * Main Class Functions:
 *
 * Constructor: Identifies paired device (limited to 1) and attempts to create communication channel
 *
 *
 * ConnectThread Functions:
 *
 * Constructor: Use IMU UUID to create a socket channel
 * Run:         Connects to Socket or throws an exception for failure and initializes
 *              thread reading class


 * ConnectedThread Functions:
 *
 * Constructor: Uses socket channel to obtain stream of bytes
 * Run:         Buffers data and forms stream into readable string
 *
 *
*/


public class BluetoothDeviceManager extends Thread {

    private static final String TAG = "BluetoothConnectionSer.";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice mBluetoothDevice;

    private BluetoothSocket mmSocket;
    private InputStream mmInStream;

    public BluetoothDevice getBluetoothDevice() {
        return mBluetoothDevice;
    }

    public BluetoothSocket getBluetoothSocket() {
        return mmSocket;
    }


    public BluetoothDeviceManager() {
        BluetoothAdapter mBluetoothAdapter = getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice tmp : pairedDevices) {
                String deviceName = tmp.getName();
                String deviceHardwareAddress = tmp.getAddress();
                Log.d(TAG, "\ndeviceName " + deviceName + " "
                        + "\ndeviceAddress " + deviceHardwareAddress);

                mBluetoothDevice = tmp;
            }

            //Use Connected Thread Here
            Log.d(TAG, "BluetoothStream: " + mBluetoothDevice);
        }

    }


    public boolean connectSocket(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket
        BluetoothSocket tmp;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

            mmSocket = tmp;

            try {
                mmSocket.connect();
            }
            catch (IOException connectionFailed) {
                Log.e(TAG, "connectSocket: ", connectionFailed);
                cancelConnectingStream();
                return false;
            }

            return true;

        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);

            return false;
        }

    }

    public void getInputStream(BluetoothSocket socket) {
        InputStream tmpIn = null;

        try {
            tmpIn = socket.getInputStream();

            Log.d(TAG, "ConnectedThread: Tmp IN : " + tmpIn);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input stream", e);
        }

        mmInStream = tmpIn;

    }

    public void run() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(mmInStream));

        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                String readLineBluetoothStream = reader.readLine();
                BluetoothDeviceData.appendToList(readLineBluetoothStream);

                //Log.d(TAG, "run: KeyCal " + readLineBluetoothStream);

            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                closeConnectedStrean();
            }
        }
    }


    void cancelConnectingStream() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }

    void closeConnectedStrean() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

}