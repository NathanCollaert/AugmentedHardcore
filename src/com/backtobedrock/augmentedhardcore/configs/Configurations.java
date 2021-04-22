package com.backtobedrock.augmentedhardcore.configs;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Configurations {
    private final AugmentedHardcore plugin;
    private final FileConfiguration config;

    //configurations
    private ConfigurationData dataConfiguration;
    private ConfigurationLivesAndLifeParts livesAndLifePartsConfiguration;
    private ConfigurationDeathBan deathBanConfiguration;
    private ConfigurationMaxHealth maxHealthConfiguration;
    private ConfigurationCombatTag combatTagConfiguration;
    private ConfigurationRevive reviveConfiguration;
    private ConfigurationMiscellaneous miscellaneousConfiguration;
    private ConfigurationGuis guisConfiguration;

    public Configurations(File configFile) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.getDataConfiguration();
        this.getLivesAndLifePartsConfiguration();
        this.getDeathBanConfiguration();
        this.getMaxHealthConfiguration();
        this.getCombatTagConfiguration();
        this.getReviveConfiguration();
        this.getMiscellaneousConfiguration();
        this.getGuisConfiguration();
    }

    public ConfigurationData getDataConfiguration() {
        if (this.dataConfiguration == null) {
            this.dataConfiguration = ConfigurationData.deserialize(this.config);
            if (this.dataConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.dataConfiguration;
    }

    public ConfigurationLivesAndLifeParts getLivesAndLifePartsConfiguration() {
        if (this.livesAndLifePartsConfiguration == null) {
            this.livesAndLifePartsConfiguration = ConfigurationLivesAndLifeParts.deserialize(this.config);
            if (this.livesAndLifePartsConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.livesAndLifePartsConfiguration;
    }

    public ConfigurationDeathBan getDeathBanConfiguration() {
        if (this.deathBanConfiguration == null) {
            this.deathBanConfiguration = ConfigurationDeathBan.deserialize(this.config);
        }
        return this.deathBanConfiguration;
    }

    public ConfigurationMaxHealth getMaxHealthConfiguration() {
        if (this.maxHealthConfiguration == null) {
            this.maxHealthConfiguration = ConfigurationMaxHealth.deserialize(this.config);
            if (this.maxHealthConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.maxHealthConfiguration;
    }

    public ConfigurationCombatTag getCombatTagConfiguration() {
        if (this.combatTagConfiguration == null) {
            this.combatTagConfiguration = ConfigurationCombatTag.deserialize(this.config);
            if (this.combatTagConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.combatTagConfiguration;
    }

    public ConfigurationRevive getReviveConfiguration() {
        if (this.reviveConfiguration == null) {
            this.reviveConfiguration = ConfigurationRevive.deserialize(this.config, this.getLivesAndLifePartsConfiguration());
            if (this.reviveConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.reviveConfiguration;
    }

    public ConfigurationMiscellaneous getMiscellaneousConfiguration() {
        if (this.miscellaneousConfiguration == null) {
            this.miscellaneousConfiguration = ConfigurationMiscellaneous.deserialize(this.config);
        }
        return this.miscellaneousConfiguration;
    }

    public ConfigurationGuis getGuisConfiguration() {
        if (this.guisConfiguration == null) {
            this.guisConfiguration = ConfigurationGuis.deserialize(this.config);
            if (this.guisConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.guisConfiguration;
    }
}
