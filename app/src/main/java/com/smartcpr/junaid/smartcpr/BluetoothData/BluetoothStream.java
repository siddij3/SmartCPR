package com.smartcpr.junaid.smartcpr.BluetoothData;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;
import static java.nio.charset.StandardCharsets.*;

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


public class BluetoothStream {

    private static final String TAG = "BluetoothConnectionSer.";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothDevice device;

    public final Context cContext;

    public BluetoothStream(Context context) {
        BluetoothAdapter mBluetoothAdapter = getDefaultAdapter();
        cContext = context;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice tmp : pairedDevices) {
                String deviceName = tmp.getName();
                String deviceHardwareAddress = tmp.getAddress();
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

        ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
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
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            try {
                mmSocket.connect();
                Log.d(TAG, "run: This socket connected successfully");
            } catch (IOException connectException) {
                cancel();
                return;
            }

            // Connection attempt succeeded and begins reading data
            Log.d(TAG, "manageMyConnectedSocket: Beginning handling bluetooth data streams");
            ConnectedThread mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();

        }

        void cancel() {
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

        ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;

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
            BufferedReader reader = new BufferedReader(new InputStreamReader(mmInStream));
            Log.d(TAG, "run: BufferedReader went well");
            //StringBuilder sb = new StringBuilder();

            // Keep listening to the InputStream until an exception occurs.
            while (true) try {
                //TODO The start of using data streams for Spectral analysis
                String tmpReadLine = reader.readLine();
                //Log.d(TAG, "run:  " + tmpReadLine);
                IMUWriteRawData(tmpReadLine, BluetoothStream.this.cContext);

            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                cancel();
                break;
            }
        }

        private void IMUWriteRawData(String data, Context context) {

            try {
                File path = context.getFilesDir();

                File file = new File(path, "data.txt");

                FileOutputStream stream = new FileOutputStream(file);
                //String str = new String(data.getBytes());
                //Log.d(TAG, "IMUWriteRawData: Write String - " + path);


                try {
                    stream.write(data.getBytes());
                    //Log.d(TAG, "IMUWriteRawData: " + data.getBytes());
                } finally {
                    stream.close();
                }

            }
            catch (IOException e) {
                Log.e(TAG, "File write failed: " + e.toString());
            }
        }

        void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }

}
