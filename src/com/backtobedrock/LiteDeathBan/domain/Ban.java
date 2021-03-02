package com.backtobedrock.LiteDeathBan.domain;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses.Location;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCause;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCauseType;
import com.backtobedrock.LiteDeathBan.utils.BanUtils;
import com.backtobedrock.LiteDeathBan.utils.ConfigUtils;
import com.backtobedrock.LiteDeathBan.utils.MessageUtils;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ban {
    private final LiteDeathBan plugin;
    private final BanConfiguration banConfiguration;

    //serializable
    private final LocalDateTime expirationDate;
    private final DamageCause damageCause;
    private final Killer killer;
    private final Killer inCombatWith;
    private final Location location;
    private final String deathMessage;
    private final int banTime;
    private final DamageCauseType damageCauseType;

    public Ban(LocalDateTime expirationDate, DamageCause damageCause, Killer killer, Killer inCombatWith, Location location, String deathMessage, int banTime, DamageCauseType damageCauseType) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.expirationDate = expirationDate;
        this.damageCause = damageCause;
        this.killer = killer;
        this.inCombatWith = inCombatWith;
        this.banConfiguration = this.plugin.getConfiguration().getBanTimesConfiguration().getBanTimes().get(damageCause.name());
        this.location = location;
        this.deathMessage = deathMessage;
        this.banTime = banTime;
        this.damageCauseType = damageCauseType;
    }

    public static Ban Deserialize(ConfigurationSection section, UUID uuid) {
        LocalDateTime cExpirationDate;
        DamageCause cDamageCause = ConfigUtils.getDamageCause(section.getString("DamageCause", "VOID"), DamageCause.VOID);
        Killer cKiller = null;
        Killer cInCombatWith = null;
        Location cLocation = new Location("world", 0, 0, 0);
        String cDeathMessage = section.getString("DeathMessage");
        int cBanTime = section.getInt("BanTime", 0);
        DamageCauseType cDamageCauseType = ConfigUtils.getDamageCauseType(section.getString("DamageCauseType", "ENVIRONMENT"), DamageCauseType.ENVIRONMENT);

        String sExpirationDate = section.getString("ExpirationDate");
        //if not found, get date from server bans
        if (sExpirationDate == null) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && Bukkit.getBanList(BanList.Type.NAME).isBanned(player.getName())) {
                BanEntry banEntry = Bukkit.getBanList(BanList.Type.NAME).getBanEntry(player.getName());
                if (banEntry != null) {
                    Date banDate = banEntry.getExpiration();
                    if (banDate != null) {
                        cExpirationDate = new Timestamp(banDate.getTime()).toLocalDateTime();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            cExpirationDate = LocalDateTime.parse(sExpirationDate);
        }

        ConfigurationSection locationSection = section.getConfigurationSection("Location");
        if (locationSection != null) {
            String world = locationSection.getString("World", "world");
            double x = locationSection.getDouble("x", 0), y = locationSection.getDouble("y", 0), z = locationSection.getDouble("z", 0);
            cLocation = new Location(world, x, y, z);
        }

        ConfigurationSection killerSection = section.getConfigurationSection("Killer");
        if (killerSection != null) {
            cKiller = Killer.Deserialize(killerSection);
        }
        ConfigurationSection inCombatWithSection = section.getConfigurationSection("InCombatWith");
        if (inCombatWithSection != null) {
            cInCombatWith = Killer.Deserialize(killerSection);
        }

        return new Ban(cExpirationDate, cDamageCause, cKiller, cInCombatWith, cLocation, cDeathMessage, cBanTime, cDamageCauseType);
    }

    public String getBanMessage() {
        //calculate how long till ban expiration in ticks
        int tickTillExpiration = (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), this.expirationDate) * 20;
        //create ban message
        String expirationDateMessage = MessageUtils.getTimeFromTicks(tickTillExpiration, false, true);
        String banMessage;
        //TODO: get messages from config
        if (this.killer == null && this.inCombatWith == null) {
            banMessage = String.format("You've died due to %s.", this.banConfiguration.getDisplayName());
        } else if (this.killer != null && this.inCombatWith == null) {
            banMessage = String.format("You've died to %s.", this.killer.getDeathMessage());
        } else if (this.killer == null) {
            banMessage = String.format("You've died due to %s whilst trying to escape %s.", this.banConfiguration.getDisplayName(), this.inCombatWith.getDeathMessage());
        } else {
            banMessage = String.format("You've died to %s whilst trying to escape %s.", this.killer.getDeathMessage(), this.inCombatWith.getDeathMessage());
        }
        return banMessage;
    }

    public void deathBan(PlayerData data, OfflinePlayer player) {
        BanList.Type type = data.getIp() == null ? BanList.Type.NAME : this.plugin.getConfiguration().getBanTimesConfiguration().getBanType();
        BanList banList = Bukkit.getBanList(type);
        //ban player
        banList.addBan(BanUtils.getBanParameter(data, player, type), "", Timestamp.valueOf(this.getExpirationDate()), "LiteDeathBan");
        //kick player off server if online
        if (player.isOnline())
            Bukkit.getScheduler().runTask(this.plugin, () -> {
                ((Player) player).kickPlayer(this.getBanMessage());
            });
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public int getBanTime() {
        return banTime;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> locationMap = new HashMap<>();

        locationMap.put("World", this.location.getWorld());
        locationMap.put("x", this.location.getX());
        locationMap.put("y", this.location.getY());
        locationMap.put("z", this.location.getZ());

        map.put("DamageCause", this.damageCause.name());
        map.put("ExpirationDate", this.expirationDate.toString());
        map.put("Killer", this.killer == null ? null : this.killer.serialize());
        map.put("InCombatWith", this.inCombatWith == null ? null : this.inCombatWith.serialize());
        map.put("Location", locationMap);
        map.put("DeathMessage", this.deathMessage);
        map.put("BanTime", this.banTime);
        map.put("DamageCauseType", this.damageCauseType.name());

        return map;
    }
}
