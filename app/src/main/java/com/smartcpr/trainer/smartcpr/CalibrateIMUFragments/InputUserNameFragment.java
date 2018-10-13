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

public class InputUserNameFragment extends Fragment {

    private EditText tInputUserName;

    private EditTextListener editTextListener;

    private KeyListener listener;

    public interface EditTextListener {
        void enableButton(EditText editText);
    }

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


    public void disableEditText() {
        tInputUserName.setFocusable(false);
        tInputUserName.setEnabled(false);
        tInputUserName.setCursorVisible(false);
        tInputUserName.setKeyListener(null);
        tInputUserName.setBackgroundColor(Color.TRANSPARENT);
    }

    public void enableEditText() {
        tInputUserName.setFocusable(true);
        tInputUserName.setEnabled(true);
        tInputUserName.setCursorVisible(true);
        tInputUserName.setKeyListener(listener);
        tInputUserName.setBackgroundColor(Color.WHITE);
    }


}
