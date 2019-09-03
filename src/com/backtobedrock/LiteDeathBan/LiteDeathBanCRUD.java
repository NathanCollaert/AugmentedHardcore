package com.backtobedrock.LiteDeathBan;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Nathan_C
 */
public class LiteDeathBanCRUD {

    private final Logger log = Bukkit.getLogger();

    File file = null;
    FileConfiguration configuration = null;
    OfflinePlayer player = null;

    public LiteDeathBanCRUD(OfflinePlayer player) {
        this.player = player;
    }

    public FileConfiguration getConfig() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(getFile());
            return configuration;
        }
        return configuration;
    }

    public void saveConfig() {
        try {
            configuration.save(this.getFile());
        } catch (IOException e) {
            this.log.log(Level.SEVERE, "Cannot save to {0}", file.getName());
        }
        this.reloadConfig();
    }

    public void setNewStart() {
        FileConfiguration conf = this.getConfig();
        conf.set("uuid", player.getUniqueId().toString());
        conf.set("playername", player.getName());
        conf.set("lives", 1);
        saveConfig();
    }

    public void setLives(int amount) {
        FileConfiguration conf = this.getConfig();
        conf.set("uuid", player.getUniqueId().toString());
        conf.set("playername", player.getName());
        conf.set("lives", amount);
    }

    private File getFile() {
        if (file == null) {
            this.file = new File(System.getProperty("user.dir") + "/plugins/LiteDeathBan/userdata/" + player.getUniqueId().toString() + ".yml");
            if (!this.file.exists()) {
                try {
                    if (this.file.createNewFile()) {
                        this.log.log(Level.INFO, "[LiteDeathBan] File for player {0} has been created", player.getName());
                    }
                } catch (IOException e) {
                    this.log.log(Level.SEVERE, "[LiteDeathBan] Cannot create data for {0}", player.getName());
                }
            }
            return file;
        }
        return file;
    }

    public static boolean doesPlayerDataExists(String id) {
        File file = new File(System.getProperty("user.dir") + "/plugins/LiteDeathBan/userdata/" + id + ".yml");
        return file.exists();
    }

    public void reloadConfig() {
        YamlConfiguration.loadConfiguration(file);
    }
}
