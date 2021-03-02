package com.backtobedrock.LiteDeathBan.mappers.server;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.ServerData;
import com.backtobedrock.LiteDeathBan.domain.callbacks.ServerDataCallback;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YAMLServerMapper implements IServerMapper {
    private final LiteDeathBan plugin;

    public YAMLServerMapper() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
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
        Bukkit.getScheduler().runTask(this.plugin, () -> {
            this.insertServerData(data);
        });
    }

    @Override
    public void getServerData(ServerDataCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.onQueryDoneServerData(ServerData.deserialize(getConfig())));
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
