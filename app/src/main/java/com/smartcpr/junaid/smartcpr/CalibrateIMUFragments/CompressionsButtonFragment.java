package com.smartcpr.junaid.smartcpr.CalibrateIMUFragments;

import android.app.AlertDialog;
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

public class CompressionsButtonFragment extends Fragment {

    private static final String TAG = "CalibratedIMUFragment";

    private Button mCompressionsButton;
    private String strVictim;


    private CompressionsButtonListener compressionsButtonListener;

    public interface CompressionsButtonListener {
        void cprVictim(String strCprVictim);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_button_compressions, container, false);
        Log.d(TAG, "onCreateView: ");

        mCompressionsButton = view.findViewById(R.id.compressions_button);
        mCompressionsButton.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: Pressed TRAIN Button");
                        openDialogBox();

                        mCompressionsButton.setClickable(false);
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
                                        compressionsButtonListener.cprVictim(strVictim);
                                    }
                                }).show();
                    }
                }
        );


        return view;
    }

}
