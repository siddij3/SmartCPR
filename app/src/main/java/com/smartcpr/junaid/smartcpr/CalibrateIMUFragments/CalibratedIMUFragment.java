package com.smartcpr.junaid.smartcpr.CalibrateIMUFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

public class CalibratedIMUFragment extends Fragment {

    private static final String TAG = "CalibratedIMUFragment";
    private TextView mCalibratingMessage;



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calibrating_imu, container, false);
        Log.d(TAG, "onCreateView: ");
        mCalibratingMessage =  view.findViewById(R.id.calibration_result_feedback);

        TextView txtView = mCalibratingMessage;

        return view;
    }

    public void setCalibratingMessageFeedback(String deviceCalibrated){
        mCalibratingMessage.setText(deviceCalibrated);


        Log.d(TAG, "setDetailsText: isDeviceCalibrated Message set");
    }

}
