package com.smartcpr.junaid.smartcpr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.smartcpr.junaid.smartcpr.BluetoothData.BluetoothStream;

public class CompressionFeedbackActivity extends AppCompatActivity {

    private final static String TAG = "CompressionFeed";
    private BluetoothStream bluetoothStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spectral_analysis);
        Log.d(TAG, "onCreate: " + TAG);

        bluetoothStream = new BluetoothStream();

    }
}
