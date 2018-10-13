package com.smartcpr.trainer.smartcpr;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.R;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.CompressionDepthFragment;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.CompressionRateFragment;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.SpectralAnalysis;

import java.util.Objects;

public class SpectralAnalysisActivity extends AppCompatActivity {

    private final static String TAG = "SpectralAnalysisActive";

    public static final String EXTRA_OFFSET_ACCELERATION_VALUE
            = "com.smartcpr.junaid.smartcpr.offsetaccelerationvalue";

    public static final String EXTRA_VICTIM_MIN_DEPTH
            = "com.smartcpr.junaid.smartcpr.minvictimdepth";

    public static final String EXTRA_VICTIM_MAX_DEPTH
            = "com.smartcpr.junaid.smartcpr.maxvictimdepth";

    public static final String EXTRA_USER_NAME
            = "com.smartcpr.junaid.smartcpr.username";


    private int txyz;
    private int desiredListSizeForCompression;
    private float offsetAcceleration;


    //private static float GRAVITY;

    Intent intent;
    Bundle bundle;
    private Handler mHandler;

    private double lastRateValue;
    double lastDepthValue;

    private String userName;

    CompressionDepthFragment compressionDepthFragment;
    CompressionRateFragment compressionRateFragment;

    private String getMinDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MIN_DEPTH);  }
    private String getMaxDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MAX_DEPTH); }
    private String getOffsetAcceleration() { return Objects.requireNonNull(bundle).getString(EXTRA_OFFSET_ACCELERATION_VALUE); }
    private String getUserName() { return Objects.requireNonNull(bundle).getString(EXTRA_USER_NAME); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);

        intent = getIntent();
        bundle = intent.getExtras();

        int minVictimDepth = Integer.parseInt(getMinDepth());
        int maxVictimDepth = Integer.parseInt(getMaxDepth());
        offsetAcceleration = Float.parseFloat(getOffsetAcceleration());
        userName = getUserName().toLowerCase();

        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeForCompression = getResources().getInteger(R.integer.compression_list_size);
        //GRAVITY = Float.parseFloat(getResources().getString(R.string.gravity_value));

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

                    compressionDepthFragment.resetColours(lastDepthValue);
                    compressionDepthFragment.changeTextView(value);


                    lastDepthValue = value;

                } else if (depthRate == 1) {
                    Log.d(TAG, "handleMessage: rate: " + value);

                    compressionRateFragment.resetColours(lastRateValue);
                    compressionRateFragment.changeTextView(value);

                    lastRateValue = value;

                }

            }
        };

    }


    private void makeFilePath() {
        //This and the next three should be done first.

    }

    private void checkFilePath() {
        //Checks for repeat User

    }

    private void getPreviousUserScore() {
//#Some arbitrary algorithm
//def getExistingScore(filePath, absRate, absDepth, numpy, sysVersion):
//    rates = []
//    depths = []
//    if int(sysVersion) < 3:
//        with open(filePath, 'rb') as csvfile:
//            return getScores(csvfile, rates, depths, absRate, absDepth, numpy)
//
//    else:
//        with open(filePath, 'r') as csvfile:
//            return getScores(csvfile, rates, depths, absRate, absDepth, numpy)
        //Then remove that file to overwrite it
    }

    //     msgDepth, msgRate = feedback.compareScore(currentScore, previousScore)
    // if iteration > minIterations:
    //        [depth, rate] = feedback.depth_rate(sofT,
    //                        maxDepth,
    //                        minDepth,
    //                        depthTolerance,
    //                        rate,
    //                        maxRate,
    //                        minRate,
    //                        rateTolerance,
    //                        msgDepth,
    //                        msgRate)

    private void getScores() {

        //def getScores(csvfile, rates, depths, absRate, absDepth, numpy):
        //    f = csv.reader(csvfile, quotechar='|', quoting=csv.QUOTE_MINIMAL)
        //    for row in f:
        //        if row:
        //            rates.append(int(row[0]))
        //            depths.append(float(row[1]))
        //
        //            #Include standard deviation in this?
        //    avgRate = numpy.mean(rates)
        //    avgDepth = numpy.mean(depths)
        //
        //    rateScore = abs(avgRate - absRate)/absRate + numpy.std(rates)
        //    depthScore = abs(avgDepth - absDepth)/absDepth + numpy.std(depths)
        //
        //    return rateScore, depthScore
    }
    private void startSpectralAnalysis() {

        SpectralAnalysis spectralAnalysisThread = new SpectralAnalysis(txyz, desiredListSizeForCompression, offsetAcceleration, mHandler);
        new Thread(spectralAnalysisThread).start();


    }

    private void writeToRecord() {
       // feedback.writeToRecord(filePath, depth, rate, sysVersion)
        // filePath = directory + "/" + fileName + "_" + age + ".csv"

    }

}

