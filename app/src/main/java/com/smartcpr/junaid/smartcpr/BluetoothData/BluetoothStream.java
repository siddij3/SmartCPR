package com.smartcpr.junaid.smartcpr.BluetoothData;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.DeviceDetailsActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

/**
 * Created by junaid on 11/15/17.
 * Reads stream from Bluetooth device. I think
*/


public class BluetoothStream {

    private static final String TAG = "BluetoothConnectionServ";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;

    public BluetoothStream() {
        BluetoothAdapter mBluetoothAdapter = getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice tmp : pairedDevices) {
                String deviceName = tmp.getName();
                String deviceHardwareAddress = tmp.getAddress(); // MAC address
                Log.d(TAG, "\ndeviceName " + deviceName + " "
                        + "\ndeviceAddress " + deviceHardwareAddress);

                device = tmp;
            }

            //Use Connected Thread Here
            Log.d(TAG, "BluetoothStream: " + device);
            ConnectThread mConnectThread = new ConnectThread(device);
            mConnectThread.start();

        }

    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }


        public void run() {
            // Cancel discovery because it otherwise slows down the connection.

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.d(TAG, "run: This socket connected successfully");
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        private void manageMyConnectedSocket(BluetoothSocket mmSocket) {
            Log.d(TAG, "manageMyConnectedSocket: Beginning handling bluetooth data streams");
            ConnectedThread mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            InputStreamReader tmpInRead = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();

                Log.d(TAG, "ConnectedThread: Tmp IN : " + tmpIn);
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }

            mmInStream = tmpIn;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int numBytes; // bytes returned from read()
            String btData;
            BufferedReader reader = new BufferedReader(new InputStreamReader(mmInStream));
            Log.d(TAG, "run: BufferedReader went well");
            StringBuilder sb = new StringBuilder();



            Log.d(TAG, "run: ConnectedThread Run thread");
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    btData = mmInStream.toString();
                    Log.d(TAG, "run: " + reader.readLine());

                   // Log.d(TAG, "run: data" + btData);
                    // Send the obtained bytes to the UI activity.

                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

}
