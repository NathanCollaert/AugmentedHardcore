package com.backtobedrock.augmentedhardcore.domain.enums;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.groups.GroupHandler;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;

public enum BanTimeType {
    GROUP,
    STATIC,
    BANCOUNT,
    PLAYTIME,
    TIMESINCELASTDEATH;

    public int getBantime(Player player, PlayerData playerData, int max) {
        GrowthType growthType = JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getDeathBanConfiguration().getBanTimeByPlaytimeGrowthType();
        switch (this) {
            case GROUP:
                try {
                    GroupHandler groupHandler = AugmentedHardcore.getGroupHandler();
                    String textValue = (String)groupHandler.getAttribute(player);
                    if(!textValue.isEmpty()) {
                        return Integer.parseInt(textValue);
                    }
                    Bukkit.getLogger().warning("No ban time value found in group attribute.");
                } catch(Exception e) {
                    Bukkit.getLogger().warning("Failed to determine ban time value based on group, configuration invalid: " + e);
                    e.printStackTrace();
                }
                return 0;
            case STATIC:
                return max;
            case BANCOUNT:
                return growthType.getBanTime(Math.max(playerData.getBanCount(), 1) * 35, max);
            case PLAYTIME:
                return growthType.getBanTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200, max);
            case TIMESINCELASTDEATH:
                return growthType.getBanTime((int) MessageUtils.timeBetweenDatesToTicks(LocalDateTime.now(), playerData.getLastDeath()) / 1200, max);
            default:
                return 0;
        }
    }
}
