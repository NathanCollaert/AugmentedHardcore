package com.backtobedrock.LiteDeathBan.domain.enums;

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
    //BYPASSES
    BYPASS_LOSELIVES,
    BYPASS_LOSELIFEPARTS,
    BYPASS_BAN,
    BYPASS_GAINLIFEPARTS_KILL,
    BYPASS_GAINLIFEPARTS_PLAYTIME,
    //GAINS
    GAIN_REVIVE_DEATH,
    GAIN_REVIVE_ALIVE;

    public String getPermissionString() {
        return "litedeathban." + this.name().replaceAll("_", ".").toLowerCase();
    }
}
