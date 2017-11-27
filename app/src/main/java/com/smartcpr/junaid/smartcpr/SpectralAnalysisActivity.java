package com.smartcpr.junaid.smartcpr;

import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.ObjectClasses.Victim;
import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.CompressionTargetsFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static com.smartcpr.junaid.smartcpr.DeviceDetailsActivity.EXTRA_BLUETOOTH_DEVICE_NAME;

public class SpectralAnalysisActivity extends AppCompatActivity {

    private final static String TAG = "SpectralAnalysisActivit";
    public static final String EXTRA_VICTIM_AGE = "com.smartcpr.junaid.smartcpr.extravictimage";

    public static final String EXTRA_MIN_AGE = "com.smartcpr.junaid.smartcpr.extraminage";
    public static final String EXTRA_MAX_AGE = "com.smartcpr.junaid.smartcpr.extramaxage";


    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static final String appName = "SmartCPR";

    Set<BluetoothDevice> pairedDevices;

    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mDevice;

    private CompressionTargetsFragment compressionTargetsFragment;


    private AcceptThread mInsecureAcceptThread;

    private UUID deviceUUID;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;

    Victim victim;

    private int adultMinRate;
    private int adultMaxRate;

    private int youthMinRate;
    private int youthMaxRate;

    private int childMinRate;
    private int childMaxRate;

    private int infantMinRate;
    private int infantMaxRate;

    private int mMinDepth;
    private int mMaxDepth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);

        Log.d(TAG, "onCreate: " + TAG);

        //Get the bundle and send to Fragment
        Bundle getBundle = getIntent().getExtras();
        String strCprVictim = getBundle.getString(EXTRA_VICTIM_AGE);
        setCPRDetails();
        victimAge(strCprVictim);
        Log.d(TAG, "onCreate: " + strCprVictim);

        mMaxDepth = victim.getMaxDepth();
        mMinDepth = victim.getMinDepth();
        compressionTargetsFragment = (CompressionTargetsFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_rate_compression_target);
        compressionTargetsFragment.setTargetDepthText(mMinDepth, mMaxDepth);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mDevice = device;
                Log.d(TAG, "onCreate: " + device + " is Paired");
            }
        }



        mConnectThread = new ConnectThread(mDevice, MY_UUID);
        mConnectThread.start();
    }

    private void victimAge(String strCprVictim) {
        if (strCprVictim.contains(getString(R.string.victim_adult)))  {
            victim = new Victim(getString(R.string.victim_adult), adultMaxRate, adultMinRate, 1);
            Log.d(TAG, "victimAge: " + strCprVictim);

        } else if (strCprVictim.contains(getString(R.string.victim_youth))) {
            victim = new Victim(getString(R.string.victim_adult), youthMaxRate, youthMinRate, 1);
            Log.d(TAG, "victimAge: " + strCprVictim);

        } else if (strCprVictim.contains(getString(R.string.victim_child))) {
            victim = new Victim(getString(R.string.victim_adult), childMaxRate, childMinRate, 0.5);
            Log.d(TAG, "victimAge: " + strCprVictim);

        } else if (strCprVictim.contains(getString(R.string.victim_infant))) {
            victim = new Victim(getString(R.string.victim_adult), infantMaxRate, infantMinRate, 0.5);
            Log.d(TAG, "victimAge: " + strCprVictim);
        }
        Log.d(TAG, "victimAge: second time " + strCprVictim);
    }

    private void setCPRDetails () {
        adultMinRate = getResources().getInteger(R.integer.adult_min);
        adultMaxRate = getResources().getInteger(R.integer.adult_max);

        youthMinRate = getResources().getInteger(R.integer.youth_min);
        youthMaxRate = getResources().getInteger(R.integer.youth_max);

        childMinRate = getResources().getInteger(R.integer.child_min);
        childMaxRate = getResources().getInteger(R.integer.child_max);

        infantMinRate = getResources().getInteger(R.integer.infant_min);
        infantMaxRate = getResources().getInteger(R.integer.infant_max);

        Log.d(TAG, "setDetails: Details Set");
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mBluetoothServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp =mBluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(appName, MY_UUID);
            } catch (IOException e) {

            }
            mBluetoothServerSocket = tmp;
        }

        public void run() {
            Log.d(TAG, "run: AcceptThread Running ");

            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run: RFCOM server socket start");
                socket = mBluetoothServerSocket.accept();
                Log.d(TAG, "run: RFCOM server socket accept connection");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (socket != null) {
                connected(socket, mDevice);
                
            }
            Log.i(TAG, "run: END mAcceptThread");
        }

        public void cancel() {
            Log.d(TAG, "cancel: Cancelling AcceptThread" );
            try {
                mBluetoothServerSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "cancel: Close AcceptThread ServerSocket Failed; "+  e.toString());
            }
        }

    }

    private class ConnectThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            deviceUUID = uuid;
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
            connected(mmSocket, mmDevice);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }


    public synchronized void start() {
        Log.d(TAG, "start");

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread();
            mInsecureAcceptThread.start();
        }
    }

    /**
     AcceptThread starts and sits waiting for a connection.
     Then ConnectThread starts and attempts to make a connection with the other devices AcceptThread.
     **/

    public void startClient(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startClient: Started.");

        mConnectThread = new ConnectThread(device, uuid);
        mConnectThread.start();
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;

            while (true) {
                try {
                   // bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    bytes = mmInStream.read(buffer);
                    String incomingMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "InputStream: " + incomingMessage);
                } catch (IOException e) {
                    Log.e(TAG, "write: Error reading Input Stream. " + e.getMessage() );
                    break;
                }
            }
        }
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }

    }


    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

}
