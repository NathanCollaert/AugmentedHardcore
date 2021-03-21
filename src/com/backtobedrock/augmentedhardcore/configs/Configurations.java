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
    private DataConfiguration dataConfiguration = null;
    private LivesAndLifePartsConfiguration livesAndLifePartsConfiguration = null;
    private DeathBanConfiguration deathBanConfiguration = null;
    private MaxHealthConfiguration maxHealthConfiguration = null;
    private CombatTagConfiguration combatTagConfiguration = null;
    private ReviveConfiguration reviveConfiguration = null;
    private GuisConfiguration guisConfiguration = null;

    public Configurations(File configFile) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.getDataConfiguration();
        this.getLivesAndLifePartsConfiguration();
        this.getBanTimesConfiguration();
        this.getMaxHealthConfiguration();
        this.getCombatTagConfiguration();
        this.getReviveConfiguration();
        this.getGuisConfiguration();
    }

    public DataConfiguration getDataConfiguration() {
        if (this.dataConfiguration == null) {
            this.dataConfiguration = DataConfiguration.deserialize(this.config);
            if (this.dataConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.dataConfiguration;
    }

    public LivesAndLifePartsConfiguration getLivesAndLifePartsConfiguration() {
        if (this.livesAndLifePartsConfiguration == null) {
            this.livesAndLifePartsConfiguration = LivesAndLifePartsConfiguration.deserialize(this.config);
            if (this.livesAndLifePartsConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.livesAndLifePartsConfiguration;
    }

    public DeathBanConfiguration getBanTimesConfiguration() {
        if (this.deathBanConfiguration == null) {
            this.deathBanConfiguration = DeathBanConfiguration.deserialize(this.config);
            if (this.deathBanConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.deathBanConfiguration;
    }

    public MaxHealthConfiguration getMaxHealthConfiguration() {
        if (this.maxHealthConfiguration == null) {
            this.maxHealthConfiguration = MaxHealthConfiguration.deserialize(this.config);
            if (this.maxHealthConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.maxHealthConfiguration;
    }

    public CombatTagConfiguration getCombatTagConfiguration() {
        if (this.combatTagConfiguration == null) {
            this.combatTagConfiguration = CombatTagConfiguration.deserialize(this.config);
            if (this.combatTagConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.combatTagConfiguration;
    }

    public ReviveConfiguration getReviveConfiguration() {
        if (this.reviveConfiguration == null) {
            this.reviveConfiguration = ReviveConfiguration.deserialize(this.config);
            if (this.reviveConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.reviveConfiguration;
    }

    public GuisConfiguration getGuisConfiguration() {
        if (this.guisConfiguration == null) {
            this.guisConfiguration = GuisConfiguration.deserialize(this.config);
            if (this.guisConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.guisConfiguration;
    }
}
