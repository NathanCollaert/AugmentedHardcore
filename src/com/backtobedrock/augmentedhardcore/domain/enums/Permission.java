package com.backtobedrock.augmentedhardcore.domain.enums;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.plugin.java.JavaPlugin;

public enum Permission {
    //COMMANDS
    HELP,
    ADDLIVES,
    ADDLIFEPARTS,
    SETLIVES,
    SETLIFEPARTS,
    RELOAD,
    UNDEATHBAN,
    REVIVE,
    LIVES,
    LIFEPARTS,
    ADDMAXLIVES,
    REMOVEMAXLIVES,
    SETMAXLIVES,
    DEATHBANS_OTHER,
    MYSTATS,
    MYSTATS_OTHER,
    //BYPASSES
    BYPASS_LOSELIVES,
    BYPASS_LOSELIFEPARTS,
    BYPASS_BAN,
    BYPASS_GAINLIFEPARTS_KILL,
    BYPASS_GAINLIFEPARTS_PLAYTIME,
    BYPASS_LOSEMAXHEALTH,
    BYPASS_GAINMAXHEALTH_KILL,
    BYPASS_GAINMAXHEALTH_PLAYTIME,
    BYPASS_ARTIFICIALREGENERATION,
    //GAINS
    GAIN_REVIVE_DEATH,
    GAIN_REVIVE_ALIVE;

    public String getPermissionString() {
        return JavaPlugin.getPlugin(AugmentedHardcore.class).getDescription().getName() + "." + this.name().replaceAll("_", ".").toLowerCase();
    }
}
