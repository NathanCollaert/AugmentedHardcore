package com.backtobedrock.LiteDeathBan.domain.enums;

public enum GrowthType {
    LINEAR,
    EXPONENTIAL;

    public int getBanTime(int time, int max) {
        switch (this) {
            case LINEAR:
                return Math.min((int) (time / .65), max);
            case EXPONENTIAL:
                return Math.min((int) (.65 * Math.pow(time, (double) time / max + .65)), max);
            default:
                return 0;
        }
    }
}
