package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class YAMLServerMapper implements IServerMapper {
    private final AugmentedHardcore plugin;

    public YAMLServerMapper() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
    }

    private void insertServerData(ServerData data) {
        FileConfiguration config = this.getConfig();
        data.serialize().forEach(config::set);
        this.saveConfig(config);
    }

    @Override
    public void insertServerDataAsync(ServerData data) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.insertServerData(data);
        });
    }

    @Override
    public void insertServerDataSync(ServerData data) {
        this.insertServerData(data);
    }

    @Override
    public CompletableFuture<ServerData> getServerData() {
        return CompletableFuture.supplyAsync(() -> ServerData.deserialize(getConfig()));
    }

    @Override
    public void updateServerData(ServerData data) {
        if (this.plugin.isStopping()) {
            this.insertServerDataSync(data);
        } else {
            this.insertServerDataAsync(data);
        }
    }

    @Override
    public void deleteServerData() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            File file = this.getFile();
            if (file.exists()) {
                if (file.delete()) {
                    this.plugin.getLogger().log(Level.INFO, "File for server data has been deleted at {1}.", new Object[]{file.getPath()});
                }
            }
        });
    }

    private void saveConfig(FileConfiguration config) {
        try {
            config.save(this.getFile());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save to {0}", this.getFile().getName());
        }
    }

    private FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(this.getFile());
    }

    private File getFile() {
        File file = new File(this.plugin.getDataFolder() + "/server.yml");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    this.plugin.getLogger().log(Level.INFO, "File for server data has been created at {0}.", new Object[]{file.getPath()});
                }
            } catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Cannot create server data file.");
            }
        }
        return file;
    }
}
