package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxHealthConfiguration {
    private final boolean useMaxHealth;
    private final double maxHealth;
    private final double minHealth;
    private final double maxHealthAfterBan;
    private final double maxHealthDecreasePerDeath;
    private final boolean maxHealthIncreaseOnKill;
    private final Map<EntityType, Double> maxHealthIncreasePerKill;
    private final boolean getMaxHealthByPlaytime;
    private final int playtimePerHalfHeart;
    private final List<String> disableLosingMaxHealthInWorlds;
    private final List<String> disableGainingMaxHealthInWorlds;
    private final boolean disableArtificialRegeneration;

    public MaxHealthConfiguration(boolean useMaxHealth, double maxHealth, double minHealth, double maxHealthAfterBan, double maxHealthDecreasePerDeath, boolean maxHealthIncreaseOnKill, Map<EntityType, Double> maxHealthIncreasePerKill, boolean getMaxHealthByPlaytime, int playtimePerHalfHeart, List<String> disableLosingMaxHealthInWorlds, List<String> disableGainingMaxHealthInWorlds, boolean disableArtificialRegeneration) {
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
        this.disableArtificialRegeneration = disableArtificialRegeneration;
    }

    public static MaxHealthConfiguration deserialize(ConfigurationSection section) {
        boolean cUseMaxHealth = section.getBoolean("UseMaxHealth", true);
        double cMaxHealth = ConfigUtils.checkMinMax("MaxHealth", section.getDouble("MaxHealth", 20), 1, 2048);
        double cMinHealth = ConfigUtils.checkMinMax("MinHealth", section.getDouble("MinHealth", 6), 1, 2048);
        double cMaxHealthAfterBan = section.getDouble("MaxHealthAfterBan", 20) == -1 ? -1 : ConfigUtils.checkMinMax("MaxHealthAfterBan", section.getDouble("MaxHealthAfterBan", 20), -1, 2048);
        double cMaxHealthDecreasePerDeath = ConfigUtils.checkMinMax("MaxHealthDecreasePerDeath", section.getDouble("MaxHealthDecreasePerDeath", 2), 1, 2048);
        boolean cMaxHealthIncreaseOnKill = section.getBoolean("MaxHealthIncreaseOnKill", true);
        Map<EntityType, Double> cMaxHealthIncreasePerKill = new HashMap<>();
        boolean cGetMaxHealthByPlaytime = section.getBoolean("GetMaxHealthByPlaytime", false);
        int cPlaytimePerHalfHeart = ConfigUtils.checkMin("PlaytimePerHalfHeart", section.getInt("PlaytimePerHalfHeart", 30), 1);
        List<String> cDisableLosingMaxHealthInWorlds = ConfigUtils.getWorlds("DisableLosingMaxHealthInWorlds", section.getStringList("DisableLosingMaxHealthInWorlds"));
        List<String> cDisableGainingMaxHealthInWorlds = ConfigUtils.getWorlds("DisableGainingMaxHealthInWorlds", section.getStringList("DisableGainingMaxHealthInWorlds"));
        boolean cDisableArtificialRegeneration = section.getBoolean("DisableArtificialRegeneration", false);

        //cMaxHealthIncreasePerKill
        ConfigurationSection maxHealthIncreasePerKillSection = section.getConfigurationSection("MaxHealthIncreasePerKill");
        if (maxHealthIncreasePerKillSection != null) {
            maxHealthIncreasePerKillSection.getKeys(false).forEach(e -> {
                EntityType type = ConfigUtils.getLivingEntityType("MaxHealthIncreasePerKill", e);
                if (type != null) {
                    double amount = ConfigUtils.checkMinMax("MaxHealthIncreasePerKill." + e, maxHealthIncreasePerKillSection.getDouble(e, 0), 0, 2048);
                    if (amount != -10)
                        cMaxHealthIncreasePerKill.put(type, amount);
                }
            });
        }

        if (cMaxHealth == -10 || cMinHealth == -10 || cMaxHealthAfterBan == -10 || cMaxHealthDecreasePerDeath == -10)
            return null;

        return new MaxHealthConfiguration(
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
                cDisableGainingMaxHealthInWorlds,
                cDisableArtificialRegeneration
        );
    }

    public boolean isDisableArtificialRegeneration() {
        return disableArtificialRegeneration;
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

    public Map<EntityType, Double> getMaxHealthIncreasePerKill() {
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
