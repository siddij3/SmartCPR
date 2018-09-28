package com.smartcpr.junaid.smartcpr.SpectralAnalysisFragments;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.smartcpr.junaid.smartcpr.R;

import java.util.Arrays;

public class CompressionDepthFragment extends Fragment{

    private final static String TAG = "CompressionDepthFrag";
    View view;

    private float[] depths;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compression_depth, container, false);
        this.view = view;


        return view;
    }

    public void compressionDepthsForAgeGroups(int minDepth, int maxDepth) {
        depths = new float[9];

        float val = (float) (minDepth - 1.5);

        for (int i = 0; i < depths.length; i++)
            depths[i] = val + (float) i / 2;


        setCompressionDepthTexts();
    }

    private void setCompressionDepthTexts() {
        TextView textView;
        String cm = " cm";
        String greaterThan = "> ";
        String lessThan = "< ";

        textView = view.findViewById(R.id.compression_depth_low_limit);
        textView.setText(String.format("%s%s%s", lessThan, String.valueOf(depths[0]), cm));

        textView = view.findViewById(R.id.compression_depth_low_2);
        textView.setText(String.format("%s%s", String.valueOf(depths[1]), cm));

        textView = view.findViewById(R.id.compression_depth_low_1);
        textView.setText(String.format("%s%s", String.valueOf(depths[2]), cm));

        textView = view.findViewById(R.id.compression_depth_good_low);
        textView.setText(String.format("%s%s", String.valueOf(depths[3]), cm));

        textView = view.findViewById(R.id.compression_depth_good_mid);
        textView.setText(String.format("%s%s", String.valueOf(depths[4]), cm));

        textView = view.findViewById(R.id.compression_depth_good_high);
        textView.setText(String.format("%s%s", String.valueOf(depths[5]), cm));

        textView = view.findViewById(R.id.compression_depth_high_1);
        textView.setText(String.format("%s%s", String.valueOf(depths[6]), cm));

        textView = view.findViewById(R.id.compression_depth_high_2);
        textView.setText(String.format("%s%s", String.valueOf(depths[7]), cm));

        textView = view.findViewById(R.id.compression_depth_high_limit);
        textView.setText(String.format("%s%s%s", greaterThan, String.valueOf(depths[8]), cm));

    }

    public void changeTextView(double depth) {
        double roundedDepth = Math.round(depth*5) / 5;

        TextView txtView = findTextView(roundedDepth);
        int color = findColour(roundedDepth);

        txtView.setBackgroundResource(color);
        txtView.setTextColor(Color.WHITE);

    }

    private int findColour(double roundedDepth) {
        int color;

        if (roundedDepth >= depths[3] & roundedDepth <= depths[5] ) {
            color = R.color.colorGreen;

        } else if (roundedDepth == depths[1] | roundedDepth == depths[2]
                | roundedDepth == depths[6] | roundedDepth == depths[7]) {
            color = R.color.colorYellow;

        } else  {
            color = R.color.colorRed;

        }

        return color;
    }

    public void resetColours(double lastDepthValue) {
        double roundedDepth = Math.round(lastDepthValue*5) /5;

        TextView textView = findTextView(roundedDepth);
        int color  = findColour(roundedDepth);

        textView.setBackgroundResource(R.color.colorWhite);
        textView.setTextColor(Color.parseColor(getString(color)));

    }


    private TextView findTextView(double roundedDepth) {
        TextView textView;

        if (roundedDepth <= depths[0])
            textView = view.findViewById(R.id.compression_depth_low_limit);

        else if (roundedDepth == depths[1])
            textView = view.findViewById(R.id.compression_depth_low_2);

        else if (roundedDepth == depths[2])
            textView = view.findViewById(R.id.compression_depth_low_1);

        else if (roundedDepth == depths[3])
            textView = view.findViewById(R.id.compression_depth_good_low);

        else if (roundedDepth == depths[4])
            textView = view.findViewById(R.id.compression_depth_good_mid);

        else if (roundedDepth == depths[5])
            textView = view.findViewById(R.id.compression_depth_good_high);

        else if (roundedDepth == depths[6])
            textView = view.findViewById(R.id.compression_depth_high_1);

        else if (roundedDepth == depths[7])
            textView = view.findViewById(R.id.compression_depth_high_2);

        else if (roundedDepth >= depths[8])
            textView = view.findViewById(R.id.compression_depth_high_limit);

        else
            textView = view.findViewById(R.id.compression_depth_low_limit);


        return textView;

    }

}
