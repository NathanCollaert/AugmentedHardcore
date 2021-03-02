package com.backtobedrock.LiteDeathBan.commands;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.enums.Command;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ICommand {

    protected final LiteDeathBan plugin;
    protected final CommandSender cs;
    protected final Player csPlayer;
    protected final String[] args;
    protected OfflinePlayer player = null;

    public ICommand(CommandSender cs, String[] args) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.cs = cs;
        this.csPlayer = cs instanceof Player ? (Player) cs : null;
        this.args = args;
    }

    public abstract void run();

    protected boolean hasPermission(Command command) {
        //TODO: make use of permission enum
        boolean hasPermission = this.cs.hasPermission("litedeathban." + command.getPermission());
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
        if ((this.args.length - 1) < command.getMinimumArguments() || (this.args.length - 1) > command.getMaximumArguments()) {
            this.sendUsageMessage(command);
            return false;
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    protected boolean hasPlayedBefore() {
        OfflinePlayer player = Bukkit.getOfflinePlayer(this.args[1]);
        if (!player.hasPlayedBefore()) {
            this.cs.sendMessage(String.format("§c%s has not played on the server before.", player.getName()));
            return false;
        }
        this.player = player;
        return true;
    }

    public void sendUsageMessage(Command command) {
        this.cs.sendMessage(new String[]{"§8§m--------------§6 Command §fUsage §8§m--------------", command.getFancyVersion(), "§8§m------------------------------------------"});
    }
}
