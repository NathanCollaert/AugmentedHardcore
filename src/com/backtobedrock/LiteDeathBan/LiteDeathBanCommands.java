package com.backtobedrock.LiteDeathBan;

import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class LiteDeathBanCommands {

    private LiteDeathBan plugin = null;
    
    public LiteDeathBanCommands(LiteDeathBan plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        switch (cmnd.getName().toLowerCase()) {
            case "setpt":
                Player plyr = ((Player) cs).getPlayer();
                plyr.setStatistic(Statistic.PLAY_ONE_MINUTE, Integer.parseInt(args[0]) * 60 * 20);
                return true;

            default:
                return false;
        }
    }
}
