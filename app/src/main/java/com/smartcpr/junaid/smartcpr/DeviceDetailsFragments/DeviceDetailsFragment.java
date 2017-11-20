package com.smartcpr.junaid.smartcpr.DeviceDetailsFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

/**
 * Created by junaid on 11/16/17.
 */

public class DeviceDetailsFragment extends Fragment{
    private final static String TAG = "DeviceDetailsFragment";


    private static TextView mDeviceName;
    private static TextView mDevicePhysicalAddress;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_device_details, container, false);

        mDeviceName = view.findViewById(R.id.text_device_name);
        mDevicePhysicalAddress = view.findViewById(R.id.text_device_physical_address);

        return view;
    }

    public void setDetailsText(String deviceName, String devicePhysicalAddress){
        mDeviceName.setText(deviceName);
        mDevicePhysicalAddress.setText(devicePhysicalAddress);
        Log.d(TAG, "setDetailsText: Device Tags Details set");

    }

}
