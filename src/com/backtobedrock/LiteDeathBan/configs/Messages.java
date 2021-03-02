package com.backtobedrock.LiteDeathBan.configs;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Messages {

    private final LiteDeathBan plugin;
    private final FileConfiguration messages;

    public Messages(File messagesFile) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
}
