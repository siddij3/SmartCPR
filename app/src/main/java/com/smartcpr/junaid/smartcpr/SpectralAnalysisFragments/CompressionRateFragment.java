package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

public class CompressionRateFragment extends Fragment {

    private static final String TAG = "CompressionRateFragment";
    private TextView mCalibratingMessage;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compression_rate, container, false);
        Log.d(TAG, "onCreateView: ");

        return view;
    }

}
