package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ConfigurationMiscellaneous {
    private final boolean disableArtificialRegeneration;
    private final boolean lightningOnDeath;
    private final List<String> commandsOnDeath;

    public ConfigurationMiscellaneous(boolean disableArtificialRegeneration, boolean lightningOnDeath, List<String> commandsOnDeath) {
        this.disableArtificialRegeneration = disableArtificialRegeneration;
        this.lightningOnDeath = lightningOnDeath;
        this.commandsOnDeath = commandsOnDeath;
    }

    public static ConfigurationMiscellaneous deserialize(ConfigurationSection section) {
        boolean cDisableArtificialRegeneration = section.getBoolean("DisableArtificialRegeneration", false);
        boolean cLightningOnDeath = section.getBoolean("LightningOnDeath", false);
        List<String> cCommandsOnDeath = section.getStringList("CommandsOnDeath");

        return new ConfigurationMiscellaneous(
                cDisableArtificialRegeneration,
                cLightningOnDeath,
                cCommandsOnDeath
        );
    }

    public boolean isDisableArtificialRegeneration() {
        return disableArtificialRegeneration;
    }

    public boolean isLightningOnDeath() {
        return lightningOnDeath;
    }

    public List<String> getCommandsOnDeath() {
        return commandsOnDeath;
    }
}
