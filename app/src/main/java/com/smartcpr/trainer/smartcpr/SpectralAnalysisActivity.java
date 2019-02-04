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

/**
 * SpectralAnalysisActivity
 *
 * Fourth activity after user opens app
 *
 * Displays numbers for compression depths and rates, identifying which corresponds to user's
 * compressions, and gives feedback for returning users
 *
 *
 * Primary Functions
 *
 * onCreate: Initializes and instantiates variables, methods and class objects for IMU calibration
 * handleMessage: handles messages from calibration thread
 * handleFeedback: sets the details for victim ages, creates instance of victim class (child, youth,
 *              adult) and begins preparations for spectral analysis activity
 *
 *
 * makeFilePathAndGetPastScore: calibrates IMU, records offset data and sends message on succesful/failed
 *                      calibration
 *
 *
 */

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

    private boolean repeatUser;
    float[] userScores;

    int minIterations;
    int iteration;

    File file;

    CompressionDepthFragment compressionDepthFragment;
    CompressionRateFragment compressionRateFragment;


    // Fragment and Object classes for giving user feedback
    UserScoreFragment userScoreFragment;
    UserFeedback userFeedback;


    int minVictimDepth;
    int maxVictimDepth;



    private String getMinDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MIN_DEPTH);  }
    private String getMaxDepth() { return Objects.requireNonNull(bundle).getString(EXTRA_VICTIM_MAX_DEPTH); }
    private String getOffsetAcceleration() { return Objects.requireNonNull(bundle).getString(EXTRA_OFFSET_ACCELERATION_VALUE); }
    private String getUserName() { return Objects.requireNonNull(bundle).getString(EXTRA_USER_NAME); }

    /**
     * onCreate
     *
     *
     * Method:
     *  Unpackages bundle from previous activity to get depths, username and offset acceleration
     *  Initializes index items from IMU, and list lengths for compression windows
     *
     *  Initializes Fragments and object classes associated with spectral analysis
     *
     *  Initializes handlers for handling information from spectral analysis thread
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);

        context = getApplicationContext();

        intent = getIntent();
        bundle = intent.getExtras();

        repeatUser = false;

        // Parses information from previous activity
        minVictimDepth = Integer.parseInt(getMinDepth());
        maxVictimDepth = Integer.parseInt(getMaxDepth());
        offsetAcceleration = Float.parseFloat(getOffsetAcceleration());
        String userName = getUserName().toLowerCase();

        // For formatting and applying spectral analysis on data from IMU
        txyz = getResources().getInteger(R.integer.array_index_txyz);
        desiredListSizeForCompression = getResources().getInteger(R.integer.compression_list_size);
        GRAVITY = 9.8f;

        // Iterations of analysis (512*5 lines of data) before giving feedback
        minIterations = 5;
        iteration = 0;

        // Fragments
        compressionDepthFragment = (CompressionDepthFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_compression_depth);
        compressionRateFragment = (CompressionRateFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_compression_rate);

        userScoreFragment = (UserScoreFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_user_score_feedback);

        compressionDepthFragment.compressionDepthsForAgeGroups(minVictimDepth, maxVictimDepth);



        handleMessage();
        handleFeedback();

        makeFilePathAndGetPastScore(userName, minVictimDepth, maxVictimDepth);
        startSpectralAnalysis();
    }

    /**
     * handleMessage
     *
     *
     * Method:
     *  The depth and rate of the compressions are displayed to the user
     *  via a change in colours of the text and background of the displayed text labels
     */
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

    /**
     * handleFeedback
     *
     *
     * Method:
     *  Takes the rate and depth of the compressions to add to the user's score file
     *  for comparison with future compressions and to determine if there has been an improvement
     */
    void handleFeedback() {
        nHandler = new Handler(Looper.getMainLooper()) {

            // Takes user scores and passes it off to fragment for feedback to user
            public void handleMessage(Message message) {

                String[] strScores =  message.obj.toString().split(",");

                double depthScore = Double.valueOf(strScores[0]);
                double rateScore = Double.valueOf(strScores[1]);


                userScoreFragment.compareScores(userScores, new double[]{depthScore, rateScore});


            }
        };

    }


    /**
     * makeFilePathAndGetPastScore
     *
     *
     * Method:
     *  Takes the name of the user, the min and max depths to create
     *  or locate the file path. (Does not create)
     *  If a file already exists, it creates a performance score and deletes the file
     *
     */
    private void makeFilePathAndGetPastScore(String userName, int min, int max) {


        // Creates an instance of specifying name and directory
        String filepath =  userName + "_"  + min + "_" + max +  ".csv";
        file = new File(String.valueOf(context.getFilesDir()), filepath);


        float absRate = 120;
        float absDepth = SimpleMathOps.getMeanValue(new int[] {minVictimDepth, maxVictimDepth});

        // stores information about file - path and directory, rate and depth information
        userFeedback = new UserFeedback(file, absDepth, absRate, nHandler, context);

        //  File directory information - file names of all files in the directory
        File directory = new File(String.valueOf(context.getFilesDir()));

        Log.d(TAG, "makeFilePathAndGetPastScore: ASDF " + Arrays.toString(directory.listFiles()));
        File[] files = directory.listFiles();

        // Checks if the file exists (if user is repeat user on the device for score comparison)
        // If yes, score is calculated and the file is deleted to make space for the new
        // performance checking
        for (File file : files) {
            if (file.getName().contains(filepath)) {

                repeatUser = true;
                userScoreFragment.setFeedbackMessage(getResources().getString(R.string.returning_user_message));

                userScores = userFeedback.getPreviousScores();

                if (file.delete())
                    Log.d(TAG, "makeFilePathAndGetPastScore: File was removed");

                break;
            }

        }


    }


    // Starts spectral analysis thread
    private void startSpectralAnalysis() {

        SpectralAnalysis spectralAnalysisThread = new SpectralAnalysis(txyz,
                desiredListSizeForCompression, offsetAcceleration,  mHandler, GRAVITY);
        new Thread(spectralAnalysisThread).start();


    }


}

