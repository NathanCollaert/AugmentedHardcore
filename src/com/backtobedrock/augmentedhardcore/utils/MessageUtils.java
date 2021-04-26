package com.backtobedrock.augmentedhardcore.utils;

import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MessageUtils {
    public static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy',' HH:mm z").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter MEDIUM_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy',' HH:mm z").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter LONG_FORMATTER = DateTimeFormatter.ofPattern("EEEE MMM dd yyyy 'at' HH:mm:ss z").withZone(ZoneId.systemDefault());

    public static long timeUnitToTicks(long time, TimeUnit unit) {
        return unit.toSeconds(time) * 20;
    }

    public static String getTimeFromTicks(long amount, TimePattern pattern) {
        StringBuilder sb = new StringBuilder();

        long sec = (amount / 20), seconds = sec % 60, minutes = sec % 3600 / 60, hours = sec % 86400 / 3600, days = sec / 86400;

        switch (pattern) {
            case LONG:
                if (days > 0) {
                    sb.append(days).append(days == 1 ? " day" : " days");
                }
                if (hours > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(hours).append(hours == 1 ? " hour" : " hours");
                }
                if (minutes > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(minutes).append(minutes == 1 ? " minute" : " minutes");
                }
                if (seconds > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(seconds).append(seconds == 1 ? " second" : " seconds");
                }
                break;
            case SHORT:
                if (days > 0) {
                    sb.append(days).append("d");
                }
                if (hours > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(hours).append("h");
                }
                if (minutes > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(minutes).append("m");
                }
                if (seconds > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(seconds).append("s");
                }
                break;
            case DIGITAL:
                sb.append(days > 9 ? days : "0" + days).append(":").append(hours > 9 ? hours : "0" + hours).append(":").append(minutes > 9 ? minutes : "0" + minutes).append(":").append(seconds > 9 ? seconds : "0" + seconds);
                break;
        }

        return sb.toString();
    }

    public static String replacePlaceholders(String string, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            string = string.replaceAll("%" + entry.getKey().toLowerCase() + "%", entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static ItemStack replaceItemNamePlacholders(ItemStack item, Map<String, String> placeholders) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(replacePlaceholders(itemMeta.getDisplayName(), placeholders));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public static ItemStack replaceItemLorePlacholders(ItemStack item, Map<String, String> placeholders) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.getLore() != null) {
            itemMeta.setLore(itemMeta.getLore().stream().map(e -> replacePlaceholders(e, placeholders)).collect(Collectors.toList()));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public static ItemStack replaceItemNameAndLorePlaceholders(ItemStack item, Map<String, String> placeholders) {
        return replaceItemNamePlacholders(replaceItemLorePlacholders(item, placeholders), placeholders);
    }
}
