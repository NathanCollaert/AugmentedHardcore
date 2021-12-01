package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utilities.ConfigUtils;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Ban {
    private final AugmentedHardcore plugin;

    //serializable
    private final LocalDateTime startDate;
    private final int banTime;
    private final DamageCause damageCause;
    private final DamageCauseType damageCauseType;
    private final Location location;
    private final Killer killer;
    private final Killer inCombatWith;
    private final String deathMessage;
    private final long timeSincePreviousDeathBan;
    private final long timeSincePreviousDeath;
    private LocalDateTime expirationDate;

    public Ban(LocalDateTime startDate, LocalDateTime expirationDate, int banTime, DamageCause damageCause, DamageCauseType damageCauseType, Location location, Killer killer, Killer inCombatWith, String deathMessage, long timeSincePreviousDeathBan, long timeSincePreviousDeath) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.startDate = startDate;
        this.expirationDate = expirationDate;
        this.banTime = banTime;
        this.damageCause = damageCause;
        this.damageCauseType = damageCauseType;
        this.location = location;
        this.killer = killer;
        this.inCombatWith = inCombatWith;
        this.deathMessage = deathMessage;
        this.timeSincePreviousDeathBan = timeSincePreviousDeathBan;
        this.timeSincePreviousDeath = timeSincePreviousDeath;
    }

    public static Ban Deserialize(ConfigurationSection section) {
        LocalDateTime cStartDate = LocalDateTime.parse(section.getString("StartDate", LocalDateTime.MIN.toString()));
        LocalDateTime cExpirationDate = LocalDateTime.parse(section.getString("ExpirationDate", LocalDateTime.MIN.toString()));
        DamageCause cDamageCause = ConfigUtils.getDamageCause(section.getString("DamageCause", DamageCause.VOID.name()), DamageCause.VOID);
        Killer cKiller = null;
        Killer cInCombatWith = null;
        Location cLocation = new Location("world", 0, 0, 0);
        String cDeathMessage = section.getString("DeathMessage");
        DamageCauseType cDamageCauseType = ConfigUtils.getDamageCauseType(section.getString("DamageCauseType", DamageCauseType.ENVIRONMENT.name()), DamageCauseType.ENVIRONMENT);
        int cBanTime = section.getInt("BanTime", 0);
        long cTimeSincePreviousDeathBan = section.getLong("TimeSincePreviousDeathBan", 0);
        long cTimeSincePreviousDeath = section.getLong("TimeSincePreviousDeath", 0);

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
            cInCombatWith = Killer.Deserialize(inCombatWithSection);
        }

        return new Ban(cStartDate, cExpirationDate, cBanTime, cDamageCause, cDamageCauseType, cLocation, cKiller, cInCombatWith, cDeathMessage, cTimeSincePreviousDeathBan, cTimeSincePreviousDeath);
    }

    public String getBanMessage() {
        String banMessage;
        if (this.damageCause == DamageCause.REVIVE) {
            if (this.inCombatWith == null) {
                //Revive
                banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getReviveBanMessage(), this.getPlaceholdersReplacements());
            } else {
                //Revive while in combat
                banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getReviveWhileInCombatBanMessage(), this.getPlaceholdersReplacements());
            }
        } else if (this.damageCause == DamageCause.COMBAT_LOG || this.damageCause == DamageCause.PLAYER_COMBAT_LOG) {
            //Combat log
            banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getCombatLogBanMessage(), this.getPlaceholdersReplacements());
        } else if (this.killer == null && this.inCombatWith == null) {
            //Environment
            banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getEnvironmentBanMessage(), this.getPlaceholdersReplacements());
        } else if (this.killer != null && this.inCombatWith == null) {
            //Entity
            banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getEntityBanMessage(), this.getPlaceholdersReplacements());
        } else if (this.killer == null) {
            //Environment while in combat
            banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getEnvironmentWhileInCombatBanMessage(), this.getPlaceholdersReplacements());
        } else {
            //Entity while in combat
            banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getEntityWhileInCombatBanMessage(), this.getPlaceholdersReplacements());
        }
        return banMessage;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
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
        map.put("DamageCauseType", this.damageCauseType.name());
        map.put("BanTime", this.banTime);
        map.put("TimeSincePreviousDeathBan", this.timeSincePreviousDeathBan);
        map.put("TimeSincePreviousDeath", this.timeSincePreviousDeath);

        return map;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public Killer getKiller() {
        return killer;
    }

    public Map<String, String> getPlaceholdersReplacements() {
        Map<String, String> placeholders = new HashMap<>();

        String damageCauseString = "-";
        String damageCauseMessage = "-";
        if (this.damageCause != null) {
            damageCauseString = this.damageCause.toString();
            BanConfiguration banConfiguration = this.plugin.getConfigurations().getDeathBanConfiguration().getBanTimes().get(this.damageCause);
            Random random = new Random();
            damageCauseMessage = banConfiguration.getDisplayMessages().get(random.nextInt(banConfiguration.getDisplayMessages().size()));
        }
        placeholders.put("ban_damage_cause_random_message", damageCauseMessage);
        placeholders.put("ban_damage_cause", damageCauseString);

        placeholders.put("ban_damage_cause_type", this.damageCauseType != null ? this.damageCauseType.toString() : "-");

        placeholders.put("ban_location", this.location != null ? this.location.toString() : "-");

        placeholders.put("ban_death_message", this.deathMessage != null && !this.deathMessage.isEmpty() ? this.deathMessage : "-");

        placeholders.put("ban_death_message_stripped", this.deathMessage != null && !this.deathMessage.equalsIgnoreCase("") ? this.deathMessage.substring(this.deathMessage.indexOf(" ")) : "-");

        placeholders.put("ban_start_date_long", this.startDate != null ? MessageUtils.LONG_FORMATTER.format(this.startDate) : "-");
        placeholders.put("ban_start_date_medium", this.startDate != null ? MessageUtils.MEDIUM_FORMATTER.format(this.startDate) : "-");
        placeholders.put("ban_start_date_short", this.startDate != null ? MessageUtils.SHORT_FORMATTER.format(this.startDate) : "-");

        long ticksTillExpiration = MessageUtils.timeBetweenDatesToTicks(LocalDateTime.now(), this.expirationDate);
        placeholders.put("ban_time_left_long", MessageUtils.getTimeFromTicks(ticksTillExpiration, TimePattern.LONG));
        placeholders.put("ban_time_left_short", MessageUtils.getTimeFromTicks(ticksTillExpiration, TimePattern.SHORT));
        placeholders.put("ban_time_left_digital", MessageUtils.getTimeFromTicks(ticksTillExpiration, TimePattern.DIGITAL));
        placeholders.put("ban_expiration_date_long", MessageUtils.LONG_FORMATTER.format(this.expirationDate));
        placeholders.put("ban_expiration_date_medium", MessageUtils.MEDIUM_FORMATTER.format(this.expirationDate));
        placeholders.put("ban_expiration_date_short", MessageUtils.SHORT_FORMATTER.format(this.expirationDate));

        placeholders.put("ban_killer", this.killer != null ? this.killer.getFormattedName() : "-");
        placeholders.put("ban_combat_tagger",
                this.killer != null ? this.inCombatWith == null ? this.killer.getFormattedName() : this.inCombatWith.getFormattedName() : this.inCombatWith != null ? this.inCombatWith.getFormattedName() : "-");

        placeholders.put("ban_in_combat_with", this.inCombatWith != null ? this.inCombatWith.getFormattedName() : "-");

        ticksTillExpiration = MessageUtils.timeUnitToTicks(this.banTime, TimeUnit.MINUTES);
        placeholders.put("ban_time_long", MessageUtils.getTimeFromTicks(ticksTillExpiration, TimePattern.LONG));
        placeholders.put("ban_time_short", MessageUtils.getTimeFromTicks(ticksTillExpiration, TimePattern.SHORT));
        placeholders.put("ban_time_digital", MessageUtils.getTimeFromTicks(ticksTillExpiration, TimePattern.DIGITAL));

        placeholders.put("ban_time_since_previous_death_ban_long", MessageUtils.getTimeFromTicks(this.timeSincePreviousDeathBan, TimePattern.LONG));
        placeholders.put("ban_time_since_previous_death_ban_short", MessageUtils.getTimeFromTicks(this.timeSincePreviousDeathBan, TimePattern.SHORT));
        placeholders.put("ban_time_since_previous_death_ban_digital", MessageUtils.getTimeFromTicks(this.timeSincePreviousDeathBan, TimePattern.DIGITAL));

        placeholders.put("ban_time_since_previous_death_long", MessageUtils.getTimeFromTicks(this.timeSincePreviousDeath, TimePattern.LONG));
        placeholders.put("ban_time_since_previous_death_short", MessageUtils.getTimeFromTicks(this.timeSincePreviousDeath, TimePattern.SHORT));
        placeholders.put("ban_time_since_previous_death_digital", MessageUtils.getTimeFromTicks(this.timeSincePreviousDeath, TimePattern.DIGITAL));

        return placeholders;
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public DamageCauseType getDamageCauseType() {
        return damageCauseType;
    }

    public Location getLocation() {
        return location;
    }

    public Killer getInCombatWith() {
        return inCombatWith;
    }

    public String getDeathMessage() {
        return deathMessage;
    }

    public long getTimeSincePreviousDeathBan() {
        return timeSincePreviousDeathBan;
    }

    public long getTimeSincePreviousDeath() {
        return timeSincePreviousDeath;
    }
}
