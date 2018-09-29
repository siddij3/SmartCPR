package com.smartcpr.trainer.smartcpr.DeviceDetailsFragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smartcpr.trainer.smartcpr.BluetoothData.BluetoothDeviceManager;
import com.smartcpr.junaid.smartcpr.R;


/**
 * CompressionsButtonFragment Fragment
 *
 * Functions:
 *
 * onAttach: Implements the interface CalibrateButtonListener, which calls
 *           cprVictim function in DeviceDetailsActivity Activity
 * onCreateView: Adds button to UI and waits for button to be pressed.
 *               Once pressed, a dialog list asks user to select the age range for calibrateing
 *
 *
 */

public class CalibrateButtonFragment extends Fragment {
    private final static String TAG = "CalibrateButtonFragment";

    private Button mCalibrateButton;

    private CalibrateButtonListener calibrateButtonListener;

    public interface CalibrateButtonListener {
        void connectDevice();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                calibrateButtonListener = (CalibrateButtonListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        } else  {
            try {
                calibrateButtonListener = (CalibrateButtonListener)context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString());
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button_calibrate, container, false);

        mCalibrateButton = view.findViewById(R.id.calibrate_button);
        mCalibrateButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Pressed Calibrate Button");
                        BluetoothDeviceManager bluetoothStream = new BluetoothDeviceManager();
                        Log.d(TAG, "onClick: " + bluetoothStream.activeCount());
                        calibrateButtonListener.connectDevice();


                        mCalibrateButton.setClickable(false);
                    }


                }
        );

        return view;
    }

}
