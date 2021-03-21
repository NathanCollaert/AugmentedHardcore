package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.configurationHelperClasses.Display;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class GuisConfiguration {
    private final Display borderDisplay;
    private final Display nextPageDisplay;
    private final Display previousPageDisplay;
    private final Display pageInformationDisplay;

    public GuisConfiguration(Display borderDisplay, Display nextPageDisplay, Display previousPageDisplay, Display pageInformationDisplay) {
        this.borderDisplay = borderDisplay;
        this.nextPageDisplay = nextPageDisplay;
        this.previousPageDisplay = previousPageDisplay;
        this.pageInformationDisplay = pageInformationDisplay;
    }

    public static GuisConfiguration deserialize(ConfigurationSection section) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);

        Map<String, Display> cDisplays = new HashMap<>();

        for (String e : Arrays.asList("BorderDisplay", "NextPageDisplay", "PreviousPageDisplay", "PageInformationDisplay")) {
            ConfigurationSection displaySection = section.getConfigurationSection(e);
            if (displaySection != null) {
                Display display = Display.deserialize(e, displaySection);
                if (display == null) {
                    return null;
                }
                cDisplays.put(e, display);
            } else {
                plugin.getLogger().log(Level.SEVERE, String.format("%s was not found, plugin is unable to load.", e));
                return null;
            }
        }

        return new GuisConfiguration(
                cDisplays.get("BorderDisplay"),
                cDisplays.get("NextPageDisplay"),
                cDisplays.get("PreviousPageDisplay"),
                cDisplays.get("PageInformationDisplay")
        );
    }

    public Display getBorderDisplay() {
        return borderDisplay;
    }

    public Display getNextPageDisplay() {
        return nextPageDisplay;
    }

    public Display getPreviousPageDisplay() {
        return previousPageDisplay;
    }

    public Display getPageInformationDisplay() {
        return pageInformationDisplay;
    }
}
