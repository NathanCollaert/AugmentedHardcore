package com.backtobedrock.augmentedhardcore.domain.enums;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalDateTime;

public enum BanTimeType {
    STATIC,
    BANCOUNT,
    PLAYTIME,
    TIMESINCELASTDEATH;

    public int getBantime(Player player, PlayerData playerData, int max) {
        GrowthType growthType = JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getDeathBanConfiguration().getBanTimeByPlaytimeGrowthType();
        switch (this) {
            case STATIC:
                return max;
            case BANCOUNT:
                return growthType.getBanTime(playerData.getBanCount() * 35, max);
            case PLAYTIME:
                return growthType.getBanTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200, max);
            case TIMESINCELASTDEATH:
                return growthType.getBanTime((int) MessageUtils.timeBetweenDatesToTicks(LocalDateTime.now(), playerData.getLastDeath()) / 1200, max);
            default:
                return 0;
        }
    }
}
