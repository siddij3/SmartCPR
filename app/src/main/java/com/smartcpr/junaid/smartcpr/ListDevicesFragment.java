package com.smartcpr.junaid.smartcpr;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by junaid on 11/15/17.
 */

public class ListDevicesFragment extends android.support.v4.app.Fragment {

    private final static String TAG = "ListDevicesFragment";

    private BluetoothAdapter mBluetoothAdapter;
    private ListView lvNewDevices;
    private ArrayList<BluetoothDevice> mBTDevices;
    private ArrayList<String> mBTDeviceDetails;
    private Boolean isDeviceBonded;

    private ArrayAdapter<String> mBTDevicesAdapter;

    ListDevicesListener activityCommander;

    public interface ListDevicesListener {
        void bluetoothDeviceBonded(Boolean isBluetoothDeviceBonded);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                activityCommander = (ListDevicesListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        } else  {
            try {
                activityCommander = (ListDevicesListener)context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString());
            }
        }
    }

    public void addDevicesToList(BluetoothDevice device) {

        if (mBTDevices.contains(device)) {
            Log.d(TAG, "Adding Devices to List: " + device.getName() + "already exists");

        } else if (!mBTDevices.contains(device)) {
            mBTDeviceDetails.add(device.getName() + "\n" + device.getAddress());
            mBTDevices.add(device);
            lvNewDevices.setAdapter(mBTDevicesAdapter);
            Log.d(TAG, "Adding Devices to List: " + device.getName() + ": " + device.getAddress());
            mBluetoothAdapter.getBondedDevices();
        }
    }

    private void pairDevice(int i, long l) {
        mBluetoothAdapter.cancelDiscovery();

        String deviceDetails = (String) lvNewDevices.getItemAtPosition(i);
        Log.d(TAG, "onItemClick " + deviceDetails);

        BluetoothDevice deviceToBePaired = mBTDevices.get(i);

        String deviceToBePairedName = deviceToBePaired.getName();
        Log.d(TAG, "onItemClick: deviceDetails = " + deviceToBePairedName);

        //Looks for paired devices, and checks if the device clicked is already paired
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                String tmp = device.getName();
                Log.d(TAG, "pairedDevice: " + tmp);
                Log.d(TAG, "pairedDevice: device is " + device);
                Log.d(TAG, "pairedDevice: deviceToBePaired is " + deviceToBePaired);

                Log.d(TAG, device.toString());
                Log.d(TAG, deviceToBePaired.toString());

                if (device.toString().equals(deviceToBePaired.toString())) {
                    Log.d(TAG, "pairDevice: "  + tmp + " = " + deviceToBePairedName);
                    isDeviceBonded = true;
                    activityCommander.bluetoothDeviceBonded(isDeviceBonded);
                    return;
                }
            }
        }

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Log.d(TAG, "Trying to pair with " + deviceToBePairedName);
            Log.d(TAG, "The size of the array is: " + mBTDevices.size());
            Log.d(TAG, "The index selected is: " + i);

            deviceToBePaired.createBond();
        }

    }

    private final BroadcastReceiver mBondState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    isDeviceBonded = true;
                    activityCommander.bluetoothDeviceBonded(isDeviceBonded);
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

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Shows Fragment with Button and ListView
        View view = inflater.inflate(R.layout.fragment_list_devices, container, false);

        isDeviceBonded = false;

        //Initializes Lists that contain the Bluetooth Device and it's details
        mBTDevices = new ArrayList<>();
        mBTDeviceDetails = new ArrayList<>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBTDevicesAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, mBTDeviceDetails);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        getActivity().registerReceiver(mBondState, filter);

        //Click Item Event for Pairing Bluetooth Devices
        lvNewDevices = view.findViewById(R.id.main_list_devices);
        lvNewDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                getActivity().registerReceiver(mBondState, filter);
                Log.d(TAG, "onItemClick: Clicked on an Item from the List");

                pairDevice(i, l);

            }
        });

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(mBondState);
    }
}

