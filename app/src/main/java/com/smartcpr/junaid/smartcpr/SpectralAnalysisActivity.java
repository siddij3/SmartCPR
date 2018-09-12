package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.DeviceDetailsFragments.CalibrateButtonFragment;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.FastFourierTransform;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Complex;
import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.CompressionDepthFragment;
import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.CompressionRateFragment;
import com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments.SpectralAnalysis;

import java.util.Objects;

public class SpectralAnalysisActivity extends AppCompatActivity {

    private final static String TAG = "SpectralAnalysisActive";

    public static final String EXTRA_OFFSET_ACCELERATION_VALUE
            = "com.smartcpr.junaid.smartcpr.offsetaccelerationvalue";

    public static final String EXTRA_VICTIM_MIN_DEPTH
            = "com.smartcpr.junaid.smartcpr.minvictimdepth";

    public static final String EXTRA_VICTIM_MAX_DEPTH
            = "com.smartcpr.junaid.smartcpr.maxvictimdepth";


    private int txyz;
    private int desiredListSizeForCompression;
    private float offsetAcceleration;

    private static float GRAVITY;

    Intent intent;
    Bundle bundle;
    private Handler mHandler;

    double lastRateValue;
    double lastDepthValue;

    CompressionDepthFragment compressionDepthFragment;
    CompressionRateFragment compressionRateFragment;

    private String getMinDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MIN_DEPTH);  }
    private String getMaxDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MAX_DEPTH); }
    private String getOffsetAcceleration() { return Objects.requireNonNull(bundle).getString(EXTRA_OFFSET_ACCELERATION_VALUE); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);

        intent = getIntent();
        bundle = intent.getExtras();

        int minVictimDepth = Integer.parseInt(getMinDepth());
        int maxVictimDepth = Integer.parseInt(getMaxDepth());
        offsetAcceleration = Float.parseFloat(getOffsetAcceleration());

        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeForCompression = getResources().getInteger(R.integer.compression_list_size);
        GRAVITY = Float.parseFloat(getResources().getString(R.string.gravity_value));

        compressionDepthFragment = (CompressionDepthFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_compression_depth);
        compressionRateFragment = (CompressionRateFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_compression_rate);


        compressionDepthFragment.compressionDepthsForAgeGroups(minVictimDepth, maxVictimDepth);

        handleMessage();
        startSpectralAnalysis();
    }

    void handleMessage() {
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                int depthRate = message.what;
                double  value = (double) message.obj;

                if (depthRate == 0 ) {
                    Log.d(TAG, "handleMessage: depth: " + value);

                    value = Math.random() *10;

                    compressionDepthFragment.resetColours(lastDepthValue);
                    compressionDepthFragment.changeTextView(value);


                    lastDepthValue = value;

                } else if (depthRate == 1) {
                    Log.d(TAG, "handleMessage: rate: " + value);

                    value = Math.random() *150;

                    compressionRateFragment.resetColours(lastRateValue);
                    compressionRateFragment.changeTextView(value);

                    lastRateValue = value;

                }

            }
        };

    }

    private void startSpectralAnalysis() {

        SpectralAnalysis spectralAnalysisThread = new SpectralAnalysis(txyz, GRAVITY, desiredListSizeForCompression, offsetAcceleration, mHandler);
        new Thread(spectralAnalysisThread).start();


    }



}

