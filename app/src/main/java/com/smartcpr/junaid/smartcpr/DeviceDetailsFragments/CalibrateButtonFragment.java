package com.smartcpr.junaid.smartcpr.DeviceDetailsFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
                        Log.d(TAG, "onClick: Pressed TRAIN Button");
                        openDialogBox();

                        mCalibrateButton.setClickable(false);
                    }

                    private void openDialogBox() {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getContext());
                        }

                        builder.setTitle("Tmp")
                                .setItems(R.array.tmp, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int index) {
                                        calibrateButtonListener.connectDevice();
                                    }
                                }).show();
                    }
                }
        );

        return view;
    }

}
