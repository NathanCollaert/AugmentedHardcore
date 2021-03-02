package com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCause;
import com.backtobedrock.LiteDeathBan.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class BanConfiguration {
    private final DamageCause damageCause;
    private final int banTime;
    private final String displayName;

    public BanConfiguration(DamageCause damageCause, int banTime, String displayName) {
        this.damageCause = damageCause;
        this.banTime = banTime;
        this.displayName = displayName;
    }

    public static BanConfiguration deserialize(DamageCause cause, ConfigurationSection section) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);

        if (section == null) {
            plugin.getLogger().log(Level.SEVERE, String.format("DeathCauseConfigurations: %s was not configured and default values will be used. BanTime: %d, DisplayName: %s.", cause.name(), cause.getDefaultBantime(), cause.getDefaultDisplayName()));
            return new BanConfiguration(cause, cause.getDefaultBantime(), cause.getDefaultDisplayName());
        }

        int cBanTime = ConfigUtils.checkMinBanTime(section.getInt("BanTime", cause.getDefaultBantime()), 0);
        String cDisplayName = section.getString("DisplayName", cause.getDefaultDisplayName());

        if (cBanTime == -10) {
            cBanTime = cause.getDefaultBantime();
            plugin.getLogger().log(Level.SEVERE, String.format("DeathCauseConfigurations: %s didn't have a correct BanTime configured and default value will be used: %d.", cause.name(), cause.getDefaultBantime()));
        }

        return new BanConfiguration(cause, cBanTime, cDisplayName);
    }

    public DamageCause getDamageCause() {
        return damageCause;
    }

    public int getBanTime() {
        return banTime;
    }

    public String getDisplayName() {
        return displayName;
    }
}
