package com.smartcpr.junaid.smartcpr;

/**
 * Created by junaid on 11/16/17.
 */

public class Victim {
    private String victimAge;

    private int maxDepth;
    private int minDepth;

    public int maxRate;
    public int minRate;

    public Victim(String victimAge, int maxDepth, int minDepth) {
        this.victimAge = victimAge;
        this.maxDepth = maxDepth;
        this.minDepth = minDepth;

        maxRate = 120;
        minRate = 100;
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

    public String getVictimAge() {
        return victimAge;
    }

    public void setVictimAge(String victimAge) {
        this.victimAge = victimAge;
    }
}
