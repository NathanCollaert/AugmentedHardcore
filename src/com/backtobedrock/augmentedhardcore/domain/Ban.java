package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Ban {
    private final AugmentedHardcore plugin;

    //serializable
    private final LocalDateTime startDate;
    private final int banTime;
    private final LocalDateTime expirationDate;
    private final DamageCause damageCause;
    private final DamageCauseType damageCauseType;
    private final Killer killer;
    private final Killer inCombatWith;
    private final Location location;
    private final String deathMessage;

    public Ban(LocalDateTime startDate, LocalDateTime expirationDate, DamageCause damageCause, Killer killer, Killer inCombatWith, Location location, String deathMessage, int banTime, DamageCauseType damageCauseType) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.damageCause = damageCause;
        this.killer = killer;
        this.inCombatWith = inCombatWith;
        this.location = location;
        this.deathMessage = deathMessage;
        this.banTime = banTime;
        this.damageCauseType = damageCauseType;
    }

    public static Ban Deserialize(ConfigurationSection section, UUID uuid) {
        LocalDateTime cStartDate = LocalDateTime.parse(section.getString("StartDate", LocalDateTime.MIN.toString()));
        LocalDateTime cExpirationDate;
        DamageCause cDamageCause = ConfigUtils.getDamageCause(section.getString("DamageCause", DamageCause.VOID.name()), DamageCause.VOID);
        Killer cKiller = null;
        Killer cInCombatWith = null;
        Location cLocation = new Location("world", 0, 0, 0);
        String cDeathMessage = section.getString("DeathMessage");
        int cBanTime = section.getInt("BanTime", 0);
        DamageCauseType cDamageCauseType = ConfigUtils.getDamageCauseType(section.getString("DamageCauseType", DamageCauseType.ENVIRONMENT.name()), DamageCauseType.ENVIRONMENT);

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

        return new Ban(cStartDate, cExpirationDate, cDamageCause, cKiller, cInCombatWith, cLocation, cDeathMessage, cBanTime, cDamageCauseType);
    }

    public String getBanMessage() {
        String banMessage;
        if (this.damageCause == DamageCause.REVIVE) {
            if (this.inCombatWith == null) {
                //Revive
                banMessage = this.placeholdersReplacements(this.plugin.getMessages().getReviveBanMessage());
            } else {
                //Revive while in combat
                banMessage = this.placeholdersReplacements(this.plugin.getMessages().getReviveWhileInCombatBanMessage());
            }
        } else if (this.damageCause == DamageCause.COMBAT_LOG || this.damageCause == DamageCause.PLAYER_COMBAT_LOG) {
            //Combat log
            banMessage = this.placeholdersReplacements(this.plugin.getMessages().getCombatLogBanMessage());
        } else if (this.killer == null && this.inCombatWith == null) {
            //Environment
            banMessage = this.placeholdersReplacements(this.plugin.getMessages().getEnvironmentBanMessage());
        } else if (this.killer != null && this.inCombatWith == null) {
            //Entity
            banMessage = this.placeholdersReplacements(this.plugin.getMessages().getEntityBanMessage());
        } else if (this.killer == null) {
            //Environment while in combat
            banMessage = this.placeholdersReplacements(this.plugin.getMessages().getEntityWhileInCombatBanMessage());
        } else {
            //Entity while in combat
            banMessage = this.placeholdersReplacements(this.plugin.getMessages().getEntityWhileInCombatBanMessage());
        }
        return banMessage;
    }

    public void deathBan(PlayerData data, OfflinePlayer player) {
        BanList.Type type = data.getIp() == null ? BanList.Type.NAME : this.plugin.getConfigurations().getBanTimesConfiguration().getBanType();
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
        map.put("StartDate", this.startDate.toString());
        map.put("ExpirationDate", this.expirationDate.toString());
        map.put("Killer", this.killer == null ? null : this.killer.serialize());
        map.put("InCombatWith", this.inCombatWith == null ? null : this.inCombatWith.serialize());
        map.put("Location", locationMap);
        map.put("DeathMessage", this.deathMessage);
        map.put("BanTime", this.banTime);
        map.put("DamageCauseType", this.damageCauseType.name());

        return map;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public Killer getInCombatWith() {
        return inCombatWith;
    }

    public Location getLocation() {
        return location;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public DamageCauseType getDamageCauseType() {
        return damageCauseType;
    }

    public Killer getKiller() {
        return killer;
    }

    private String placeholdersReplacements(String message) {
        String replacedMessage = message;
        if (replacedMessage.contains("%raw_damage_cause%") && this.damageCause != null) {
            replacedMessage = replacedMessage.replaceAll("%raw_damage_cause%", this.damageCause.name().toLowerCase().replaceAll("_", " "));
        }
        if (replacedMessage.contains("%damage_cause_message_random%") && this.damageCause != null) {
            BanConfiguration banConfiguration = this.plugin.getConfigurations().getBanTimesConfiguration().getBanTimes().get(this.damageCause.name());
            Random random = new Random();
            replacedMessage = replacedMessage.replaceAll("%damage_cause_message_random%", banConfiguration.getDisplayMessages().get(random.nextInt(banConfiguration.getDisplayMessages().size())));
        }
        if (replacedMessage.contains("%damage_cause_type%") && this.damageCauseType != null) {
            replacedMessage = replacedMessage.replaceAll("%damage_cause_type%", this.damageCauseType.name().toLowerCase().replaceAll("_", " "));
        }
        if (replacedMessage.contains("%location%") && this.location != null) {
            replacedMessage = replacedMessage.replaceAll("%location%", this.location.toString());
        }
        if (replacedMessage.contains("%death_message%") && this.deathMessage != null) {
            replacedMessage = replacedMessage.replaceAll("%death_message%", this.deathMessage);
        }
        if (replacedMessage.contains("%death_message_stripped%") && this.deathMessage != null) {
            replacedMessage = replacedMessage.replaceAll("%death_message_stripped%", this.deathMessage.substring(this.deathMessage.indexOf(" ")));
        }
        if (replacedMessage.contains("%raw_ban_time%")) {
            replacedMessage = replacedMessage.replaceAll("%raw_ban_time%", Integer.toString(this.banTime));
        }
        if (replacedMessage.contains("%short_ban_time_left%") && this.expirationDate != null) {
            int tickTillExpiration = (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), this.expirationDate) * 20;
            replacedMessage = replacedMessage.replaceAll("%short_ban_time_left%", MessageUtils.getTimeFromTicks(tickTillExpiration, false, false));
        }
        if (replacedMessage.contains("%long_ban_time_left%") && this.expirationDate != null) {
            int tickTillExpiration = (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), this.expirationDate) * 20;
            replacedMessage = replacedMessage.replaceAll("%long_ban_time_left%", MessageUtils.getTimeFromTicks(tickTillExpiration, false, true));
        }
        if (replacedMessage.contains("%digital_ban_time_left%") && this.expirationDate != null) {
            int tickTillExpiration = (int) ChronoUnit.SECONDS.between(LocalDateTime.now(), this.expirationDate) * 20;
            replacedMessage = replacedMessage.replaceAll("%digital_ban_time_left%", MessageUtils.getTimeFromTicks(tickTillExpiration, true, false));
        }
        if (replacedMessage.contains("%short_expiration_date%") && this.expirationDate != null) {
            replacedMessage = replacedMessage.replaceAll("%short_expiration_date%", DateTimeFormatter.ofPattern("MM/dd/yy',' HH:mm z").withZone(ZoneId.systemDefault()).format(this.expirationDate));
        }
        if (replacedMessage.contains("%medium_expiration_date%") && this.expirationDate != null) {
            replacedMessage = replacedMessage.replaceAll("%medium_expiration_date%", DateTimeFormatter.ofPattern("MMM dd yyyy',' HH:mm z").withZone(ZoneId.systemDefault()).format(this.expirationDate));
        }
        if (replacedMessage.contains("%long_expiration_date%") && this.expirationDate != null) {
            replacedMessage = replacedMessage.replaceAll("%long_expiration_date%", DateTimeFormatter.ofPattern("EEEE MMM dd yyyy 'at' HH:mm:ss z").withZone(ZoneId.systemDefault()).format(this.expirationDate));
        }
        if (replacedMessage.contains("%killer%") && killer != null) {
            replacedMessage = replacedMessage.replaceAll("%killer%", this.killer.getFormattedName());
        }
        if (replacedMessage.contains("%in_combat_with%") && this.inCombatWith != null) {
            replacedMessage = replacedMessage.replaceAll("%in_combat_with%", this.inCombatWith.getFormattedName());
        }
        if (replacedMessage.contains("%combat_tagger%") && this.killer != null) {
            replacedMessage = replacedMessage.replaceAll("%combat_tagger%", this.inCombatWith == null ? this.killer.getFormattedName() : this.inCombatWith.getFormattedName());
        }
        return ChatColor.translateAlternateColorCodes('&', replacedMessage);
    }
}
