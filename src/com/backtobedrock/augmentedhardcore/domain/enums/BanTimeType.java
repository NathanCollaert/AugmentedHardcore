package com.backtobedrock.augmentedhardcore.domain.enums;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public enum BanTimeType {
    STATIC,
    BANCOUNT,
    PLAYTIME,
    TIMESINCELASTDEATH;

    public int getBantime(Player player, PlayerData playerData, int max) {
        GrowthType growthType = JavaPlugin.getPlugin(AugmentedHardcore.class).getConfigurations().getBanTimesConfiguration().getBanTimeByPlaytimeGrowthType();
        switch (this) {
            case STATIC:
                return max;
            case BANCOUNT:
                return growthType.getBanTime(playerData.getBanCount() * 35, max);
            case PLAYTIME:
                return growthType.getBanTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 1200, max);
            case TIMESINCELASTDEATH:
                return growthType.getBanTime(player.getStatistic(Statistic.TIME_SINCE_DEATH) / 1200, max);
            default:
                return 0;
        }
    }
}
