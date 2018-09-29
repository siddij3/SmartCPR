package com.smartcpr.trainer.smartcpr.ObjectClasses;

/**
 * Created by junaid on 11/16/17.
 */

public class Victim {
    private final String victim;

    private int maxDepth;
    private final int minDepth;

    private final int maxRate;
    private final int minRate;
    private double depthTolerance;


    public Victim(String victim, int maxDepth, int minDepth, double depthTolerance) {
        this.victim = victim;
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;
        this.depthTolerance = depthTolerance;

        maxRate = 120;
        minRate = 100;
    }

    public int maxDepth() {
        return maxDepth;
    }

    public int minDepth() {
        return minDepth;
    }

}
