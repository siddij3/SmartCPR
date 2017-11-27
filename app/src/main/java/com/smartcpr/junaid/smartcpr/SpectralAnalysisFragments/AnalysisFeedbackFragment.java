package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartcpr.junaid.smartcpr.R;

/**
 * Created by junaid on 11/24/17.
 */

public class AnalysisFeedbackFragment extends Fragment {
    private final static String TAG = "AnalysisFeedbackFragment";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analysis_feedback, container, false);

        return view;
    }
}
