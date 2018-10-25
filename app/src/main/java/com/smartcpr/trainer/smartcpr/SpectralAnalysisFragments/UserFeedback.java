package com.smartcpr.trainer.smartcpr.SpectralAnalysisFragments;

import android.content.Context;
import android.os.Message;
import android.util.Log;

import com.smartcpr.trainer.smartcpr.MathOperationsClasses.SimpleMathOps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import android.os.Handler;

public class UserFeedback {
    private static final String TAG = "UserFeedback";
    private int numRows;

    private final File mFile;

    private float absRate, absDepth;

    private float[] userScores;

    private double depth;
    private double  rate;

    private Handler nHandler;

    Context mContext;

    //This iis for reading from file for score check
    public UserFeedback(File file, float absRate, float absDepth, Handler handler, Context context) {
        this.mFile = file;
        this.absDepth = absDepth;
        this.absRate = absRate;
        this.nHandler = handler;

        this.mContext = context;

        depth = 0f;
        rate = 0f;
    }


    public float[] getPreviousScores() {
        //Read text from file
        StringBuilder data = new StringBuilder();

        Log.d(TAG, "getPreviousScores: KEY");

        try {
            BufferedReader br = new BufferedReader(new FileReader(mFile));
            String line;

            while ((line = br.readLine()) != null) {
                Log.d(TAG, "getPreviousScores: KEY" + line);
                data.append(line);
                data.append(';');
            }
            br.close();
        }
        catch (IOException e) {
            Log.e(TAG, "UserFeedback: ", e);
        }

        userScores = getScore(data.toString());

        return userScores;
    }

    public void getNewScores() {
        ReadFromRecord readFromRecord = new ReadFromRecord(userScores, nHandler);
        new Thread(readFromRecord).start();
    }

    private float[] getScore(String data) {
        String[] rows = data.split(";");

        float[] depth = new float[rows.length];
        float[] rate = new float[rows.length];


            for (int i = 0; i < rows.length; i++) {
                String[] tmp = rows[i].split(",");

                for (String str: tmp) {
                    if (str == "")
                        continue;

                }

                Log.d(TAG, "getScore: ASDF " + Arrays.toString(tmp));
                depth[i] = Float.parseFloat(tmp[0]);
                rate[i] = Float.parseFloat(tmp[1]);

            }

            float avgDepth = SimpleMathOps.getMeanValue(depth);
            float avgRate = SimpleMathOps.getMeanValue(rate);

            float depthScore = (float) (Math.abs(avgDepth - absDepth) / absDepth +
                    SimpleMathOps.standardDeviation(depth));

            float rateScore = (float) (Math.abs(avgRate - absRate) / absRate +
                    SimpleMathOps.standardDeviation(rate));

            return new float[]{depthScore, rateScore};

    }

    public void writeToRecord(double newDepth, double newRate) {
        this.depth = newDepth;
        this.rate = newRate;

        new Thread(new WriteToRecord(mFile, depth, rate)).start();
    }

    private class WriteToRecord implements  Runnable {
        double depth, rate;
        File mmFile;

        WriteToRecord(File file, double depth, double rate) {
            this.depth = depth;
            this.rate = rate;
            mmFile = file;

        }

        public void run() {

            String data = depth + "," + rate + ";";
            Log.d(TAG, "run: ASDF" + data);

            try {
                FileOutputStream outputStream = new FileOutputStream (new File(mmFile.getAbsolutePath().toString()), true);

                outputStream.write(data.getBytes());
                outputStream.close();

                Log.d(TAG, "run: appended?");


            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "run: " + e);
            }

        }

    }

    private class ReadFromRecord implements  Runnable {
        Handler nnHandler;

        float depthScore;
        float rateScore;

        ReadFromRecord(float[] scores,  Handler handler) {
            this.depthScore = scores[0];
            this.rateScore = scores[1];

            this.nnHandler = handler;

        }

        public void run() {

            StringBuilder data = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(mFile));
                String line;

                while ((line = br.readLine()) != null) {
                    data.append(line);
                    data.append(';');
                }
                br.close();
            }
            catch (IOException e) {
                Log.e(TAG, "UserFeedback: ", e);
            }

            float[] score = getScore(data.toString());

            String tmp = score[0] + "," + score[1];

            Message messageScores =  nnHandler.obtainMessage(0, tmp);

            messageScores.sendToTarget();
        }

    }


}

