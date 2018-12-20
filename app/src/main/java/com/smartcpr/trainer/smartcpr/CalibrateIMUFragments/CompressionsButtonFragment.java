package com.smartcpr.trainer.smartcpr.CalibrateIMUFragments;

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
 * CompressionsButtonFragment
 *
 * Interface - compressionsButtonListener: Attached to main activity.
 *
 * Functions:
 * onAttach: IDK it does something
 * onCreateView: creates a button listener when button is pressed. When pressed, it creates a
 *                dialog box with 3 options: child, youth, adult; to select for compressions
 * makeButtonClickable: Enables the buttons
 *
 * setCalibratingMessageFeedback: Displays message
 *      Params:
 *          deviceCalibrated - string that details the calibration status
 */

public class CompressionsButtonFragment extends Fragment {

    private static final String TAG = "CalibratedIMUFragment";

    private Button mCompressionsButton;
    private String strVictim;


    private CompressionsButtonListener compressionsButtonListener;

    public interface CompressionsButtonListener {
        void cprVictim(String strCprVictim);
    }

    // Attaches a method associated with a button to the main activity, I think
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                compressionsButtonListener = (CompressionsButtonListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        } else  {
            try {
                compressionsButtonListener = (CompressionsButtonListener)context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString());
            }
        }
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button_compressions, container, false);
        Log.d(TAG, "onCreateView: ");

        mCompressionsButton = view.findViewById(R.id.compressions_button);

        // Initiates listener for button
        mCompressionsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Pressed Compressions Button");
                        openDialogBox();

                        mCompressionsButton.setClickable(false);
                    }


                    // Dialog box with 3 options; child, youth, adult
                    private void openDialogBox() {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(getContext());
                        }

                        builder.setTitle(R.string.pick_victim)
                                .setItems(R.array.victim, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int index) {
                                        strVictim = getResources().getStringArray(R.array.victim)[index];
                                        Log.d(TAG, "onClick: Clicked on the Item  " + strVictim);
                                        compressionsButtonListener.cprVictim(strVictim);
                                    }
                                }).show();
                    }
                }
        );


        return view;
    }

    // Called in CalibrateIMUActivity
    public void makeButtonClickable(boolean setClickable) {
        mCompressionsButton.setClickable(setClickable);
    }
}
