package com.smartcpr.trainer.smartcpr.CalibrateIMUFragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.smartcpr.junaid.smartcpr.R;


/**
 * InputUserNameFragment
 *
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

public class InputUserNameFragment extends Fragment {

    private EditText tInputUserName;

    private EditTextListener editTextListener;

    private KeyListener listener;

    public interface EditTextListener {
        void enableButton(EditText editText);
    }

    // Attaches a method associated with a button to the main activity, I think
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity){
            activity = (Activity) context;
            try {
                editTextListener = (EditTextListener)activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString());
            }
        } else  {
            try {
                editTextListener = (EditTextListener)context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString());
            }
        }
    }


    // Initializes listeners, and the textbox to streamline UX stuff
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edittext_username, container, false);

        tInputUserName = view.findViewById(R.id.edit_text_user_name);

        listener = tInputUserName.getKeyListener();

        tInputUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                editTextListener.enableButton(tInputUserName);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;
    }

    // Makes it so the edittext is unusable
    public void disableEditText() {
       // tInputUserName.setFocusable(false);
       tInputUserName.setEnabled(false);
       tInputUserName.setCursorVisible(false);
       // tInputUserName.setKeyListener(null);
        tInputUserName.setBackgroundColor(Color.TRANSPARENT);
    }

    // Makes it so the edittext is usable to allow user to input name
    public void enableEditText() {
        tInputUserName.setFocusable(true);
        tInputUserName.setEnabled(true);
        tInputUserName.setCursorVisible(true);
        tInputUserName.setText("");
        tInputUserName.setKeyListener(listener);
        tInputUserName.setBackgroundColor(Color.WHITE);
        //tInputUserName.requestFocus();

    }


}
