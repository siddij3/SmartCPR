package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

/**
 * Created by junaid on 11/24/17.
 */

public class CompressionTargetsFragment extends android.support.v4.app.Fragment {
    private final static String TAG = "CompressionTargetsFragm";

    private static TextView mTargetDepth;

    String minAge;
    String maxAge;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compression_targets, container, false);

        mTargetDepth = view.findViewById(R.id.rate_compression_target);

        Log.d(TAG, "onCreateView: " + minAge);
        Log.d(TAG, "onCreateView: " + maxAge);

        return view;
    }

    public void setTargetDepthText(int minAge, int maxAge) {
        String targetDepth = minAge + " - " + maxAge + " cm";
        mTargetDepth.setText(targetDepth);
        Log.d(TAG, "setTargetRateText: Depth Details Set");

    }
}
