package com.backtobedrock.LiteDeathBan.utils;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.enums.*;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ConfigUtils {
    public static int checkMin(String id, int value, int min) {
        if (value >= min) {
            return value;
        } else {
            sendErrorMessage(String.format("%s: value cannot be lower than %d, your value is: %d", id, min, value));
            return -10;
        }
    }

    public static int checkMinBanTime(int value, int min) {
        if (value >= min) {
            return value;
        } else {
            return -10;
        }
    }

    public static int checkMax(String id, int value, int max) {
        if (value <= max) {
            return value;
        } else {
            sendErrorMessage(String.format("%s: value cannot be higher than %d, your value is: %d", id, max, value));
            return -10;
        }
    }

    public static int checkMinMax(String id, int value, int min, int max) {
        if (value >= min && value <= max) {
            return value;
        } else {
            sendErrorMessage(String.format("%s: value cannot be lower than %d and higher than %d, your value is: %d", id, min, max, value));
            return -10;
        }
    }

    public static List<String> getWorlds(String id, List<String> worlds) {
        List<String> lowercaseWorlds = worlds.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> lowercaseLoadedWorlds = Bukkit.getWorlds().stream().map(e -> e.getName().toLowerCase()).collect(Collectors.toList());
        for (String w : lowercaseWorlds) {
            if (!lowercaseLoadedWorlds.contains(w)) {
                lowercaseWorlds.remove(w);
                sendWarningMessage(String.format("Warning: %s in %s is not a currently loaded world.", w, id));
            }
        }
        return lowercaseWorlds;
    }

    public static StorageType getStorageType(String id, String storageType) {
        try {
            return StorageType.valueOf(storageType.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing storage type.", id, storageType));
            return null;
        }
    }

    public static Material getMaterial(String id, String material) {
        try {
            return Material.valueOf(material.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing material.", id, material));
            return null;
        }
    }

    public static GrowthType getGrowthType(String id, String growthType, GrowthType type) {
        try {
            return GrowthType.valueOf(growthType.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing growth type, default value will be used: %s.", id, growthType, type));
            return type;
        }
    }

    public static BanTimeType getBanTimeType(String id, String banTimeType, BanTimeType type) {
        try {
            return BanTimeType.valueOf(banTimeType.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing ban time type, default value will be used: %s.", id, banTimeType, type));
            return type;
        }
    }

    public static BanList.Type getBanType(String id, String banType, BanList.Type type) {
        try {
            return BanList.Type.valueOf(banType.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing ban type, default value will be used: %s.", id, banType, type));
            return type;
        }
    }

    public static BarStyle getBarStyle(String id, String barStyle, BarStyle type) {
        try {
            return BarStyle.valueOf(barStyle.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing bar type, default value will be used: %s.", id, barStyle, type));
            return type;
        }
    }

    public static BarColor getBarColor(String id, String barColor, BarColor type) {
        try {
            return BarColor.valueOf(barColor.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing bar color, default value will be used: %s.", id, barColor, type));
            return type;
        }
    }

    public static NotificationType getNotifcationType(String id, String notificationType) {
        try {
            return NotificationType.valueOf(notificationType.toUpperCase());
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing notification type.", id, notificationType));
            return null;
        }
    }

    public static DamageCause getDamageCause(String damageCause, DamageCause defaultValue) {
        try {
            return DamageCause.valueOf(damageCause.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static DamageCauseType getDamageCauseType(String damageCauseType, DamageCauseType defaultValue) {
        try {
            return DamageCauseType.valueOf(damageCauseType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    public static EntityType getEntityType(String entityType) {
        try {
            return EntityType.valueOf(entityType.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static EntityType getLivingEntityType(String id, String livingEntityType) {
        try {
            EntityType type = EntityType.valueOf(livingEntityType.toUpperCase());
            if (type.isAlive())
                return type;
            return null;
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not a living entity.", id, livingEntityType));
            return null;
        }
    }

    private static void sendErrorMessage(String message) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        plugin.getLogger().log(Level.SEVERE, message);
    }

    private static void sendWarningMessage(String message) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        plugin.getLogger().log(Level.INFO, message);
    }
}
