package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.BanConfiguration;
import com.backtobedrock.augmentedhardcore.domain.enums.BanTimeType;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.domain.enums.GrowthType;
import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.BanList;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public class DeathBanConfiguration {
    private final boolean useDeathBan;
    private final Map<String, BanConfiguration> banTimes;
    private final BanList.Type banType;
    private final BanTimeType banTimeType;
    private final GrowthType banTimeByPlaytimeGrowthType;
    private final boolean selfHarmBan;
    private final List<String> disableBanInWorlds;

    public DeathBanConfiguration(boolean useDeathBan, Map<String, BanConfiguration> banTimes, BanList.Type banType, BanTimeType banTimeType, GrowthType banTimeByPlaytimeGrowthType, boolean selfHarmBan, List<String> disableBanInWorlds) {
        this.useDeathBan = useDeathBan;
        this.banTimes = banTimes;
        this.banType = banType;
        this.banTimeType = banTimeType;
        this.banTimeByPlaytimeGrowthType = banTimeByPlaytimeGrowthType;
        this.selfHarmBan = selfHarmBan;
        this.disableBanInWorlds = disableBanInWorlds;
    }

    public static DeathBanConfiguration deserialize(ConfigurationSection section) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        //configurations
        boolean cUseDeathBan = section.getBoolean("UseDeathBan", true);
        Map<String, BanConfiguration> cBanTimes = new HashMap<>();
        BanList.Type cBanType = cUseDeathBan ? ConfigUtils.getBanType("BanType", section.getString("BanType", BanList.Type.NAME.name()), BanList.Type.NAME) : BanList.Type.NAME;
        BanTimeType cBanTimeType = cUseDeathBan ? ConfigUtils.getBanTimeType("BanTimeType", section.getString("BanTimeType", BanTimeType.STATIC.name()), BanTimeType.STATIC) : BanTimeType.STATIC;
        GrowthType cBanTimeByPlaytimeGrowthType = cUseDeathBan ? ConfigUtils.getGrowthType("BanTimeByPlaytimeGrowthType", section.getString("BanTimeByPlaytimeGrowthType", GrowthType.EXPONENTIAL.name()), GrowthType.EXPONENTIAL) : GrowthType.LINEAR;
        boolean cSelfHarmBan = section.getBoolean("SelfHarmBan", false);
        List<String> cDisableBanInWorlds = ConfigUtils.getWorlds("DisableBanInWorlds", section.getStringList("DisableBanInWorlds"));

        //loop over all damage causes
        ConfigurationSection banTimesConfigurations = section.getConfigurationSection("BanTimes");
        if (cUseDeathBan && banTimesConfigurations == null) {
            plugin.getLogger().log(Level.SEVERE, "There were no ban times configured in the config.yml.");
            return null;
        }
        Arrays.stream(DamageCause.values()).forEach(e -> {
            ConfigurationSection damageCauseSection = banTimesConfigurations.getConfigurationSection(e.name());
            BanConfiguration banConfiguration = new BanConfiguration(e, 0, Collections.emptyList());
            if (cUseDeathBan && damageCauseSection != null)
                banConfiguration = BanConfiguration.deserialize(e, damageCauseSection);
            cBanTimes.put(e.name(), banConfiguration);
        });

        return new DeathBanConfiguration(
                cUseDeathBan,
                cBanTimes,
                cBanType,
                cBanTimeType,
                cBanTimeByPlaytimeGrowthType,
                cSelfHarmBan,
                cDisableBanInWorlds
        );
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
