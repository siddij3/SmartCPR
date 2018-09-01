package com.smartcpr.junaid.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.MathOperationsClasses.FastFourierTransform;
import com.smartcpr.junaid.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.junaid.smartcpr.ObjectClasses.Complex;
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

    private static float offsetAcceleration;

    private int txyz;
    private int desiredListSizeForCompression;

    private static float GRAVITY;
    private static float OFFSET_BENCHMARK;
    private static float OFFSET_FAILED_VALUE;


    private float minVictimDepth;
    private float maxVictimDepth;

    Intent intent;
    Bundle bundle;

    private String getOffsetValue() { return Objects.requireNonNull(bundle).getString(EXTRA_OFFSET_ACCELERATION_VALUE); }
    private String getMinDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MIN_DEPTH);  }
    private String getMaxDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MAX_DEPTH); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);

        intent = getIntent();
        bundle = intent.getExtras();

        offsetAcceleration = Float.parseFloat(getOffsetValue());

        Log.d(TAG, "onCreate: "  + getMinDepth());
        Log.d(TAG, "onCreate: " + getMaxDepth());

        minVictimDepth = Float.parseFloat(getMinDepth());
        maxVictimDepth = Float.parseFloat(getMaxDepth());

        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeForCompression = getResources().getInteger(R.integer.compression_list_size);

        OFFSET_BENCHMARK = Float.parseFloat(getResources().getString(R.string.offset_benchmark_value));
        OFFSET_FAILED_VALUE = Float.parseFloat(getResources().getString(R.string.offset_failed_value));
        GRAVITY = Float.parseFloat(getResources().getString(R.string.gravity_value));

    }


    private void startSpectralAnalysis() {
        SpectralAnalysis spectralAnalysisThread = new SpectralAnalysis(txyz, offsetAcceleration, OFFSET_BENCHMARK, OFFSET_FAILED_VALUE, GRAVITY);
    }

    private void performSpectralAnalysis(float[] time, float[] acceleration) {
        int N = time.length;

        //May not need this thing
        N = FastFourierTransform.setArraySizeExponentOfTwo(N);
        Log.d(TAG, "performSpectralAnalysis: Array Size " + N);


        double hanningAppliedValues[] = SimpleMathOps.applyHanningWindow(acceleration, N);


        Complex[] baseComplexArray = FastFourierTransform.baseComplexArrayWithWindow(hanningAppliedValues, N);
        Complex[] complexArrayFFTValues = FastFourierTransform.simpleFFT(baseComplexArray);

        Complex[] fftPolarSingle = FastFourierTransform.fftDoubleToSingle(complexArrayFFTValues, N, 2);
        double[] fftSmooth = FastFourierTransform.smoothFFTValues(fftPolarSingle, N);


    }


}
