package com.backtobedrock.augmentedhardcore.utils;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.Location;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.DeathBanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCauseType;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class BanUtils {
    public static String getBanParameter(PlayerData data, OfflinePlayer player, BanList.Type type) {
        if (type == BanList.Type.IP) {
            return data.getIp();
        } else {
            return player.getName();
        }
    }

    public static void unDeathBan(OfflinePlayer player) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        CompletableFuture<PlayerData> playerFuture = plugin.getPlayerRepository().getByPlayer(player);
        CompletableFuture<ServerData> serverFuture = plugin.getServerRepository().getServerData();

        serverFuture.thenAcceptBothAsync(playerFuture, (serverData, playerData) -> {
            if (!serverData.isDeathBanned(player)) {
                return;
            }

            BanList.Type type = playerData.getIp() == null ? BanList.Type.NAME : plugin.getConfigurations().getDeathBanConfiguration().getBanType();
            BanList banList = Bukkit.getBanList(type);
            String banParameter = getBanParameter(playerData, player, type);
            if (!banList.isBanned(banParameter)) {
                return;
            }

            banList.pardon(banParameter);
            serverData.removeBan(player);
        });
    }

    public static Ban getBan(Player player, PlayerData playerData, DamageCause damageCause, Killer killer, Killer inCombatWith, String deathMessage, DamageCauseType type) {
        DeathBanConfiguration config = JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getDeathBanConfiguration();
        int rawBanTime = config.getBanTimes().get(damageCause.name()) == null ? 0 : config.getBanTimes().get(damageCause.name()).getBanTime();
        int banTime = config.getBanTimeType().getBantime(player, playerData, rawBanTime);
        return new Ban(LocalDateTime.now(), LocalDateTime.now().plusMinutes(banTime), damageCause, killer, inCombatWith, new Location(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), deathMessage, banTime, type);
    }
}
