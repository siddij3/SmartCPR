package com.smartcpr.junaid.smartcpr;

import android.Manifest;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by junaid on 11/9/17.
 */

public class ScanButtonFragment extends Fragment {

    private final static String TAG = "ScanButtonFragment";
    private final int REQUEST_ENABLE_BT = 1;
    private static Button mScanButton;
    private BluetoothAdapter mBluetoothAdapter;
    private ListView lvNewDevices;

    private ArrayList<BluetoothDevice> mBTDevices;
    private ArrayList<String> mBTDeviceDetails;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scan_bluetooth, container, false);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        lvNewDevices = view.findViewById(R.id.main_list_devices);

        mBTDevices = new ArrayList<>();
        mBTDeviceDetails = new ArrayList<>();

        mScanButton  = view.findViewById(R.id.main_scan_button);
        mScanButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        mScanButtonClicked(view);
                    }
                }
        );

        lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                getActivity().registerReceiver(mBondState, filter);
                pairDevice(adapterView, view, i, l);
            }
        });

        return view;
    }

    private void mScanButtonClicked(View view) {
        if (mBluetoothAdapter == null) {
            Log.d(TAG,"Device does not support Bluetooth" );
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            Log.d(TAG,"Enabling Bluetooth" );

        }
        Log.d(TAG,"Bluetooth Enabled");
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        getActivity().registerReceiver(mReceiver, filter);
        discoverDevices(view);
    }

    private void discoverDevices(View view) {
        if(mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "Canceling discovery.");
        }

        //check BT permissions in manifest
        checkBTPermissions();

        Log.d(TAG, "Starting Discovery discovery.");
        mBluetoothAdapter.startDiscovery();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mDiscovery, filter);
    }

    private void pairDevice (AdapterView<?> adapterView, View view, int i, long l) {
        mBluetoothAdapter.cancelDiscovery();

        //Found the Problem
        String deviceDetails = (String) lvNewDevices.getItemAtPosition(i);
        BluetoothDevice device = mBTDevices.get(i);
        Log.d(TAG, "onItemClick " + deviceDetails);
        Log.d(TAG, "onItemClick (Device) " + device);

        String deviceName = device.getName();
        Log.d(TAG, "onItemClick: deviceDetails = " + deviceName);

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceName);
            Log.d(TAG, "The size of the array is: " + mBTDevices.size());
            Log.d(TAG, "The index selected is: " + i);
            Log.d(TAG, mBTDevices.get(i).getName());

            mBTDevices.get(i).createBond();
            Toast.makeText(getContext(), "Pairing with " + deviceName, 2);
        }

    }


    //Asynchronous method
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION CHANGED.");
            Log.d(TAG, action);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "STATE ON");
                        
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

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                String deviceDetails = device.getName() + "\n" + device.getAddress();

                if (mBTDeviceDetails.contains(deviceDetails)) {
                    Log.d(TAG, "onReceive: " + device.getName() + "already exists");

                } else if (!mBTDeviceDetails.contains(deviceDetails)) {
                    if (device.getName() != "null") {

                        mBTDeviceDetails.add(deviceDetails);
                        mBTDevices.add(device);
                        Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());

                        //Refreshes List to avoid duplication?
                        //lvNewDevices.setAdapter(new ArrayAdapter<>(context,
                        //      android.R.layout.simple_list_item_1,
                        //    new ArrayList<>()));
                    }

                    lvNewDevices.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, mBTDeviceDetails));

                }

            }
        }
    };

    private final BroadcastReceiver mBondState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


        private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = ActivityCompat.checkSelfPermission(getActivity(), "Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += ActivityCompat.checkSelfPermission(getActivity(), "Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mReceiver);
        getActivity().unregisterReceiver(mDiscovery);
        getActivity().unregisterReceiver(mBondState);
    }
}
