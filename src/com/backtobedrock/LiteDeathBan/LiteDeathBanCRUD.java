package com.backtobedrock.LiteDeathBan;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public final class LiteDeathBanCRUD {

    private final LiteDeathBan plugin;

    private File file = null;
    private FileConfiguration configuration = null;
    private OfflinePlayer player = null;
    private LocalDateTime lastRevive;
    private String lastBan;
    private int lifeParts;
    private int lives;
    private int totalDeathBans;
    private long lastPartPlaytime;

    public LiteDeathBanCRUD(OfflinePlayer player, LiteDeathBan plugin) {
        this.plugin = plugin;
        this.player = player;
        this.lives = this.getConfig().getInt("lives", this.plugin.getLDBConfig().getLivesAtStart());
        this.totalDeathBans = this.getConfig().getInt("totalDeathBans", 0);
        this.lastBan = this.getConfig().getString("lastBan", "never");
        this.lastRevive = LocalDateTime.parse(this.getConfig().getString("lastRevive", this.plugin.getLDBConfig().isReviveOptionOnFirstJoin() ? LocalDateTime.MIN.toString() : LocalDateTime.now().toString()));
        this.lifeParts = this.getConfig().getInt("lifeParts", 0);
        this.lastPartPlaytime = this.getConfig().getLong("lastPartPlaytime", 0);
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
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save to {0}", file.getName());
        }
    }

    private void setNewStart() {
        FileConfiguration conf = this.getConfig();
        conf.set("uuid", player.getUniqueId().toString());
        conf.set("playername", player.getName());
        conf.set("lives", this.plugin.getLDBConfig().getLivesAtStart());
        conf.set("totalDeathBans", 0);
        conf.set("lastBan", "never");
        conf.set("lastRevive", this.plugin.getLDBConfig().isReviveOptionOnFirstJoin() ? LocalDateTime.MIN.toString() : LocalDateTime.now().toString());
        conf.set("lifeParts", 0);
        conf.set("lastPartPlaytime", this.plugin.getLDBConfig().isCountPlaytimeFromStart() ? 0 : this.player.getPlayer().getStatistic(Statistic.PLAY_ONE_MINUTE));
        this.saveConfig();
    }

    public void setLives(int amount, boolean save) {
        FileConfiguration conf = this.getConfig();
        conf.set("playername", player.getName());
        conf.set("lives", amount);
        this.lives = amount;
        if (this.plugin.getLDBConfig().isShowLivesInTabMenu() && this.player.isOnline()) {
            this.player.getPlayer().setPlayerListFooter(this.plugin.getMessages().getOnLivesLeftInTabMenu(this.player.getName(), this.lives, this.plugin.getLDBConfig().getMaxLives()));
        }
        if (save) {
            this.saveConfig();
        }
    }

    public int getLives() {
        return this.lives;
    }

    public void setTotalDeathBans(int amount, boolean save) {
        FileConfiguration conf = this.getConfig();
        conf.set("playername", player.getName());
        conf.set("totalDeathBans", amount);
        this.totalDeathBans = amount;
        if (save) {
            this.saveConfig();
        }
    }

    public int getTotalDeathBans() {
        return this.totalDeathBans;
    }

    public void setLastBanDate(LocalDateTime date, boolean save) {
        FileConfiguration conf = this.getConfig();
        conf.set("playername", player.getName());
        conf.set("lastBanDate", this.plugin.getLDBConfig().getSaveDateFormat().format(date));
        this.lastBan = this.plugin.getLDBConfig().getSaveDateFormat().format(date);
        if (save) {
            this.saveConfig();
        }
    }

    public String getLastBanDate() {
        return this.lastBan;
    }

    public void setLastRevive(LocalDateTime date, boolean save) {
        FileConfiguration conf = this.getConfig();
        conf.set("playername", player.getName());
        conf.set("lastRevive", date.toString());
        this.lastRevive = date;
        if (save) {
            this.saveConfig();
        }
    }

    public LocalDateTime getLastRevive() {
        return this.lastRevive;
    }

    public void setLifeParts(int lifeParts, boolean save) {
        FileConfiguration conf = this.getConfig();
        conf.set("playername", player.getName());
        int amountOfPartsPerLife = this.plugin.getLDBConfig().getAmountOfPartsPerLife();
        int maxLives = this.plugin.getLDBConfig().getMaxLives();
        if (this.lives < maxLives) {
            if (lifeParts == 0) {
                this.player.getPlayer().spigot().sendMessage(new ComponentBuilder(this.plugin.getMessages().getOnPartsLostCauseDeath(this.player.getName(), amountOfPartsPerLife)).create());
            } else if (lifeParts < amountOfPartsPerLife) {
                int oldLifeParts = this.lifeParts;
                conf.set("lifeParts", lifeParts);
                this.lifeParts = lifeParts;
                String broadcastMessage = this.plugin.getMessages().getOnExtraPartBroadcast(this.player.getName(), lifeParts, lifeParts - oldLifeParts, amountOfPartsPerLife);
                String playerMessage = this.plugin.getMessages().getOnExtraPart(this.player.getName(), lifeParts, lifeParts - oldLifeParts, amountOfPartsPerLife);
                if (!broadcastMessage.trim().isEmpty()) {
                    Bukkit.broadcastMessage(broadcastMessage);
                }
                if (!playerMessage.trim().isEmpty()) {
                    this.player.getPlayer().spigot().sendMessage(new ComponentBuilder(playerMessage).create());
                }
            } else {
                int extraLives = lifeParts / amountOfPartsPerLife;
                int oldLives = this.lives;
                this.setLives(this.lives + extraLives >= maxLives ? maxLives : this.lives + extraLives, false);
                int lifePartsLeft = this.lives >= maxLives ? 0 : lifeParts - (amountOfPartsPerLife * extraLives);
                conf.set("lifeParts", lifePartsLeft);
                this.lifeParts = lifePartsLeft;
                String broadcastMessage = this.plugin.getMessages().getOnExtraLifeBroadcast(this.player.getName(), oldLives + extraLives >= maxLives ? maxLives - oldLives : extraLives, this.lives, maxLives, lifePartsLeft, amountOfPartsPerLife);
                String playerMessage = this.plugin.getMessages().getOnExtraLife(this.player.getName(), oldLives + extraLives >= maxLives ? maxLives - oldLives : extraLives, this.lives, maxLives, lifePartsLeft, amountOfPartsPerLife);
                if (this.lives == maxLives) {
                    broadcastMessage = this.plugin.getMessages().getOnMaxLivesBroadcast(this.player.getName(), oldLives + extraLives >= maxLives ? maxLives - oldLives : extraLives, this.lives, maxLives, lifePartsLeft, amountOfPartsPerLife);
                    playerMessage = this.plugin.getMessages().getOnMaxLives(this.player.getName(), oldLives + extraLives >= maxLives ? maxLives - oldLives : extraLives, this.lives, maxLives, lifePartsLeft, amountOfPartsPerLife);
                    if (!broadcastMessage.trim().isEmpty()) {
                        Bukkit.broadcastMessage(broadcastMessage);
                    }
                    if (!playerMessage.trim().isEmpty()) {
                        this.player.getPlayer().spigot().sendMessage(new ComponentBuilder(playerMessage).create());
                    }
                } else {
                    if (!broadcastMessage.trim().isEmpty()) {
                        Bukkit.broadcastMessage(broadcastMessage);
                    }
                    if (!playerMessage.trim().isEmpty()) {
                        this.player.getPlayer().spigot().sendMessage(new ComponentBuilder(playerMessage).create());
                    }
                }
            }
        }
        if (save) {
            this.saveConfig();
        }
    }

    public int getLifeParts() {
        return lifeParts;
    }

    public void setLastPartPlaytime(long lastPartPlaytime, boolean save) {
        FileConfiguration conf = this.getConfig();
        conf.set("playername", player.getName());
        conf.set("lastPartPlaytime", lastPartPlaytime);
        this.lastPartPlaytime = lastPartPlaytime;
        if (save) {
            this.saveConfig();
        }
    }

    public long getLastPartPlaytime() {
        return lastPartPlaytime;
    }

    private File getFile() {
        if (file == null) {
            this.file = new File(this.plugin.getDataFolder() + "/userdata/" + player.getUniqueId().toString() + ".yml");
            if (!this.file.exists()) {
                try {
                    if (this.file.createNewFile()) {
                        this.setNewStart();
                        Bukkit.getLogger().log(Level.INFO, "[LiteDeathBan] File for player {0} has been created", player.getName());
                    }
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.SEVERE, "[LiteDeathBan] Cannot create data for {0}", player.getName());
                }
            }
            return file;
        }
        return file;
    }

    public void reloadConfig() {
        YamlConfiguration.loadConfiguration(file);
    }

    public static boolean doesPlayerDataExists(String id, LiteDeathBan plugin) {
        File file = new File(plugin.getDataFolder() + id + ".yml");
        return file.exists();
    }
}
