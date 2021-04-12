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
    private final EnumMap<DamageCause, BanConfiguration> banTimes;
    private final BanList.Type banType;
    private final BanTimeType banTimeType;
    private final GrowthType banTimeByPlaytimeGrowthType;
    private final boolean selfHarmBan;
    private final List<String> disableBanInWorlds;

    public DeathBanConfiguration(boolean useDeathBan, EnumMap<DamageCause, BanConfiguration> banTimes, BanList.Type banType, BanTimeType banTimeType, GrowthType banTimeByPlaytimeGrowthType, boolean selfHarmBan, List<String> disableBanInWorlds) {
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
        EnumMap<DamageCause, BanConfiguration> cBanTimes = new EnumMap<>(DamageCause.class);
        BanList.Type cBanType = ConfigUtils.getBanType("BanType", section.getString("BanType", BanList.Type.NAME.name()), BanList.Type.NAME);
        BanTimeType cBanTimeType = ConfigUtils.getBanTimeType("BanTimeType", section.getString("BanTimeType", BanTimeType.STATIC.name()), BanTimeType.STATIC);
        GrowthType cBanTimeByPlaytimeGrowthType = ConfigUtils.getGrowthType("BanTimeByPlaytimeGrowthType", section.getString("BanTimeByPlaytimeGrowthType", GrowthType.EXPONENTIAL.name()), GrowthType.EXPONENTIAL);
        boolean cSelfHarmBan = section.getBoolean("SelfHarmBan", false);
        List<String> cDisableBanInWorlds = ConfigUtils.getWorlds("DisableBanInWorlds", section.getStringList("DisableBanInWorlds"));

        //loop over all damage causes
        ConfigurationSection banTimesConfigurations = section.getConfigurationSection("BanTimes");
        if (cUseDeathBan && banTimesConfigurations == null) {
            plugin.getLogger().log(Level.WARNING, "There were no ban times configured in the config.yml.");
        } else if (cUseDeathBan) {
            Arrays.stream(DamageCause.values()).forEach(e -> {
                ConfigurationSection damageCauseSection = banTimesConfigurations.getConfigurationSection(e.name());
                BanConfiguration banConfiguration = new BanConfiguration(0, Collections.emptyList());
                if (damageCauseSection != null) {
                    banConfiguration = BanConfiguration.deserialize(e, damageCauseSection);
                }
                cBanTimes.put(e, banConfiguration);
            });
        }

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

    public EnumMap<DamageCause, BanConfiguration> getBanTimes() {
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

    public boolean isUseDeathBan() {
        return useDeathBan;
    }
}
