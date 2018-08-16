package com.smartcpr.junaid.smartcpr.ObjectClasses;

/**
 * Created by junaid on 11/16/17.
 */

public class Victim {
    private String victim;

    private int maxDepth;
    private int minDepth;

    private int maxRate;
    private int minRate;
    private double depthTolerance;


    public Victim(String victim, int maxDepth, int minDepth, double depthTolerance) {
        this.victim = victim;
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
        this.depthTolerance = depthTolerance;

        maxRate = 120;
        minRate = 100;
    }

    public double getDepthTolerance() {
        return depthTolerance;
    }

    public void setDepthTolerance(double depthTolerance) {
        this.depthTolerance = depthTolerance;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public void setMinDepth(int minDepth) {
        this.minDepth = minDepth;
    }

    public int getMaxRate() { return maxRate; }

    public int getMinRate() { return minRate; }

    public String getVictim() { return victim; }
}
