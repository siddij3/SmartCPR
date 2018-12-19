package com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

public class UserScoreFragment extends Fragment {

    private final static String TAG = "UserScoreFragment";

    private TextView mPerformanceFeedback;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_feedback, container, false);

        mPerformanceFeedback = view.findViewById(R.id.user_feedback);

        return view;
    }

    public void setFeedbackMessage(String msg) {
        mPerformanceFeedback.setText(msg);

        Log.d(TAG, "setFeedbackMessage: ");
    }

    //0th index is depth. 1st index is rate
    public void compareScores(float[] userScores, double[] newScores) {
        String msgDepth;
        String msgRate;


        double oldDepthScore = (double) userScores[0];
        double oldRateScore = (double)  userScores[1];

        double newDepthScore = newScores[0];
        double newRateScore = newScores[1];


        //Smaller score is better
        if (newDepthScore <oldDepthScore) {
            msgDepth = "Better Depth";
        } else if (newDepthScore > oldDepthScore) {
            msgDepth = "Worse Depth ";
        } else {
            msgDepth = "About the Same Depth";
        }

        if (newRateScore < oldRateScore) {
            msgRate = "Better Rate ";

        } else if (newRateScore > oldRateScore) {
            msgRate = "Worse Rate ";

        } else {
            msgRate = "About the Same Rate";

        }

        String msg = msgRate + ", " + msgDepth;

        setFeedbackMessage(msg);

    }
}