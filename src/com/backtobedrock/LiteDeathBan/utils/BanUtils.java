package com.backtobedrock.LiteDeathBan.utils;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.Ban;
import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.PlayerData;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.BanTimesConfiguration;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses.Location;
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

    public static void unDeathBan(PlayerData data, OfflinePlayer player) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        plugin.getServerRepository().getServerData(data1 -> {
            if (!data1.isDeathBanned(player)) {
                return;
            }

            BanList.Type type = data.getIp() == null ? BanList.Type.NAME : plugin.getConfiguration().getBanTimesConfiguration().getBanType();
            BanList banList = Bukkit.getBanList(type);
            String banParameter = getBanParameter(data, player, type);
            if (!banList.isBanned(banParameter)) {
                return;
            }

            banList.pardon(banParameter);
            data1.removeBan(player);
        });
    }

    public static Ban getBan(Player player, DamageCause damageCause, Killer killer, Killer inCombatWith, String deathMessage, DamageCauseType type) {
        BanTimesConfiguration config = JavaPlugin.getPlugin(LiteDeathBan.class).getConfiguration().getBanTimesConfiguration();
        int bantime = config.getBanTimes().get(damageCause.name()).getBanTime();
        //check if bantime by time since last death
        if (config.isBanTimeByPlaytimeSinceLastDeath()) {
            //TODO: implement
        }
        //check if bantime by playtime
        else if (config.isBanTimeByPlaytime()) {
            //TODO: implement
        }
        return new Ban(LocalDateTime.now().plusMinutes(bantime), damageCause, killer, inCombatWith, new Location(player.getLocation().getWorld().getName(), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()), deathMessage, bantime, type);
    }
}
