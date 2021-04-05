package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
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
import java.util.*;
import java.util.concurrent.TimeUnit;

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
        BanList.Type type = data.getIp() == null ? BanList.Type.NAME : this.plugin.getConfigurations().getDeathBanConfiguration().getBanType();
        BanList banList = Bukkit.getBanList(type);
        //ban player
        banList.addBan(BanUtils.getBanParameter(data, player, type), "", Timestamp.valueOf(this.getExpirationDate()), this.plugin.getDescription().getName());

        if (player.isOnline()) {
            Bukkit.getScheduler().runTask(this.plugin, () -> ((Player) player).kickPlayer(this.getBanMessage()));
        }
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

    public String placeholdersReplacements(String message) {
        Map<String, String> placeholders = new HashMap<>();

        if (this.damageCause != null) {
            placeholders.put("raw_damage_cause", this.damageCause.toString());
            BanConfiguration banConfiguration = this.plugin.getConfigurations().getDeathBanConfiguration().getBanTimes().get(this.damageCause.name());
            Random random = new Random();
            placeholders.put("damage_cause_message_random", banConfiguration.getDisplayMessages().get(random.nextInt(banConfiguration.getDisplayMessages().size())));
        }

        if (this.damageCauseType != null) {
            placeholders.put("damage_cause_type", this.damageCauseType.toString());
        }

        if (this.location != null) {
            placeholders.put("location", this.location.toString());
        }

        if (this.deathMessage != null) {
            placeholders.put("death_message", this.deathMessage);
        }

        if (this.deathMessage != null) {
            placeholders.put("death_message_stripped", this.deathMessage.substring(this.deathMessage.indexOf(" ")));
        }

        if (this.expirationDate != null) {
            long ticksTillExpiration = MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), this.expirationDate), TimeUnit.SECONDS);
            placeholders.put("short_ban_time_left", MessageUtils.getTimeFromTicks(ticksTillExpiration, false, false));
            placeholders.put("long_ban_time_left", MessageUtils.getTimeFromTicks(ticksTillExpiration, false, true));
            placeholders.put("digital_ban_time_left", MessageUtils.getTimeFromTicks(ticksTillExpiration, true, false));
            placeholders.put("short_expiration_date", MessageUtils.SHORT_FORMATTER.format(this.expirationDate));
            placeholders.put("medium_expiration_date", MessageUtils.MEDIUM_FORMATTER.format(this.expirationDate));
            placeholders.put("long_expiration_date", MessageUtils.LONG_FORMATTER.format(this.expirationDate));
        }

        if (killer != null) {
            placeholders.put("killer", this.killer.getFormattedName());
            placeholders.put("combat_tagger", this.inCombatWith == null ? this.killer.getFormattedName() : this.inCombatWith.getFormattedName());
        }

        if (this.inCombatWith != null) {
            placeholders.put("in_combat_with", this.inCombatWith.getFormattedName());
        }

        placeholders.put("raw_ban_time", Integer.toString(this.banTime));

        return MessageUtils.replacePlaceholders(message, placeholders);
    }
}
