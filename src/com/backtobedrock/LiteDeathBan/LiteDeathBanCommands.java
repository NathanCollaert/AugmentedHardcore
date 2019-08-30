package com.backtobedrock.LiteDeathBan;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author PC_Nathan
 */
class LiteDeathBanCommands {

    private LiteDeathBan plugin = null;

    /**
     *
     * @param plugin
     */
    public LiteDeathBanCommands(LiteDeathBan plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        return false;
    }

}
