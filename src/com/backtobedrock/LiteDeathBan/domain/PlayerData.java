package com.backtobedrock.LiteDeathBan.domain;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.runnables.Playtime;
import com.backtobedrock.LiteDeathBan.utils.BanUtils;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerData {
    private final LiteDeathBan plugin;
    //serializable
    private final Map<Integer, Ban> bans;
    private final String lastKnownName;
    private Playtime playtimeRunnable = null;
    private int lives;
    private int lifeParts;
    private String ip;
    private int timeTillNextLifePart;

    public PlayerData(OfflinePlayer player) {
        this(player, new LinkedHashMap<>(), JavaPlugin.getPlugin(LiteDeathBan.class).getConfiguration().getLivesAndLifePartsConfiguration().getLivesAtStart(), JavaPlugin.getPlugin(LiteDeathBan.class).getConfiguration().getLivesAndLifePartsConfiguration().getLifePartsAtStart(), JavaPlugin.getPlugin(LiteDeathBan.class).getConfiguration().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());
    }

    public PlayerData(OfflinePlayer player, Map<Integer, Ban> bans, int lives, int lifeParts, int timeTillNextLifePart) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.bans = bans;
        this.setLives(player, lives);
        this.setLifeParts(player, lifeParts);
        this.lastKnownName = player.getName();
        this.timeTillNextLifePart = timeTillNextLifePart;
        if (player.getPlayer() != null && player.getPlayer().getAddress() != null) {
            InetSocketAddress address = player.getPlayer().getAddress();
            this.ip = address.getHostName() == null ? address.getHostString() : address.getHostName();
        }
    }

    public static PlayerData deserialize(ConfigurationSection section, OfflinePlayer player) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);

        Map<Integer, Ban> cBans = new LinkedHashMap<>();
        int cLives = section.getInt("Lives", plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLivesAtStart());
        int cLifeParts = section.getInt("LifeParts", plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLifePartsAtStart());
        int cTimeTillNextLifePart = section.getInt("TimeTillNextLifePart", plugin.getConfiguration().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());

        //get all bans
        ConfigurationSection bansSection = section.getConfigurationSection("Bans");
        if (bansSection != null) {
            int i = 0;
            for (String e : bansSection.getKeys(false)) {
                ConfigurationSection banSection = bansSection.getConfigurationSection(e);
                if (banSection != null) {
                    Ban ban = Ban.Deserialize(banSection, player.getUniqueId());
                    if (ban != null) {
                        cBans.put(i, ban);
                        i++;
                    }
                }
            }
        }

        return new PlayerData(player, cBans, cLives, cLifeParts, cTimeTillNextLifePart);
    }

    public String getIp() {
        return ip;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(OfflinePlayer player, int lives) {
        this.lives = Math.max(Math.min(lives, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getMaxLives()), 0);
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public int getLifeParts() {
        return lifeParts;
    }

    public void setLifeParts(OfflinePlayer player, int lifeParts) {
        int lifePartsPerLife = this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLifePartsPerLife();
        int lives = Math.min(lifeParts / lifePartsPerLife, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getMaxLives() - this.getLives());
        if (lives > 0)
            this.increaseLives(player, lives);
        this.lifeParts = Math.max(0, lifeParts - (lives * lifePartsPerLife));
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public Map<Integer, Ban> getBans() {
        return bans;
    }

    private void decreaseLives(OfflinePlayer player, int amount) {
        this.setLives(player, this.getLives() - amount);
    }

    public void increaseLives(OfflinePlayer player, int amount) {
        this.setLives(player, this.getLives() + amount);
    }

    private void decreaseLifeParts(OfflinePlayer player, int amount) {
        this.setLifeParts(player, this.getLifeParts() - amount);
    }

    public void increaseLifeParts(OfflinePlayer player, int amount) {
        this.setLifeParts(player, this.getLifeParts() + amount);
    }

    public void onDeath(Player player, Ban ban) {
        //lose lives
        this.loseLives(player, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLivesLostPerDeath());

        //lose life parts
        if (this.lives != 0) {
            this.loseLifeParts(player, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLifePartsLostPerDeath());
        } else {
            this.loseLifeParts(player, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLifePartsLostPerDeathBan());
        }

        //ban if 0 lives
        if (this.lives == 0)
            this.ban(player, ban);
    }

    private void loseLives(Player player, int amount) {
        //check if permission to bypass
        if (player.hasPermission("litedeathban.bypass.loselives"))
            return;

        //check if in disabled world
        if (this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getDisableLosingLivesInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        //lose lives
        this.decreaseLives(player, amount);
    }

    private void loseLifeParts(Player player, int amount) {
        //check if permission to bypass
        if (player.hasPermission("litedeathban.bypass.loselifeparts"))
            return;

        //check if in disabled world
        if (this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getDisableLosingLifePartsInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        //lose life parts
        this.decreaseLifeParts(player, amount);
    }

    private void ban(Player player, Ban ban) {
        //Check if permission to bypass
        if (player.hasPermission("litedeathban.bypass.ban"))
            return;

        //check if in disabled world
        if (this.plugin.getConfiguration().getBanTimesConfiguration().getDisableBanInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        //ban player
        if (ban.getBanTime() > 0) {
            ban.deathBan(this, player);
            //add ban to player and server data
            this.addBan(player, ban);
            this.plugin.getServerRepository().getServerData(data -> {
                data.addBan(player, ban);
            });
        }
    }

    public void addBan(OfflinePlayer player, Ban ban) {
        this.bans.put(this.bans.size(), ban);
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public void onRespawn(Player player) {
        //check if player has 0 lives
        if (this.lives > 0)
            return;

        //give player lives
        this.increaseLives(player, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLivesAfterBan());

        //give player life parts
        this.increaseLifeParts(player, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLifePartsAfterBan());
    }

    public void onEntityKill(Player player, EntityType type) {
        //check if permission to gain life parts for kill
        if (player.hasPermission("litedeathban.bypass.gainlifeparts.kill"))
            return;

        //gain life parts
        this.gainLifeParts(player, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getLifePartsPerKill().getOrDefault(type, 0));
    }

    private void gainLifeParts(Player player, int amount) {
        //check if in disabled world
        if (this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getDisableGainingLifePartsInWorlds().contains(player.getWorld().getName().toLowerCase()))
            return;

        this.increaseLifeParts(player, amount);
    }

    public boolean isBanned(OfflinePlayer player) {
        BanList.Type type = this.getIp() == null ? BanList.Type.NAME : this.plugin.getConfiguration().getBanTimesConfiguration().getBanType();
        BanList banList = Bukkit.getBanList(type);
        return banList.isBanned(BanUtils.getBanParameter(this, player, type));
    }

    public void onJoin(Player player) {
        if (!this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().isGetLifePartsByPlaytime())
            return;

        if (this.timeTillNextLifePart > plugin.getConfiguration().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart())
            this.setTimeTillNextLifePart(player, plugin.getConfiguration().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart());

        if (this.playtimeRunnable != null)
            return;

        this.playtimeRunnable = new Playtime(player);
        this.playtimeRunnable.start();
    }

    public void decreaseTimeTillNextLifePart(Player player, int amount) {
        //check if permission to gain life parts for playtime
        if (player.hasPermission("litedeathban.bypass.gainlifeparts.playtime"))
            return;

        int decreased = this.getTimeTillNextLifePart() - amount;
        if (decreased <= 0) {
            this.gainLifeParts(player, 1);
            this.setTimeTillNextLifePart(player, this.plugin.getConfiguration().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart() - Math.abs(decreased));
        } else {
            this.setTimeTillNextLifePart(player, decreased);
        }
    }

    public void onLeave() {
        if (this.playtimeRunnable == null)
            return;

        this.playtimeRunnable.stop();
        this.playtimeRunnable = null;
    }

    public int getTimeTillNextLifePart() {
        return timeTillNextLifePart;
    }

    public void setTimeTillNextLifePart(OfflinePlayer player, int timeTillNextLifePart) {
        this.timeTillNextLifePart = timeTillNextLifePart;
        this.plugin.getPlayerRepository().updatePlayerData(player, this);
    }

    public void onReload(Player player) {
        this.onJoin(player);
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();

        Map<String, Object> cBans = new HashMap<>();
        this.bans.forEach((key, value) -> cBans.put(key.toString(), value.serialize()));

        map.put("Bans", cBans);
        map.put("TimeTillNextLifePart", this.timeTillNextLifePart);
        map.put("LifeParts", this.lifeParts);
        map.put("Lives", this.lives);
        map.put("LastKnownPlayername", this.lastKnownName);
        map.put("LastKnownIP", this.ip);

        return map;
    }
}
