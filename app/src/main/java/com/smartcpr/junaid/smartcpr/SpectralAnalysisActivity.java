package com.smartcpr.junaid.smartcpr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.ManageData;
import com.smartcpr.junaid.smartcpr.CalibrateIMUFragments.CalibratedIMUFragment;
import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.CompressionRateFragment;

public class SpectralAnalysisActivity extends AppCompatActivity {

    private final static String TAG = "SpectralAnalysisActive";

    CompressionRateFragment compressionRateFragment;

    private ManageData manageData;

    private int txyz;
    private int desiredListSizeSizeForCalibration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);
        Log.d(TAG, "onCreate: ");


        manageData = new ManageData();

    }
}
