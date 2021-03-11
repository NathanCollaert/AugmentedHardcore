package com.backtobedrock.LiteDeathBan.configs;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Configurations {
    private final LiteDeathBan plugin;
    private final FileConfiguration config;

    //configurations
    private DataConfiguration dataConfiguration = null;
    private LivesAndLifePartsConfiguration livesAndLifePartsConfiguration = null;
    private BanTimesConfiguration banTimesConfiguration = null;
    private CombatTagConfiguration combatTagConfiguration = null;
    private ReviveConfiguration reviveConfiguration = null;
    private GuisConfiguration guisConfiguration = null;

    public Configurations(File configFile) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.getDataConfiguration();
        this.getLivesAndLifePartsConfiguration();
        this.getBanTimesConfiguration();
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

    public BanTimesConfiguration getBanTimesConfiguration() {
        if (this.banTimesConfiguration == null) {
            this.banTimesConfiguration = BanTimesConfiguration.deserialize(this.config);
            if (this.banTimesConfiguration == null) {
                this.plugin.getServer().getPluginManager().disablePlugin(this.plugin);
            }
        }
        return this.banTimesConfiguration;
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
