package com.backtobedrock.LiteDeathBan.utils;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.Ban;
import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.Location;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.BanTimesConfiguration;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCause;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCauseType;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;

public class BanUtils {
    public static String getBanParameter(PlayerData data, OfflinePlayer player, BanList.Type type) {
        if (type == BanList.Type.IP) {
            return data.getIp();
        } else {
            return player.getName();
        }
    }

    public static void unDeathBan(OfflinePlayer player) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        plugin.getPlayerRepository().getByPlayer(player, data -> {
            plugin.getServerRepository().getServerData(data1 -> {
                if (!data1.isDeathBanned(player)) {
                    return;
                }

                BanList.Type type = data.getIp() == null ? BanList.Type.NAME : plugin.getConfigurations().getBanTimesConfiguration().getBanType();
                BanList banList = Bukkit.getBanList(type);
                String banParameter = getBanParameter(data, player, type);
                if (!banList.isBanned(banParameter)) {
                    return;
                }

                banList.pardon(banParameter);
                data1.removeBan(player);
            });
        });
    }

    public static Ban getBan(Player player, DamageCause damageCause, Killer killer, Killer inCombatWith, String deathMessage, DamageCauseType type) {
        BanTimesConfiguration config = JavaPlugin.getPlugin(LiteDeathBan.class).getConfigurations().getBanTimesConfiguration();
        int banTime = config.getBanTimeType().getBantime(player, config.getBanTimes().get(damageCause.name()).getBanTime());
        return new Ban(LocalDateTime.now().plusMinutes(banTime), damageCause, killer, inCombatWith, new Location(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), deathMessage, banTime, type);
    }
}
