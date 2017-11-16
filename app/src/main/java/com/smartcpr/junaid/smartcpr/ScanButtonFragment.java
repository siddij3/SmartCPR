package com.smartcpr.junaid.smartcpr;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by junaid on 11/9/17.
 */

public class ScanButtonFragment extends Fragment {

    private final static String TAG = "ScanButtonFragment";
    private static Button mScanButton;
    private final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    //private ListView lvNewDevices;
    private ArrayList<BluetoothDevice> mBTDevices;

    ScanButtonListener activityCommander;

    public interface ScanButtonListener {
        void addDevice(BluetoothDevice BTDevice);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                activityCommander = (ScanButtonListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        } else  {
            try {
                activityCommander = (ScanButtonListener)context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Shows Fragment with Button and ListView
        View view = inflater.inflate(R.layout.fragment_scan_bluetooth, container, false);

        //Initializes Bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Initializes Lists that contain the Bluetooth Device and it's details
        mBTDevices = new ArrayList<>();

        //Click Event for Scan Button
        mScanButton = view.findViewById(R.id.main_scan_button);
        mScanButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        mScanButtonClicked();
                    }
                }
        );

        return view;
    }

    //Turns on Bluetooth if not already enabled
    private void mScanButtonClicked() {
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "Device does not support Bluetooth");
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.d(TAG, "Enabling Bluetooth");
        }

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);

        discoverDevices();

        Log.d(TAG, "Bluetooth Enabled");
    }

    private void discoverDevices() {
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Canceling discovery.");
        }

        //check BT permissions in manifest
        checkBTPermissions();

        mBluetoothAdapter.startDiscovery();
        Log.d(TAG, "Starting Discovery discovery.");

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mDiscovery, filter);
    }

    //Asynchronous method turns on Bluetooth Receiver if it's off
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION CHANGED.");
            Log.d(TAG, action);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "STATE ON");
                        discoverDevices();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "STATE TURNING ON");
                        break;
                }
            }
        }
    };

    public final BroadcastReceiver mDiscovery = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, action + "\tACTION FOUND");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (mBTDevices.contains(device)) {
                    Log.d(TAG, "onReceive: " + device.getName() + "already exists");

                } else if (!mBTDevices.contains(device)) {
                    if (device.getName() != null && (device.getName().length() > 0)) {

                        activityCommander.addDevice(device);

                        Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                    }
                }
            }
        }
    };


    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), "Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += ActivityCompat.checkSelfPermission(getActivity(), "Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(mDiscovery);
    }
}
