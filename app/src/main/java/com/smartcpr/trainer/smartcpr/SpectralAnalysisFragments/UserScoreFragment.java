package com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartcpr.junaid.smartcpr.R;

public class UserScoreFragment extends Fragment {
    private void getExistingScore(String filePath, float absRate, float absDepth) {
        float[] rates = new float[];
        float[] depths = new float[];


    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_feedback, container, false);


        return view;
    }
}

/*


def compareScore(currentScore, previousScore):
    msgDepth = ""
    msgRate = ""
    if not currentScore or not previousScore:
        return "", ""

    if currentScore[0] < previousScore[0]:
        msgDepth = "Better Depth Than Your Previous Attempt"
    elif currentScore[0] > previousScore[0]:
        msgDepth = "Worse Depth Than Your Previous Attempt"
    else:
        msgDepth = "About the Same as Your Previous Attempt"

    return msgDepth, msgRate
    */