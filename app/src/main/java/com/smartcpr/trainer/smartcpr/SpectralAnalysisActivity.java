package com.smartcpr.trainer.smartcpr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.R;
import com.smartcpr.trainer.smartcpr.MathOperationsClasses.SimpleMathOps;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.CompressionDepthFragment;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.CompressionRateFragment;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.SpectralAnalysis;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.UserFeedback;
import com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments.UserScoreFragment;

import java.io.File;
import java.util.Arrays;
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

    private Context context;

    private int txyz;
    private int desiredListSizeForCompression;
    private float offsetAcceleration;


    private static float GRAVITY;

    Intent intent;
    Bundle bundle;

    private Handler mHandler;
    private Handler nHandler;


    private double lastRateValue;
    double lastDepthValue;

    private String userName;
    private boolean repeatUser;
    float[] userScores;

    int minIterations;
    int iteration;

    File file;

    CompressionDepthFragment compressionDepthFragment;
    CompressionRateFragment compressionRateFragment;

    UserScoreFragment userScoreFragment;

    int minVictimDepth;
    int maxVictimDepth;


    UserFeedback userFeedback;

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

        context = getApplicationContext();

        repeatUser = false;

        minVictimDepth = Integer.parseInt(getMinDepth());
        maxVictimDepth = Integer.parseInt(getMaxDepth());
        offsetAcceleration = Float.parseFloat(getOffsetAcceleration());
        userName = getUserName().toLowerCase();

        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeForCompression = getResources().getInteger(R.integer.compression_list_size);
        GRAVITY = 9.8f;

        minIterations = 5;
        iteration = 0;


        compressionDepthFragment = (CompressionDepthFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_compression_depth);
        compressionRateFragment = (CompressionRateFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_compression_rate);

        userScoreFragment = (UserScoreFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_user_score_feedback);


        compressionDepthFragment.compressionDepthsForAgeGroups(minVictimDepth, maxVictimDepth);



        handleMessage();
        handleFeedback();

        makeFilePathAndGetPastScore(userName, minVictimDepth, maxVictimDepth);
        startSpectralAnalysis();
    }


    void handleMessage() {
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message message) {
                String[] value =  message.obj.toString().split(",");

                compressionRateFragment.resetColours(lastRateValue);
                compressionDepthFragment.resetColours(lastDepthValue);

                double depth = Double.valueOf(value[0]);
                double rate = Double.valueOf(value[1]);

                compressionDepthFragment.changeTextView(depth);
                compressionRateFragment.changeTextView(rate);

                lastDepthValue = depth;
                lastRateValue = rate;
        
                try {
                    Log.d(TAG, "ASDF: " + userFeedback.toString());
                    userFeedback.writeToRecord(depth,  rate);
                } catch (Exception e) {
                    Log.e(TAG, "ASDF: ",e );
                }

                if (repeatUser){
                    iteration +=1;
                    if (iteration > minIterations){
                        userFeedback.getNewScores();
                    }
                }

            }
        };

    }    
    
    void handleFeedback() {
        nHandler = new Handler(Looper.getMainLooper()) {
            //Change Fragment Text here
            public void handleMessage(Message message) {
                int depthRate = message.what;

                String[] strScores =  message.obj.toString().split(",");

                double depthScore = Double.valueOf(strScores[0]);
                double rateScore = Double.valueOf(strScores[1]);

                Log.d(TAG, "makeFilePathAndGetPastScore: ASDF" + depthScore + "\t" + rateScore);


                userScoreFragment.compareScores(userScores, new double[]{depthRate, rateScore});


            }
        };

    }


    private void makeFilePathAndGetPastScore(String userName, int min, int max) {

        String filepath =  userName + "_"  + min + "_" + max +  ".csv";

        file = new File(String.valueOf(context.getFilesDir()), filepath);


        float absRate = 120;
        float absDepth = SimpleMathOps.getMeanValue(new int[] {minVictimDepth, maxVictimDepth});

        File directory = new File(String.valueOf(context.getFilesDir()));

        userFeedback = new UserFeedback(file, absDepth, absRate, nHandler, context);

        Log.d(TAG, "makeFilePathAndGetPastScore: ASDF " + Arrays.toString(directory.listFiles()));
        File[] files = directory.listFiles();

        for (File file : files) {
            if (file.getName().contains(filepath)) {

                repeatUser = true;
                userScoreFragment.setFeedbackMessage(getResources().getString(R.string.returning_user_message));

                userScores = userFeedback.getPreviousScores();

                Log.d(TAG, "makeFilePathAndGetPastScore: ASDF " + Arrays.toString(userScores));
                file.delete();
                break;
            }

        }


    }

    private void startSpectralAnalysis() {

        SpectralAnalysis spectralAnalysisThread = new SpectralAnalysis(txyz,
                desiredListSizeForCompression, offsetAcceleration,  mHandler, GRAVITY);
        new Thread(spectralAnalysisThread).start();


    }


}

