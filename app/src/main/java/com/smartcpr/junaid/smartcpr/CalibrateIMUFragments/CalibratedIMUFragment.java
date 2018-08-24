package com.smartcpr.junaid.smartcpr.CalibrateIMUFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CalibratedIMUFragment extends Fragment {

    private static final String TAG = "CalibratedIMUFragment";
    private TextView mCalibratingMessage;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calibrating_imu, container, false);

        mCalibratingMessage =  view.findViewById(R.id.calibration_result_feedback);

        ReadRawData(getActivity());

        return view;
    }

    public void setCalibratingMessageFeedback(Boolean isDeviceCalibrated){
        mCalibratingMessage.setText(isDeviceCalibrated.toString());
        Log.d(TAG, "setDetailsText: isDeviceCalibrated  set");

    }

    private void ReadRawData(Context context) {

        File path = context.getFilesDir();
        File file = new File(path, "data.txt");

        while  (true) {
            try {
                int length = (int) file.length();

                byte[] bytes = new byte[length];

                FileInputStream in = new FileInputStream(file);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }

                String contents = new String(bytes);

                Log.d(TAG, "ReadRawData: " + contents);
            } catch (IOException e) {
                Log.e(TAG, "File read failed: " + e.toString());
                Log.d(TAG, "ReadRawData: " + e.getMessage());
            }
        }
    }
}
