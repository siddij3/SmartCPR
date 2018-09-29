package com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

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

        switch (roundedRate) {
            case 135:
                textView = view.findViewById(R.id.compression_rate_135);
                break;
            case 130:
                textView = view.findViewById(R.id.compression_rate_130);
                break;
            case 125:
                textView = view.findViewById(R.id.compression_rate_125);
                break;
            case 120:
                textView = view.findViewById(R.id.compression_rate_120);
                break;
            case 115:
                textView = view.findViewById(R.id.compression_rate_115);
                break;
            case 110:
                textView = view.findViewById(R.id.compression_rate_110);
                break;
            case 105:
                textView = view.findViewById(R.id.compression_rate_105);
                break;
            case 100:
                textView = view.findViewById(R.id.compression_rate_100);
                break;
            case 95:
                textView = view.findViewById(R.id.compression_rate_95);
                break;
            case 90:
                textView = view.findViewById(R.id.compression_rate_90);
                break;
            case 85:
                textView = view.findViewById(R.id.compression_rate_85);
                break;
            default:
                if (roundedRate >= 140) {
                    textView = view.findViewById(R.id.compression_rate_140);
                }
                else {
                    textView = view.findViewById(R.id.compression_rate_80);
                }
        }


        return textView;

    }





}
