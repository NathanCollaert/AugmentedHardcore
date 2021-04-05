package com.backtobedrock.augmentedhardcore.utils;

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

    public static String getTimeFromTicks(long amount, boolean digital, boolean longVersion) {
        long timeInTicks = amount;
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
