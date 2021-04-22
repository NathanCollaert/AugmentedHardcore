package com.backtobedrock.augmentedhardcore.utils;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.Location;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.ConfigurationDeathBan;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class BanUtils {
    public static String getBanParameter(PlayerData data, BanList.Type type) {
        if (type == BanList.Type.IP) {
            return data.getLastKnownIp();
        } else {
            return data.getPlayer().getName();
        }
    }

    public static boolean unDeathBan(PlayerData playerData) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        if (playerData == null) {
            return false;
        }

        ServerData serverData = plugin.getServerRepository().getServerDataSync();
        if (!serverData.isDeathBanned(playerData.getPlayer())) {
            return false;
        }

        BanList.Type type = playerData.getLastKnownIp() == null ? BanList.Type.NAME : plugin.getConfigurations().getDeathBanConfiguration().getBanType();
        BanList banList = Bukkit.getBanList(type);
        String banParameter = BanUtils.getBanParameter(playerData, type);
        BanEntry entry = banList.getBanEntry(banParameter);

        if (entry == null) {
            return false;
        }

        if (!entry.getSource().equalsIgnoreCase(plugin.getDescription().getName())) {
            serverData.removeBan(playerData.getPlayer());
            return false;
        }

        banList.pardon(banParameter);
        serverData.removeBan(playerData.getPlayer());
        return true;
    }

    public static BanEntry isBanned(PlayerData playerData) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        BanList.Type type = (playerData == null || playerData.getLastKnownIp() == null) ? BanList.Type.NAME : plugin.getConfigurations().getDeathBanConfiguration().getBanType();
        return Bukkit.getBanList(type).getBanEntry(BanUtils.getBanParameter(playerData, type));
    }

    public static Ban getDeathBan(Player player, PlayerData playerData, DamageCause damageCause, Killer killer, Killer inCombatWith, String deathMessage, DamageCauseType type) {
        ConfigurationDeathBan config = JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getDeathBanConfiguration();
        BanConfiguration banConfiguration = config.getBanTimes().get(damageCause);
        int rawBanTime = banConfiguration == null ? 0 : banConfiguration.getBanTime();
        int banTime = config.getBanTimeType().getBantime(player, playerData, rawBanTime);

        return new Ban(
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(banTime),
                banTime,
                damageCause,
                type,
                new Location(player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()),
                killer,
                inCombatWith,
                deathMessage,
                playerData.getLastDeathBan() == null ? player.getStatistic(Statistic.PLAY_ONE_MINUTE) : MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(playerData.getLastDeathBan().getStartDate(), LocalDateTime.now()), TimeUnit.SECONDS),
                player.getStatistic(Statistic.TIME_SINCE_DEATH)
        );
    }

    public static void deathBan(PlayerData playerData, Ban ban) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        BanList.Type type = playerData.getLastKnownIp() == null ? BanList.Type.NAME : plugin.getConfigurations().getDeathBanConfiguration().getBanType();
        BanList banList = Bukkit.getBanList(type);
        banList.addBan(BanUtils.getBanParameter(playerData, type), String.format("Death banned by %s", plugin.getDescription().getName()), Date.from(ZonedDateTime.of(ban.getExpirationDate(), ZoneId.systemDefault()).toInstant()), plugin.getDescription().getName());

        Player player = playerData.getPlayer().getPlayer();
        if (player != null) {
            if (player.hasPermission(Permission.BYPASS_BAN_SPECTATOR.getPermissionString())) {
                Bukkit.getScheduler().runTask(plugin, () -> player.setGameMode(GameMode.SPECTATOR));
            } else {
                Bukkit.getScheduler().runTask(plugin, () -> player.kickPlayer(ban.getBanMessage()));
            }
        }
    }
}
