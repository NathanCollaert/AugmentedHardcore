package com.backtobedrock.LiteDeathBan.domain.configurationDomain;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCause;
import com.backtobedrock.LiteDeathBan.domain.enums.GrowthType;
import com.backtobedrock.LiteDeathBan.utils.ConfigUtils;
import org.bukkit.BanList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class BanTimesConfiguration {
    private final Map<String, BanConfiguration> banTimes;
    private final BanList.Type banType;
    private final boolean banTimeByPlaytime;
    private final boolean banTimeByPlaytimeSinceLastDeath;
    private final int banTimeByPlaytimeGrowthIncrease;
    private final int banTimeByPlaytimeGrowthInterval;
    private final GrowthType banTimeByPlaytimeGrowthType;
    private final List<String> disableBanInWorlds;

    public BanTimesConfiguration(Map<String, BanConfiguration> banTimes, BanList.Type banType, boolean banTimeByPlaytime, boolean banTimeByPlaytimeSinceLastDeath, int banTimeByPlaytimeGrowthIncrease, int banTimeByPlaytimeGrowthInterval, GrowthType banTimeByPlaytimeGrowthType, List<String> disableBanInWorlds) {
        this.banTimes = banTimes;
        this.banType = banType;
        this.banTimeByPlaytime = banTimeByPlaytime;
        this.banTimeByPlaytimeSinceLastDeath = banTimeByPlaytimeSinceLastDeath;
        this.banTimeByPlaytimeGrowthIncrease = banTimeByPlaytimeGrowthIncrease;
        this.banTimeByPlaytimeGrowthInterval = banTimeByPlaytimeGrowthInterval;
        this.banTimeByPlaytimeGrowthType = banTimeByPlaytimeGrowthType;
        this.disableBanInWorlds = disableBanInWorlds;
    }

    public static BanTimesConfiguration deserialize(ConfigurationSection section) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);

        //configurations
        Map<String, BanConfiguration> cBanTimes = new HashMap<>();
        BanList.Type cBanType = ConfigUtils.getBanType("BanType", section.getString("BanType", "name"), BanList.Type.NAME);
        boolean cBanTimeByPlaytime = section.getBoolean("BanTimeByPlaytime");
        boolean cBanTimeByPlaytimeSinceLastDeath = cBanTimeByPlaytime && section.getBoolean("BanTimeByPlaytimeSinceLastDeath");
        int cBanTimeByPlaytimeGrowthIncrease = cBanTimeByPlaytime ? ConfigUtils.checkMin("BanTimeByPlaytimeGrowthIncrease", section.getInt("BanTimeByPlaytimeGrowthIncrease"), 1) : 1;
        int cBanTimeByPlaytimeGrowthInterval = cBanTimeByPlaytime ? ConfigUtils.checkMin("BanTimeByPlaytimeGrowthInterval", section.getInt("BanTimeByPlaytimeGrowthInterval"), 1) : 1;
        GrowthType cBanTimeByPlaytimeGrowthType = cBanTimeByPlaytime ? ConfigUtils.getGrowthType("BanTimeByPlaytimeGrowthType", section.getString("BanTimeByPlaytimeGrowthType", "exponential"), GrowthType.EXPONENTIAL) : GrowthType.EXPONENTIAL;
        List<String> cDisableBanInWorlds = ConfigUtils.getWorlds("DisableBanInWorlds", section.getStringList("DisableBanInWorlds"));

        //loop over all damage causes
        ConfigurationSection deathCauseConfigurations = section.getConfigurationSection("DeathCauseConfigurations");
        if (deathCauseConfigurations == null) {
            plugin.getLogger().log(Level.SEVERE, "There were no ban times configured in the config.yml.");
            return null;
        }
        Arrays.stream(DamageCause.values()).forEach(e -> {
            cBanTimes.put(e.name(), BanConfiguration.deserialize(e, deathCauseConfigurations.getConfigurationSection(e.name())));
        });

        //check if everything configured correctly
        if (cBanTimeByPlaytimeGrowthIncrease == -10 || cBanTimeByPlaytimeGrowthInterval == -10) {
            return null;
        }

        return new BanTimesConfiguration(cBanTimes, cBanType, cBanTimeByPlaytime, cBanTimeByPlaytimeSinceLastDeath, cBanTimeByPlaytimeGrowthIncrease, cBanTimeByPlaytimeGrowthInterval, cBanTimeByPlaytimeGrowthType, cDisableBanInWorlds);
    }

    public Map<String, BanConfiguration> getBanTimes() {
        return banTimes;
    }

    public boolean isBanTimeByPlaytime() {
        return banTimeByPlaytime;
    }

    public boolean isBanTimeByPlaytimeSinceLastDeath() {
        return banTimeByPlaytimeSinceLastDeath;
    }

    public int getBanTimeByPlaytimeGrowthIncrease() {
        return banTimeByPlaytimeGrowthIncrease;
    }

    public int getBanTimeByPlaytimeGrowthInterval() {
        return banTimeByPlaytimeGrowthInterval;
    }

    public GrowthType getBanTimeByPlaytimeGrowthType() {
        return banTimeByPlaytimeGrowthType;
    }

    public List<String> getDisableBanInWorlds() {
        return disableBanInWorlds;
    }

    public BanList.Type getBanType() {
        return banType;
    }
}
