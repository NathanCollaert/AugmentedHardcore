package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    public static Ban Deserialize(ConfigurationSection section) {
        LocalDateTime cStartDate = LocalDateTime.parse(section.getString("StartDate", LocalDateTime.MIN.toString()));
        LocalDateTime cExpirationDate = LocalDateTime.parse(section.getString("ExpirationDate"));
        DamageCause cDamageCause = ConfigUtils.getDamageCause(section.getString("DamageCause", DamageCause.VOID.name()), DamageCause.VOID);
        Killer cKiller = null;
        Killer cInCombatWith = null;
        Location cLocation = new Location("world", 0, 0, 0);
        String cDeathMessage = section.getString("DeathMessage");
        int cBanTime = section.getInt("BanTime", 0);
        DamageCauseType cDamageCauseType = ConfigUtils.getDamageCauseType(section.getString("DamageCauseType", DamageCauseType.ENVIRONMENT.name()), DamageCauseType.ENVIRONMENT);

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
            banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getEntityWhileInCombatBanMessage(), this.getPlaceholdersReplacements());
        } else {
            //Entity while in combat
            banMessage = MessageUtils.replacePlaceholders(this.plugin.getMessages().getEntityWhileInCombatBanMessage(), this.getPlaceholdersReplacements());
        }
        return banMessage;
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

        placeholders.put("ban_death_message", this.deathMessage != null ? this.deathMessage : "-");

        placeholders.put("ban_death_message_stripped", this.deathMessage != null && !this.deathMessage.equalsIgnoreCase("") ? this.deathMessage.substring(this.deathMessage.indexOf(" ")) : "-");

        placeholders.put("ban_start_date_long", this.startDate != null ? MessageUtils.LONG_FORMATTER.format(this.startDate) : "-");
        placeholders.put("ban_start_date_medium", this.startDate != null ? MessageUtils.MEDIUM_FORMATTER.format(this.startDate) : "-");
        placeholders.put("ban_start_date_short", this.startDate != null ? MessageUtils.SHORT_FORMATTER.format(this.startDate) : "-");

        long ticksTillExpiration = MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), this.expirationDate), TimeUnit.SECONDS);
        placeholders.put("ban_time_left_long", MessageUtils.getTimeFromTicks(ticksTillExpiration, false, true));
        placeholders.put("ban_time_left_short", MessageUtils.getTimeFromTicks(ticksTillExpiration, false, false));
        placeholders.put("ban_time_left_digital", MessageUtils.getTimeFromTicks(ticksTillExpiration, true, false));
        placeholders.put("ban_expiration_date_long", MessageUtils.LONG_FORMATTER.format(this.expirationDate));
        placeholders.put("ban_expiration_date_medium", MessageUtils.MEDIUM_FORMATTER.format(this.expirationDate));
        placeholders.put("ban_expiration_date_short", MessageUtils.SHORT_FORMATTER.format(this.expirationDate));

        placeholders.put("ban_killer", this.killer != null ? this.killer.getFormattedName() : "-");
        placeholders.put("ban_combat_tagger", this.killer != null ? this.inCombatWith == null ? this.killer.getFormattedName() : this.inCombatWith.getFormattedName() : "-");

        placeholders.put("ban_in_combat_with", this.inCombatWith != null ? this.inCombatWith.getFormattedName() : "-");

        ticksTillExpiration = MessageUtils.timeUnitToTicks(this.banTime, TimeUnit.MINUTES);
        placeholders.put("ban_time_long", MessageUtils.getTimeFromTicks(ticksTillExpiration, false, true));
        placeholders.put("ban_time_short", MessageUtils.getTimeFromTicks(ticksTillExpiration, false, false));
        placeholders.put("ban_time_digital", MessageUtils.getTimeFromTicks(ticksTillExpiration, true, false));

        return placeholders;
    }
}
