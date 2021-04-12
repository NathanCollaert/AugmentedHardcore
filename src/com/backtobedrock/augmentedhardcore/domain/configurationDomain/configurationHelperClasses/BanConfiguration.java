package com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;

public class BanConfiguration {
    private final int banTime;
    private final List<String> displayMessages;

    public BanConfiguration(int banTime, List<String> displayMessages) {
        this.banTime = banTime;
        this.displayMessages = displayMessages;
    }

    public static BanConfiguration deserialize(DamageCause cause, ConfigurationSection section) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        int cBanTime = ConfigUtils.checkMinBanTime(section.getInt("BanTime", cause.getDefaultBantime()), 0);
        List<String> cDisplayMessages = section.contains("DisplayMessages") ? section.getStringList("DisplayMessages") : null;

        if (cDisplayMessages == null) {
            cDisplayMessages = cause.getDefaultDisplayMessages();
            plugin.getLogger().log(Level.SEVERE, String.format("DeathCauseConfigurations: %s didn't have correct DisplayMessages configured and default value will be used: %s.", cause.name(), cause.getDefaultDisplayMessages().toString()));
        }

        if (cBanTime == -10) {
            cBanTime = cause.getDefaultBantime();
            plugin.getLogger().log(Level.SEVERE, String.format("DeathCauseConfigurations: %s didn't have a correct BanTime configured and default value will be used: %d.", cause.name(), cause.getDefaultBantime()));
        }

        return new BanConfiguration(cBanTime, cDisplayMessages);
    }

    public int getBanTime() {
        return banTime;
    }

    public List<String> getDisplayMessages() {
        return displayMessages;
    }
}
