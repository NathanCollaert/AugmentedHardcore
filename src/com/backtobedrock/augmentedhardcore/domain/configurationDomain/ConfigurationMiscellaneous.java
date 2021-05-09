package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public class ConfigurationMiscellaneous {
    private final boolean disableArtificialRegeneration;
    private final boolean lightningOnDeath;
    private final List<String> commandsOnDeath;
    private final boolean deathScreen;

    public ConfigurationMiscellaneous(boolean disableArtificialRegeneration, boolean lightningOnDeath, List<String> commandsOnDeath, boolean deathScreen) {
        this.disableArtificialRegeneration = disableArtificialRegeneration;
        this.lightningOnDeath = lightningOnDeath;
        this.commandsOnDeath = commandsOnDeath;
        this.deathScreen = deathScreen;
    }

    public static ConfigurationMiscellaneous deserialize(ConfigurationSection section) {
        boolean cDisableArtificialRegeneration = section.getBoolean("DisableArtificialRegeneration", false);
        boolean cLightningOnDeath = section.getBoolean("LightningOnDeath", false);
        List<String> cCommandsOnDeath = section.getStringList("CommandsOnDeath");
        boolean cDeathScreen = section.getBoolean("DeathScreen", true);

        return new ConfigurationMiscellaneous(
                cDisableArtificialRegeneration,
                cLightningOnDeath,
                cCommandsOnDeath,
                cDeathScreen
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

    public boolean isDeathScreen() {
        return deathScreen;
    }
}
