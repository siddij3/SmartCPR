package com.smartcpr.trainer.smartcpr.ScanDevicesFragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.smartcpr.junaid.smartcpr.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

/**
 * ListDevicesFragment Fragment
 *
 * Functions:
 *
 * onAttach: Implements the interface ListDevicesListener, which calls
 *           bluetoothDeviceBonded function in ScanDevicesActivity Activity
 *
 * addDevicesToList: Adds a new device to the list and checks for duplicates
 *
 * pairDevice: Gets the details from the device tapped on the list and pairs the device
 *
 * mBondState: Gets Bluetooth states before, during and after pairing
 *
 * onCreateView: Initializes Lists that contain the Bluetooth Device and it's details
 *               and listens for tap on list item device
 *
 *
 */

public class ListDevicesFragment extends Fragment {

    private final static String TAG = "ListDevicesFragment";

    private BluetoothAdapter mBluetoothAdapter;
    private ListView lvNewDevices;
    private ArrayList<BluetoothDevice> mBTDevices;
    private ArrayList<String> mBTDeviceDetails;

    private ArrayAdapter<String> mBTDevicesAdapter;

    private ListDevicesListener listDevicesListener;

    public interface ListDevicesListener {
        void bluetoothDeviceBonded(Boolean isBluetoothDeviceBonded, BluetoothDevice bluetoothDevice);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                listDevicesListener = (ListDevicesListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        } else  {
            try {
                listDevicesListener = (ListDevicesListener)context;
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

    private void pairDevice(int i) {
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

                Log.d(TAG, device.toString());
                Log.d(TAG, deviceToBePaired.toString());

                if (device.toString().equals(deviceToBePaired.toString())) {
                    Log.d(TAG, "pairDevice: "  + tmp + " = " + deviceToBePairedName);
                    listDevicesListener.bluetoothDeviceBonded(true, deviceToBePaired);
                    return;
                }
            }
        }

        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        Log.d(TAG, "Trying to pair with " + deviceToBePairedName);
        Log.d(TAG, "The size of the array is: " + mBTDevices.size());
        Log.d(TAG, "The index selected is: " + i);

        deviceToBePaired.createBond();

    }

    private final BroadcastReceiver mBondState = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (Objects.requireNonNull(action).equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                BluetoothDevice mBluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //3 cases:
                //case1: bonded already
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    listDevicesListener.bluetoothDeviceBonded(true, mBluetoothDevice);

                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                }
                //case2: creating a bone
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    lvNewDevices.setClickable(true);
                    lvNewDevices.setEnabled(true);

                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    lvNewDevices.setClickable(true);
                    lvNewDevices.setEnabled(true);

                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Shows Fragment with Button and ListView
        View view = inflater.inflate(R.layout.fragment_list_devices, container, false);

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
                lvNewDevices.setClickable(false);
                lvNewDevices.setEnabled(false);

                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                getActivity().registerReceiver(mBondState, filter);

                Log.d(TAG, "onItemClick: Clicked on an Item from the List");
                pairDevice(i);

            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Pausing Fragment");

    }

    @Override
    public void onResume() {
        super.onResume();
        lvNewDevices.setClickable(true);
        lvNewDevices.setEnabled(true);

        Log.d(TAG, "onResume: Resuming Fragment");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: Destroying Activity");

        getActivity().unregisterReceiver(mBondState);
    }
}

