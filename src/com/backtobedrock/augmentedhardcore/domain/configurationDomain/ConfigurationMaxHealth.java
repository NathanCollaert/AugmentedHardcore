package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.utilities.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.List;

public class ConfigurationMaxHealth {
    private final boolean useMaxHealth;
    private final double maxHealth;
    private final double minHealth;
    private final double maxHealthAfterBan;
    private final double maxHealthDecreasePerDeath;
    private final boolean maxHealthIncreaseOnKill;
    private final EnumMap<EntityType, Double> maxHealthIncreasePerKill;
    private final boolean getMaxHealthByPlaytime;
    private final int playtimePerHalfHeart;
    private final List<String> disableLosingMaxHealthInWorlds;
    private final List<String> disableGainingMaxHealthInWorlds;

    public ConfigurationMaxHealth(boolean useMaxHealth, double maxHealth, double minHealth, double maxHealthAfterBan, double maxHealthDecreasePerDeath, boolean maxHealthIncreaseOnKill, EnumMap<EntityType, Double> maxHealthIncreasePerKill, boolean getMaxHealthByPlaytime, int playtimePerHalfHeart, List<String> disableLosingMaxHealthInWorlds, List<String> disableGainingMaxHealthInWorlds) {
        this.useMaxHealth = useMaxHealth;
        this.maxHealth = maxHealth;
        this.minHealth = minHealth;
        this.maxHealthAfterBan = maxHealthAfterBan;
        this.maxHealthDecreasePerDeath = maxHealthDecreasePerDeath;
        this.maxHealthIncreaseOnKill = maxHealthIncreaseOnKill;
        this.maxHealthIncreasePerKill = maxHealthIncreasePerKill;
        this.getMaxHealthByPlaytime = getMaxHealthByPlaytime;
        this.playtimePerHalfHeart = playtimePerHalfHeart;
        this.disableLosingMaxHealthInWorlds = disableLosingMaxHealthInWorlds;
        this.disableGainingMaxHealthInWorlds = disableGainingMaxHealthInWorlds;
    }

    public static ConfigurationMaxHealth deserialize(ConfigurationSection section) {
        boolean cUseMaxHealth = section.getBoolean("UseMaxHealth", true);
        double cMaxHealth = ConfigUtils.checkMinMax("MaxHealth", section.getDouble("MaxHealth", 20), 1, Double.MAX_VALUE);
        double cMinHealth = ConfigUtils.checkMinMax("MinHealth", section.getDouble("MinHealth", 6), 1, Double.MAX_VALUE);
        double cMaxHealthAfterBan = section.getDouble("MaxHealthAfterBan", 20) == -1 ? -1 : ConfigUtils.checkMinMax("MaxHealthAfterBan", section.getDouble("MaxHealthAfterBan", 20), -1, Double.MAX_VALUE);
        double cMaxHealthDecreasePerDeath = ConfigUtils.checkMinMax("MaxHealthDecreasePerDeath", section.getDouble("MaxHealthDecreasePerDeath", 2), 1, Double.MAX_VALUE);
        boolean cMaxHealthIncreaseOnKill = section.getBoolean("MaxHealthIncreaseOnKill", true);
        EnumMap<EntityType, Double> cMaxHealthIncreasePerKill = new EnumMap<>(EntityType.class);
        boolean cGetMaxHealthByPlaytime = section.getBoolean("GetMaxHealthByPlaytime", false);
        int cPlaytimePerHalfHeart = ConfigUtils.checkMinMax("PlaytimePerHalfHeart", section.getInt("PlaytimePerHalfHeart", 30), 1, Integer.MAX_VALUE);
        List<String> cDisableLosingMaxHealthInWorlds = ConfigUtils.getWorlds("DisableLosingMaxHealthInWorlds", section.getStringList("DisableLosingMaxHealthInWorlds"));
        List<String> cDisableGainingMaxHealthInWorlds = ConfigUtils.getWorlds("DisableGainingMaxHealthInWorlds", section.getStringList("DisableGainingMaxHealthInWorlds"));

        //cMaxHealthIncreasePerKill
        ConfigurationSection maxHealthIncreasePerKillSection = section.getConfigurationSection("MaxHealthIncreasePerKill");
        if (maxHealthIncreasePerKillSection != null) {
            maxHealthIncreasePerKillSection.getKeys(false).forEach(e -> {
                EntityType type = ConfigUtils.getLivingEntityType("MaxHealthIncreasePerKill", e);
                if (type != null) {
                    double amount = ConfigUtils.checkMinMax("MaxHealthIncreasePerKill." + e, maxHealthIncreasePerKillSection.getDouble(e, 0), 0, Integer.MAX_VALUE);
                    if (amount != -10)
                        cMaxHealthIncreasePerKill.put(type, amount);
                }
            });
        }

        if (cMaxHealth == -10 || cMinHealth == -10 || cMaxHealthAfterBan == -10 || cMaxHealthDecreasePerDeath == -10 || cPlaytimePerHalfHeart == -10) {
            return null;
        }

        return new ConfigurationMaxHealth(
                cUseMaxHealth,
                cMaxHealth,
                cMinHealth,
                cMaxHealthAfterBan,
                cMaxHealthDecreasePerDeath,
                cMaxHealthIncreaseOnKill,
                cMaxHealthIncreasePerKill,
                cGetMaxHealthByPlaytime,
                cPlaytimePerHalfHeart * 1200,
                cDisableLosingMaxHealthInWorlds,
                cDisableGainingMaxHealthInWorlds
        );
    }

    public boolean isUseMaxHealth() {
        return useMaxHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getMinHealth() {
        return minHealth;
    }

    public double getMaxHealthAfterBan() {
        return maxHealthAfterBan;
    }

    public double getMaxHealthDecreasePerDeath() {
        return maxHealthDecreasePerDeath;
    }

    public boolean isMaxHealthIncreaseOnKill() {
        return maxHealthIncreaseOnKill;
    }

    public EnumMap<EntityType, Double> getMaxHealthIncreasePerKill() {
        return maxHealthIncreasePerKill;
    }

    public List<String> getDisableLosingMaxHealthInWorlds() {
        return disableLosingMaxHealthInWorlds;
    }

    public List<String> getDisableGainingMaxHealthInWorlds() {
        return disableGainingMaxHealthInWorlds;
    }

    public boolean isGetMaxHealthByPlaytime() {
        return getMaxHealthByPlaytime;
    }

    public int getPlaytimePerHalfHeart() {
        return playtimePerHalfHeart;
    }
}
