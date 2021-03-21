package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractCommand {

    protected final AugmentedHardcore plugin;
    protected final CommandSender cs;
    protected final Player csPlayer;
    protected final String[] args;
    protected OfflinePlayer player = null;

    public AbstractCommand(CommandSender cs, String[] args) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.cs = cs;
        this.csPlayer = cs instanceof Player ? (Player) cs : null;
        this.args = args;
    }

    public abstract void run();

    protected boolean hasPermission(Command command) {
        boolean hasPermission = this.cs.hasPermission(command.getPermission().getPermissionString());
        if (!hasPermission) {
            this.cs.sendMessage("§cYou have no permission to use this command.");
        }
        return hasPermission;
    }

    protected boolean isPlayer() {
        boolean isPlayer = this.csPlayer != null;
        if (!isPlayer) {
            this.cs.sendMessage("§cYou will need to log in to use this command.");
        }
        return isPlayer;
    }

    protected boolean hasCorrectAmountOfArguments(Command command) {
        if (this.args.length < command.getMinimumArguments() || this.args.length > command.getMaximumArguments()) {
            this.sendUsageMessage(command);
            return false;
        }
        return true;
    }

    protected boolean hasPlayedBefore(String playername) {
        @SuppressWarnings("deprecation") OfflinePlayer player = Bukkit.getOfflinePlayer(playername);
        //TODO: uncomment
//        if (!player.hasPlayedBefore()) {
//            this.cs.sendMessage(String.format("§c%s has not played on the server before.", player.getName()));
//            return false;
//        }
        this.player = player;
        return true;
    }

    public void sendUsageMessage(Command command) {
        this.cs.sendMessage(new String[]{"§8§m--------------§6 Command §fUsage §8§m--------------", command.getFancyVersion(), "§8§m------------------------------------------"});
    }
}
