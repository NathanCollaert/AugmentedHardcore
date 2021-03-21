package com.backtobedrock.augmentedhardcore.domain.enums;

public enum GrowthType {
    LINEAR,
    EXPONENTIAL;

    public int getBanTime(int time, int max) {
        switch (this) {
            case LINEAR:
                return Math.min((time + (time / 60)), max);
            case EXPONENTIAL:
                return Math.min((int) (Math.pow(60 * time, (double) time / max)), max);
            default:
                return 0;
        }
    }
}
