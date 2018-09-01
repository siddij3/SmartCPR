package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smartcpr.junaid.smartcpr.R;

public class CompressionDepthFragment extends Fragment{

    private final static String TAG = "CompressionDepthFrag";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compression_depth, container, false);


        return view;
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }
    //Find which rate corresponds to which label


    //New function. Change label colours

}
