package com.smartcpr.junaid.smartcpr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.CompressionDepthsFragment;

public class CompressionFeedbackActivity extends AppCompatActivity  implements CompressionDepthsFragment. {

    private final static String TAG = "SpectralAnalysisActivit";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);
        Log.d(TAG, "onCreate: " + TAG);


    }
}
