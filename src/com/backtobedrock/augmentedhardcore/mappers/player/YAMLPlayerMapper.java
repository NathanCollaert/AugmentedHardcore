package com.backtobedrock.augmentedhardcore.mappers.player;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class YAMLPlayerMapper implements IPlayerMapper {
    private final AugmentedHardcore plugin;

    public YAMLPlayerMapper() {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        //create userdata folder if none existent
        File udFile = new File(this.plugin.getDataFolder() + "/userdata");
        if (udFile.mkdirs()) {
            this.plugin.getLogger().log(Level.INFO, "Creating {0}.", udFile.getAbsolutePath());
        }
    }

    private void insertPlayerData(PlayerData data) {
        FileConfiguration config = this.getConfig(data.getPlayer());
        data.serialize().forEach(config::set);
        this.saveConfig(data.getPlayer(), config);
    }

    @Override
    public void insertPlayerDataAsync(PlayerData data) {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            this.insertPlayerData(data);
        });
    }

    @Override
    public void insertPlayerDataSync(PlayerData data) {
        this.insertPlayerData(data);
    }

    @Override
    public CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> PlayerData.deserialize(this.getConfig(player), player));
    }

    @Override
    public PlayerData getByPlayerSync(OfflinePlayer player) {
        return PlayerData.deserialize(this.getConfig(player), player);
    }

    @Override
    public void updatePlayerData(PlayerData data) {
        if (this.plugin.isStopping()) {
            this.insertPlayerDataSync(data);
        } else {
            this.insertPlayerDataAsync(data);
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
