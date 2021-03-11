package com.backtobedrock.LiteDeathBan.domain.configurationDomain;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.configurationDomain.configurationHelperClasses.Display;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class GuisConfiguration {
    private final Display borderDisplay;

    public GuisConfiguration(Display borderDisplay) {
        this.borderDisplay = borderDisplay;
    }

    public static GuisConfiguration deserialize(ConfigurationSection section) {
        LiteDeathBan plugin = JavaPlugin.getPlugin(LiteDeathBan.class);

        Display cBorderDisplay;

        ConfigurationSection borderDisplaySection = section.getConfigurationSection("BorderDisplay");
        if (borderDisplaySection != null) {
            cBorderDisplay = Display.deserialize("BorderDisplay", borderDisplaySection);
        } else {
            plugin.getLogger().log(Level.SEVERE, "BorderDisplay was not found.");
            return null;
        }

        if (cBorderDisplay == null) {
            return null;
        }

        return new GuisConfiguration(cBorderDisplay);
    }

    public Display getBorderDisplay() {
        return borderDisplay;
    }
}
