package com.backtobedrock.augmentedhardcore.utils;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.Location;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.DeathBanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class BanUtils {
    public static String getBanParameter(PlayerData data, BanList.Type type) {
        if (type == BanList.Type.IP) {
            return data.getIp();
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

        BanList.Type type = playerData.getIp() == null ? BanList.Type.NAME : plugin.getConfigurations().getDeathBanConfiguration().getBanType();
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

        BanList.Type type = (playerData == null || playerData.getIp() == null) ? BanList.Type.NAME : plugin.getConfigurations().getDeathBanConfiguration().getBanType();
        return Bukkit.getBanList(type).getBanEntry(BanUtils.getBanParameter(playerData, type));
    }

    public static Ban getDeathBan(Player player, PlayerData playerData, DamageCause damageCause, Killer killer, Killer inCombatWith, String deathMessage, DamageCauseType type) {
        DeathBanConfiguration config = JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getDeathBanConfiguration();
        BanConfiguration banConfiguration = config.getBanTimes().get(damageCause);
        int rawBanTime = banConfiguration == null ? 0 : banConfiguration.getBanTime();
        int banTime = config.getBanTimeType().getBantime(player, playerData, rawBanTime);
        return new Ban(LocalDateTime.now(), LocalDateTime.now().plusMinutes(banTime), damageCause, killer, inCombatWith, new Location(player.getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), deathMessage, banTime, type);
    }

    public static void deathBan(PlayerData playerData, Ban ban) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        BanList.Type type = playerData.getIp() == null ? BanList.Type.NAME : plugin.getConfigurations().getDeathBanConfiguration().getBanType();
        BanList banList = Bukkit.getBanList(type);
        banList.addBan(BanUtils.getBanParameter(playerData, type), "", Timestamp.valueOf(ban.getExpirationDate()), plugin.getDescription().getName());

        if (playerData.getPlayer().getPlayer() != null) {
            Bukkit.getScheduler().runTask(plugin, () -> playerData.getPlayer().getPlayer().kickPlayer(ban.getBanMessage()));
        }
    }
}
