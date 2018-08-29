package com.smartcpr.junaid.smartcpr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.CompressionRateFragment;

public class SpectralAnalysisActivity extends AppCompatActivity {

    CompressionRateFragment compressionRateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);

        compressionRateFragment = (CompressionRateFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_compression_rate);

    }
}
