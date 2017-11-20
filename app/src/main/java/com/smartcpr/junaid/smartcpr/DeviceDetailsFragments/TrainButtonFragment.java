package com.smartcpr.junaid.smartcpr;

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


/**
 * Created by junaid on 11/16/17.
 */

public class TrainButtonFragment extends Fragment {
    private final static String TAG = "TrainButtonFragment";

    private static Button mTrainButton;
    private String strVictim;

    TrainButtonListener trainButtonListener;

    public interface TrainButtonListener {
        void cprVictim(String strCprVictim);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                trainButtonListener = (TrainButtonListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        } else  {
            try {
                trainButtonListener = (TrainButtonListener)context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString());
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button_train, container, false);

        mTrainButton = view.findViewById(R.id.train_button);
        mTrainButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Pressed TRAIN Button");
                        openDialogBox();

                    }

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
                                        trainButtonListener.cprVictim(strVictim);
                                    }
                                }).show();
                    }
                }
        );

        return view;
    }

}
