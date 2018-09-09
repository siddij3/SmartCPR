package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

import org.w3c.dom.Text;

public class CompressionRateFragment extends Fragment{

    private final static String TAG = "CompressionRateFragment";
    View view;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compression_rate, container, false);

        this.view = view;

        return view;
    }



    public void changeTextView(double rate) {
        int roundedRate = (int) (5*(Math.round(rate/5)));

        TextView txtView = findTextView(roundedRate);

        int color = findColour(roundedRate);

        Log.d(TAG, "changeTextView: " +  color);

        txtView.setBackgroundResource(color);
        txtView.setTextColor(Color.WHITE);

    }

    public void resetColours(double lastRateValue) {
        int roundedRate = (int) (5*(Math.round(lastRateValue/5)));

        TextView textView = findTextView(roundedRate);
        int color  = findColour(roundedRate);

        textView.setBackgroundResource(R.color.colorWhite);
        textView.setTextColor(Color.parseColor(getString(color)));

    }

    private int findColour(int roundedRate) {
        int color;
        Log.d(TAG, "findColour: " + roundedRate);

        if (roundedRate <= 120 & roundedRate >= 100 ) {
            color = R.color.colorGreen;
        } else if (roundedRate == 130
                | roundedRate == 125
                |roundedRate == 95
                | roundedRate == 90 ) {
            color = R.color.colorYellow;

        } else  {
            color = R.color.colorRed;
        }
        return color;
    }

    private TextView findTextView(int roundedRate) {
        TextView textView;


        if (roundedRate >= 140) {
            textView = view.findViewById(R.id.compression_rate_140);

        } else if (roundedRate == 135) {
            textView = view.findViewById(R.id.compression_rate_135);

        } else if (roundedRate == 130) {
            textView = view.findViewById(R.id.compression_rate_130);

        } else if (roundedRate == 125) {
            textView = view.findViewById(R.id.compression_rate_125);

        } else if (roundedRate == 120) {
            textView = view.findViewById(R.id.compression_rate_120);

        } else if (roundedRate == 115) {
            textView = view.findViewById(R.id.compression_rate_115);

        } else if (roundedRate == 110) {
            textView = view.findViewById(R.id.compression_rate_110);

        } else if (roundedRate == 105) {
            textView = view.findViewById(R.id.compression_rate_105);

        } else if (roundedRate == 100) {
            textView = view.findViewById(R.id.compression_rate_100);

        } else if (roundedRate == 95) {
            textView = view.findViewById(R.id.compression_rate_95);

        } else if (roundedRate == 90) {
            textView = view.findViewById(R.id.compression_rate_90);

        } else if (roundedRate == 85) {
            textView = view.findViewById(R.id.compression_rate_85);

        } else if (roundedRate <= 80) {
            textView = view.findViewById(R.id.compression_rate_80);

        } else  {
            textView = view.findViewById(R.id.compression_rate_80);
        }

        Log.d(TAG, "findTextView: " + textView.getText());


        return textView;

    }





}
