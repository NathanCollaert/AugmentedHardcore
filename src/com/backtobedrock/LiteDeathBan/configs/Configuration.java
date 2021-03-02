package com.backtobedrock.LiteDeathBan.configs;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.BanTimesConfiguration;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.DataConfiguration;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.LivesAndLifePartsConfiguration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Configuration {
    private final LiteDeathBan plugin;
    private final FileConfiguration config;

    //configurations
    private BanTimesConfiguration banTimesConfiguration = null;
    private LivesAndLifePartsConfiguration livesAndLifePartsConfiguration = null;
    private DataConfiguration dataConfiguration = null;

    public Configuration(File configFile) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.config = YamlConfiguration.loadConfiguration(configFile);
        this.getDataConfiguration();
        this.getBanTimesConfiguration();
        this.getLivesAndLifePartsConfiguration();
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
}
