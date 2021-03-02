package com.backtobedrock.LiteDeathBan.utils;

import java.util.concurrent.TimeUnit;

public class MessageUtils {
    public static String getTimeFromTicks(int amount, boolean digital, boolean longVersion) {
        int timeInTicks = amount;
        StringBuilder sb = new StringBuilder();

        long days = TimeUnit.SECONDS.toDays(timeInTicks / 20);
        timeInTicks -= TimeUnit.DAYS.toSeconds(days * 20);

        long hours = TimeUnit.SECONDS.toHours(timeInTicks / 20);
        timeInTicks -= TimeUnit.HOURS.toSeconds(hours * 20);

        long minutes = TimeUnit.SECONDS.toMinutes(timeInTicks / 20);
        timeInTicks -= TimeUnit.MINUTES.toSeconds(minutes * 20);

        long seconds = timeInTicks / 20;
        timeInTicks -= seconds * 20;
        if (digital) {
            sb.append(days > 9 ? days : "0" + days).append(":").append(hours > 9 ? hours : "0" + hours).append(":").append(minutes > 9 ? minutes : "0" + minutes).append(":").append(seconds > 9 ? seconds : "0" + seconds);
        } else {
            if (days > 0) {
                sb.append(days).append(longVersion ? days > 1 ? " days" : " day" : "d");
            }
            if (hours > 0) {
                if (!sb.toString().isEmpty()) {
                    sb.append(", ");
                }
                sb.append(hours).append(longVersion ? hours > 1 ? " hours" : " hour" : "h");
            }
            if (minutes > 0) {
                if (!sb.toString().isEmpty()) {
                    sb.append(", ");
                }
                sb.append(minutes).append(longVersion ? minutes > 1 ? " minutes" : " minute" : "m");
            }
            if (seconds > 0) {
                if (!sb.toString().isEmpty()) {
                    sb.append(", ");
                }
                sb.append(seconds).append(longVersion ? seconds > 1 ? " seconds" : " second" : "s");
            }
            if (sb.toString().isEmpty()) {
                sb.append(timeInTicks).append(longVersion ? timeInTicks > 1 ? " ticks" : " tick" : "t");
            }
        }

        return sb.toString();
    }
}
