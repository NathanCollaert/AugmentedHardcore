package com.backtobedrock.LiteDeathBan.mappers.player;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.callbacks.IPlayerDataCallback;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class YAMLPlayerMapper implements IPlayerMapper {
    private final LiteDeathBan plugin;

    public YAMLPlayerMapper() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    private void insertPlayerData(OfflinePlayer player, PlayerData data) {
        FileConfiguration config = this.getConfig(player);
        data.serialize().forEach(config::set);
        this.saveConfig(player, config);
    }

    @Override
    public void insertPlayerDataAsync(OfflinePlayer player, PlayerData data) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.insertPlayerData(player, data);
        });
    }

    @Override
    public void insertPlayerDataSync(OfflinePlayer player, PlayerData data) {
        this.insertPlayerData(player, data);
    }

    @Override
    public void getByPlayer(OfflinePlayer player, IPlayerDataCallback callback) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> callback.onQueryDonePlayerData(PlayerData.deserialize(this.getConfig(player), player)));
    }

    @Override
    public PlayerData getByPlayerSync(OfflinePlayer player) {
        return PlayerData.deserialize(this.getConfig(player), player);
    }

    @Override
    public void updatePlayerData(OfflinePlayer player, PlayerData data) {
        if (this.plugin.isStopping()) {
            this.insertPlayerDataSync(player, data);
        } else {
            this.insertPlayerDataAsync(player, data);
        }
    }

    @Override
    public void deletePlayerData(OfflinePlayer player) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            File file = this.getFile(player);
            if (file.exists()) {
                if (file.delete()) {
                    this.plugin.getLogger().log(Level.INFO, "File for player {0} has been deleted at {1}.", new Object[]{player.getName(), file.getPath()});
                }
            }
        });
    }

    private void saveConfig(OfflinePlayer player, FileConfiguration config) {
        try {
            config.save(this.getFile(player));
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save to {0}", this.getFile(player).getName());
        }
    }

    private FileConfiguration getConfig(OfflinePlayer player) {
        return YamlConfiguration.loadConfiguration(this.getFile(player));
    }

    private File getFile(OfflinePlayer player) {
        File file = new File(this.plugin.getDataFolder() + "/userdata/" + player.getUniqueId().toString() + ".yml");
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    this.plugin.getLogger().log(Level.INFO, "File for player {0} has been created at {1}.", new Object[]{player.getName(), file.getPath()});
                }
            } catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Cannot create data for {0}.", player.getName());
            }
        }
        return file;
    }
}
