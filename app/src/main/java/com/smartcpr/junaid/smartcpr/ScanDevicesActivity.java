package com.smartcpr.junaid.smartcpr;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class ScanDevicesActivity extends AppCompatActivity {
    private final static String TAG = "ScanDevicesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);

        Log.d(TAG,"onActivityResultCalled" );
    }

}

