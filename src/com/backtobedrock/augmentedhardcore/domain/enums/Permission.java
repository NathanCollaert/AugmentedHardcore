package com.backtobedrock.augmentedhardcore.domain.enums;

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
        return "augmentedhardcore." + this.name().replaceAll("_", ".").toLowerCase();
    }
}
