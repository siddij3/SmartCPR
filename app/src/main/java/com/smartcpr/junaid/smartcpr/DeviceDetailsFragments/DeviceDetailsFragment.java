package com.smartcpr.junaid.smartcpr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by junaid on 11/16/17.
 */

public class DeviceDetailsFragment extends Fragment{

    private TextView mDeviceName;
    private TextView mDevicePhysicalAddress;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text_device_details, container, false);

        mDeviceName = getActivity().findViewById(R.id.text_device_name);
        //mDeviceName.setText("Sex is very nice");

        mDevicePhysicalAddress = getActivity().findViewById(R.id.text_device_name);
        //mDevicePhysicalAddress.setText("Yes, yes it is");


        return view;
    }

}
