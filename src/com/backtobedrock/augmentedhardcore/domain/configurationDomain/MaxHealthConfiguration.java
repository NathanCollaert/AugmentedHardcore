package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaxHealthConfiguration {
    private final boolean useMaxHealth;
    private final double maxHealthAtStart;
    private final double maxHealth;
    private final double minHealth;
    private final double maxHealthAfterBan;
    private final double maxHealthDecreasePerDeath;
    private final boolean maxHealthIncreaseOnKill;
    private final Map<EntityType, Double> maxHealthIncreasePerKill;
    private final List<String> disableLosingMaxHealthInWorlds;
    private final List<String> disableGainingMaxHealthInWorlds;
    private final boolean disableNaturalRegeneration;
    private final boolean disableArtificialRegeneration;

    public MaxHealthConfiguration(boolean useMaxHealth, double maxHealthAtStart, double maxHealth, double minHealth, double maxHealthAfterBan, double maxHealthDecreasePerDeath, boolean maxHealthIncreaseOnKill, Map<EntityType, Double> maxHealthIncreasePerKill, List<String> disableLosingMaxHealthInWorlds, List<String> disableGainingMaxHealthInWorlds, boolean disableNaturalRegeneration, boolean disableArtificialRegeneration) {
        this.useMaxHealth = useMaxHealth;
        this.maxHealthAtStart = maxHealthAtStart;
        this.maxHealth = maxHealth;
        this.minHealth = minHealth;
        this.maxHealthAfterBan = maxHealthAfterBan;
        this.maxHealthDecreasePerDeath = maxHealthDecreasePerDeath;
        this.maxHealthIncreaseOnKill = maxHealthIncreaseOnKill;
        this.maxHealthIncreasePerKill = maxHealthIncreasePerKill;
        this.disableLosingMaxHealthInWorlds = disableLosingMaxHealthInWorlds;
        this.disableGainingMaxHealthInWorlds = disableGainingMaxHealthInWorlds;
        this.disableNaturalRegeneration = disableNaturalRegeneration;
        this.disableArtificialRegeneration = disableArtificialRegeneration;
    }

    public static MaxHealthConfiguration deserialize(ConfigurationSection section) {
        boolean cUseMaxHealth = section.getBoolean("UseMaxHealth", true);
        double cMaxHealthAtStart = cUseMaxHealth ? ConfigUtils.checkMinMax("MaxHealthAtStart", section.getDouble("MaxHealthAtStart", 20), 1, 2048) : 20;
        double cMaxHealth = cUseMaxHealth ? ConfigUtils.checkMinMax("MaxHealth", section.getDouble("MaxHealth", 20), 1, 2048) : 20;
        double cMinHealth = cUseMaxHealth ? ConfigUtils.checkMinMax("MinHealth", section.getDouble("MinHealth", 6), 1, 2048) : 20;
        double cMaxHealthAfterBan = cUseMaxHealth ? section.getDouble("MaxHealthAfterBan", 20) == -1 ? -1 : ConfigUtils.checkMinMax("MaxHealthAfterBan", section.getDouble("MaxHealthAfterBan", 20), -1, 2048) : 20;
        double cMaxHealthDecreasePerDeath = cUseMaxHealth ? ConfigUtils.checkMinMax("MaxHealthDecreasePerDeath", section.getDouble("MaxHealthDecreasePerDeath", 2), 1, 2048) : 0;
        boolean cMaxHealthIncreaseOnKill = section.getBoolean("MaxHealthIncreaseOnKill", true);
        Map<EntityType, Double> cMaxHealthIncreasePerKill = new HashMap<>();
        List<String> cDisableLosingMaxHealthInWorlds = ConfigUtils.getWorlds("DisableLosingMaxHealthInWorlds", section.getStringList("DisableLosingMaxHealthInWorlds"));
        List<String> cDisableGainingMaxHealthInWorlds = ConfigUtils.getWorlds("DisableGainingMaxHealthInWorlds", section.getStringList("DisableGainingMaxHealthInWorlds"));
        boolean cDisableNaturalRegeneration = section.getBoolean("DisableNaturalRegeneration", false);
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

        if (cMaxHealthAtStart == -10 || cMaxHealth == -10 || cMinHealth == -10 || cMaxHealthAfterBan == -10 || cMaxHealthDecreasePerDeath == -10)
            return null;

        return new MaxHealthConfiguration(
                cUseMaxHealth,
                cMaxHealthAtStart,
                cMaxHealth,
                cMinHealth,
                cMaxHealthAfterBan,
                cMaxHealthDecreasePerDeath,
                cMaxHealthIncreaseOnKill,
                cMaxHealthIncreasePerKill,
                cDisableLosingMaxHealthInWorlds,
                cDisableGainingMaxHealthInWorlds,
                cDisableNaturalRegeneration,
                cDisableArtificialRegeneration
        );
    }

    public boolean isDisableNaturalRegeneration() {
        return disableNaturalRegeneration;
    }

    public boolean isDisableArtificialRegeneration() {
        return disableArtificialRegeneration;
    }

    public boolean isUseMaxHealth() {
        return useMaxHealth;
    }

    public double getMaxHealthAtStart() {
        return maxHealthAtStart;
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
}
