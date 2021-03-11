package com.backtobedrock.LiteDeathBan.domain.configurationDomain;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.LiteDeathBan.domain.enums.BanTimeType;
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
    private final BanTimeType banTimeType;
    private final GrowthType banTimeByPlaytimeGrowthType;
    private final boolean selfHarmBan;
    private final List<String> disableBanInWorlds;

    public BanTimesConfiguration(Map<String, BanConfiguration> banTimes, BanList.Type banType, BanTimeType banTimeType, GrowthType banTimeByPlaytimeGrowthType, boolean selfHarmBan, List<String> disableBanInWorlds) {
        this.banTimes = banTimes;
        this.banType = banType;
        this.banTimeType = banTimeType;
        this.banTimeByPlaytimeGrowthType = banTimeByPlaytimeGrowthType;
        this.selfHarmBan = selfHarmBan;
        this.disableBanInWorlds = disableBanInWorlds;
    }

    public static BanTimesConfiguration deserialize(ConfigurationSection section) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);

        //configurations
        Map<String, BanConfiguration> cBanTimes = new HashMap<>();
        BanList.Type cBanType = ConfigUtils.getBanType("BanType", section.getString("BanType", "name"), BanList.Type.NAME);
        BanTimeType cBanTimeType = ConfigUtils.getBanTimeType("BanTimeType", section.getString("BanTimeType", "STATIC"), BanTimeType.STATIC);
        GrowthType cBanTimeByPlaytimeGrowthType = ConfigUtils.getGrowthType("BanTimeByPlaytimeGrowthType", section.getString("BanTimeByPlaytimeGrowthType", "EXPONENTIAL"), GrowthType.EXPONENTIAL);
        boolean cSelfHarmBan = section.getBoolean("SelfHarmBan", true);
        List<String> cDisableBanInWorlds = ConfigUtils.getWorlds("DisableBanInWorlds", section.getStringList("DisableBanInWorlds"));

        //loop over all damage causes
        ConfigurationSection banTimesConfigurations = section.getConfigurationSection("BanTimes");
        if (banTimesConfigurations == null) {
            plugin.getLogger().log(Level.SEVERE, "There were no ban times configured in the config.yml.");
            return null;
        }
        Arrays.stream(DamageCause.values()).forEach(e -> {
            cBanTimes.put(e.name(), BanConfiguration.deserialize(e, banTimesConfigurations.getConfigurationSection(e.name())));
        });

        return new BanTimesConfiguration(
                cBanTimes,
                cBanType,
                cBanTimeType,
                cBanTimeByPlaytimeGrowthType,
                cSelfHarmBan,
                cDisableBanInWorlds);
    }

    public Map<String, BanConfiguration> getBanTimes() {
        return banTimes;
    }

    public List<String> getDisableBanInWorlds() {
        return disableBanInWorlds;
    }

    public BanList.Type getBanType() {
        return banType;
    }

    public BanTimeType getBanTimeType() {
        return banTimeType;
    }

    public GrowthType getBanTimeByPlaytimeGrowthType() {
        return banTimeByPlaytimeGrowthType;
    }

    public boolean isSelfHarmBan() {
        return selfHarmBan;
    }
}
