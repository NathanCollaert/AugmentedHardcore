package com.backtobedrock.LiteDeathBan;

import com.backtobedrock.LiteDeathBan.runnables.ReviveChatWarning;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class LiteDeathBanCommands {

    private LiteDeathBan plugin = null;

    public LiteDeathBanCommands(LiteDeathBan plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String alias, String[] args) {
        switch (args.length) {
            case 0:
                return this.zeroParameters(cs, cmnd);
            case 1:
                return this.oneParameter(cs, cmnd, args[0]);
            case 2:
                return this.twoParameters(cs, cmnd, args);
            default:
                return false;
        }
    }

    private boolean zeroParameters(CommandSender cs, Command cmnd) {
        Player sender = null;
        if (cs instanceof Player) {
            sender = (Player) cs;
        }
        switch (cmnd.getName().toLowerCase()) {
            case "lives":
                //check if player
                if (sender == null) {
                    cs.spigot().sendMessage(new ComponentBuilder("You'll need to log in to use this command.").color(ChatColor.RED).create());
                    return true;
                }
                //check for permission
                if (!cmnd.testPermission(cs)) {
                    return true;
                }

                LiteDeathBanCRUD livesCrud = new LiteDeathBanCRUD(sender, this.plugin);
                cs.spigot().sendMessage(new ComponentBuilder(livesCrud.getLives() > 1 ? "You have " + livesCrud.getLives() + " lives left." : "You have one life left.").color(ChatColor.GOLD).create());
                return true;
            case "lifeparts":
                //check if player
                if (sender == null) {
                    cs.spigot().sendMessage(new ComponentBuilder("You'll need to log in to use this command.").color(ChatColor.RED).create());
                    return true;
                }
                //check for permission
                if (!cmnd.testPermission(cs)) {
                    return true;
                }

                LiteDeathBanCRUD lifePartsCrud = new LiteDeathBanCRUD(sender, this.plugin);
                cs.spigot().sendMessage(new ComponentBuilder(lifePartsCrud.getLifeParts() == 1 ? "You have one life part left." : "You have " + lifePartsCrud.getLifeParts() + " life parts left.").color(ChatColor.GOLD).create());
                return true;
        }
        return false;
    }

    private boolean oneParameter(CommandSender cs, Command cmnd, String arg) {
        Player sender = null;
        if (cs instanceof Player) {
            sender = (Player) cs;
        }
        switch (cmnd.getName().toLowerCase()) {
            case "revive":
                //check if player
                if (sender == null) {
                    cs.spigot().sendMessage(new ComponentBuilder("You'll need to log in to use this command.").color(ChatColor.RED).create());
                    return true;
                }
                //check for permission
                if (!cmnd.testPermission(cs)) {
                    return true;
                }
                //check if enabled
                if (!this.plugin.getLDBConfig().isRevive()) {
                    cs.spigot().sendMessage(new ComponentBuilder("Reviving is not available.").color(ChatColor.RED).create());
                    return true;
                }
                //check if in a disbaled world
                List<String> disableLosingLivesInWorlds = this.plugin.getLDBConfig().getDisableLosingLivesInWorlds();
                List<String> disableBanInWorlds = this.plugin.getLDBConfig().getDisableBanInWorlds();
                List<String> disableReviveInWorlds = this.plugin.getLDBConfig().getDisableReviveInWorlds();
                if (disableLosingLivesInWorlds.contains(sender.getWorld().getName().toLowerCase()) || disableBanInWorlds.contains(sender.getWorld().getName().toLowerCase()) || disableReviveInWorlds.contains(sender.getWorld().getName().toLowerCase())) {
                    cs.spigot().sendMessage(new ComponentBuilder("Reviving is not available in this world.").color(ChatColor.RED).create());
                    return true;
                }
                //check if trying to revive him/herself
                if (arg.equalsIgnoreCase(sender.getName())) {
                    cs.spigot().sendMessage(new ComponentBuilder("You cannot revive yourself.").color(ChatColor.RED).create());
                    return true;
                }
                //check for player data
                OfflinePlayer playerToRevive = Bukkit.getOfflinePlayer(arg);
                if (!LiteDeathBanCRUD.doesPlayerDataExists(playerToRevive, this.plugin)) {
                    cs.spigot().sendMessage(new ComponentBuilder(playerToRevive.getName() + " has no data on this server.").color(ChatColor.RED).create());
                    return true;
                }
                //check if max lives already
                LiteDeathBanCRUD reviveCrudPlayerToRevive = new LiteDeathBanCRUD(playerToRevive, this.plugin);
                if (reviveCrudPlayerToRevive.getLives() >= this.plugin.getLDBConfig().getMaxLives()) {
                    cs.spigot().sendMessage(new ComponentBuilder(playerToRevive.getName() + " already has the maximum amount of lives.").color(ChatColor.RED).create());
                    return true;
                }
                //check if on cooldown
                LiteDeathBanCRUD reviveCrudPlayer = new LiteDeathBanCRUD(sender, this.plugin);
                int timeLeft = this.plugin.getLDBConfig().getTimeBetweenRevives() - (int) Duration.between(reviveCrudPlayer.getLastRevive(), LocalDateTime.now()).toMinutes();
                if (timeLeft > 0) {
                    cs.spigot().sendMessage(new ComponentBuilder("Your revive ability is on cooldown for another " + timeLeft + " minutes.").color(ChatColor.RED).create());
                    return true;
                }

                //revive message
                boolean containsConfirmation = this.plugin.doesConfirmationContain(sender.getUniqueId());
                if (!containsConfirmation) {
                    BukkitTask chatTask = new ReviveChatWarning(this.plugin, sender).runTaskLater(this.plugin, 7 * 20);
                    this.plugin.addToConfirmation(sender.getUniqueId(), arg, chatTask.getTaskId());
                    cs.spigot().sendMessage(new ComponentBuilder("Would you really like to give a life to " + playerToRevive.getName() + "?\n").color(ChatColor.GOLD).append("[Confirm]").color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revive " + playerToRevive.getName() + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to confirm revive").create())).append(" or ").reset().color(ChatColor.GOLD).append("/revive " + playerToRevive.getName() + " confirm").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/revive " + playerToRevive.getName() + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to use command").create())).create());
                } else {
                    if (this.plugin.getFromConfirmation(sender.getUniqueId()).equalsIgnoreCase(arg)) {
                        cs.spigot().sendMessage(new ComponentBuilder("already reviving this player").color(ChatColor.YELLOW).create());
                    } else {
                        Bukkit.getScheduler().cancelTask(this.plugin.getFromConfirmationRunners(sender.getUniqueId()));
                        BukkitTask chatTask = new ReviveChatWarning(this.plugin, sender).runTaskLater(this.plugin, 7 * 20);
                        this.plugin.addToConfirmation(sender.getUniqueId(), arg, chatTask.getTaskId());
                        cs.spigot().sendMessage(new ComponentBuilder("Would you really like to give a life to " + playerToRevive.getName() + "?\n").color(ChatColor.GOLD).append("[Confirm]").color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revive " + playerToRevive.getName() + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to confirm revive").create())).append(" or ").reset().color(ChatColor.GOLD).append("/revive " + playerToRevive.getName() + " confirm").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/revive " + playerToRevive.getName() + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to use command").create())).create());
                    }
                }
                return true;
            case "lives":
                //check for permission
                if (!cs.hasPermission("litedeathban.lives.other")) {
                    cs.spigot().sendMessage(new ComponentBuilder(cmnd.getPermissionMessage()).color(ChatColor.RED).create());
                    return true;
                }
                //check for player data
                OfflinePlayer livesPlyr = Bukkit.getOfflinePlayer(arg);
                if (!LiteDeathBanCRUD.doesPlayerDataExists(livesPlyr, this.plugin)) {
                    cs.spigot().sendMessage(new ComponentBuilder(livesPlyr.getName() + " has no data on this server.").color(ChatColor.RED).create());
                    return true;
                }

                LiteDeathBanCRUD livesPlyrCrud = new LiteDeathBanCRUD(livesPlyr, this.plugin);
                cs.spigot().sendMessage(new ComponentBuilder(livesPlyrCrud.getLives() > 1 ? livesPlyr.getName() + " has " + livesPlyrCrud.getLives() + " lives left." : livesPlyr.getName() + " has one life left.").color(ChatColor.GOLD).create());
                return true;
            case "lifeparts":
                //check for permission
                if (!cs.hasPermission("litedeathban.lifeparts.other")) {
                    cs.spigot().sendMessage(new ComponentBuilder(cmnd.getPermissionMessage()).color(ChatColor.RED).create());
                    return true;
                }
                //check for player data
                OfflinePlayer lifePartsPlyr = Bukkit.getOfflinePlayer(arg);
                if (!LiteDeathBanCRUD.doesPlayerDataExists(lifePartsPlyr, this.plugin)) {
                    cs.spigot().sendMessage(new ComponentBuilder(lifePartsPlyr.getName() + " has no data on this server.").color(ChatColor.RED).create());
                    return true;
                }

                LiteDeathBanCRUD lifePartsPlyrCrud = new LiteDeathBanCRUD(lifePartsPlyr, this.plugin);
                cs.spigot().sendMessage(new ComponentBuilder(lifePartsPlyrCrud.getLifeParts() == 1 ? lifePartsPlyr.getName() + " has one life part left." : lifePartsPlyr.getName() + " has " + lifePartsPlyrCrud.getLifeParts() + " life parts left.").color(ChatColor.GOLD).create());
                return true;
            default:
                return false;
        }
    }

    private boolean twoParameters(CommandSender cs, Command cmnd, String[] args) {
        Player sender = null;
        if (cs instanceof Player) {
            sender = (Player) cs;
        }
        switch (cmnd.getName().toLowerCase()) {
            case "revive":
                //check if player
                if (sender == null) {
                    cs.spigot().sendMessage(new ComponentBuilder("You'll need to log in to use this command.").color(ChatColor.RED).create());
                    return true;
                }
                //check for permission
                if (!cmnd.testPermission(cs)) {
                    return true;
                }
                //check if enabled
                if (!this.plugin.getLDBConfig().isRevive()) {
                    cs.spigot().sendMessage(new ComponentBuilder("Reviving is not available.").color(ChatColor.RED).create());
                    return true;
                }
                //check if in disbaled world
                List<String> disableLosingLivesInWorlds = this.plugin.getLDBConfig().getDisableLosingLivesInWorlds();
                List<String> disableBanInWorlds = this.plugin.getLDBConfig().getDisableBanInWorlds();
                List<String> disableReviveInWorlds = this.plugin.getLDBConfig().getDisableReviveInWorlds();
                if (disableLosingLivesInWorlds.contains(sender.getWorld().getName().toLowerCase()) || disableBanInWorlds.contains(sender.getWorld().getName().toLowerCase()) || disableReviveInWorlds.contains(sender.getWorld().getName().toLowerCase())) {
                    cs.spigot().sendMessage(new ComponentBuilder("Reviving is not available in this world.").color(ChatColor.RED).create());
                    return true;
                }
                //check if confirmation
                if (!args[1].equalsIgnoreCase("confirm")) {
                    cs.spigot().sendMessage(new ComponentBuilder("/revive <player> confirm").color(ChatColor.RED).create());
                    return true;
                }
                //check if reviving someone
                if (!this.plugin.doesConfirmationContain(sender.getUniqueId())) {
                    cs.spigot().sendMessage(new ComponentBuilder("You are not reviving any player. Use /revive <player>").color(ChatColor.RED).create());
                    return true;
                }
                //check if confirming correct player
                String personInConfirmationList = this.plugin.getFromConfirmation(sender.getUniqueId());
                if (!personInConfirmationList.equalsIgnoreCase(args[0])) {
                    cs.spigot().sendMessage(new ComponentBuilder("The player you wanted to revive was not " + args[0] + ".").color(ChatColor.RED).create());
                    return true;
                }

                //revive
                Bukkit.getScheduler().cancelTask(this.plugin.getFromConfirmationRunners(sender.getUniqueId()));
                this.plugin.removeFromConfirmation(sender.getUniqueId());
                this.revivePlayer(sender, Bukkit.getOfflinePlayer(personInConfirmationList));
                cs.spigot().sendMessage(new ComponentBuilder("You've given a live to " + args[0] + ".").color(ChatColor.GOLD).create());
                return true;
            case "setlives":
                int amount = this.parseStringToInt(args[1], cs);
                //check for permission
                if (!cmnd.testPermission(cs)) {
                    return true;
                }
                //check for player data
                OfflinePlayer setLivesPlyrToSetLivesOf = Bukkit.getOfflinePlayer(args[0]);
                if (!LiteDeathBanCRUD.doesPlayerDataExists(setLivesPlyrToSetLivesOf, this.plugin)) {
                    cs.spigot().sendMessage(new ComponentBuilder(setLivesPlyrToSetLivesOf.getName() + " has no data on this server.").color(ChatColor.RED).create());
                    return true;
                }
                //check if amount < 0
                if (amount < 0) {
                    cs.spigot().sendMessage(new ComponentBuilder("You can't set a player's lives to " + amount + ".").color(ChatColor.RED).create());
                    return true;
                }

                LiteDeathBanCRUD setLivesCrudPlyrToSetLivesOf = new LiteDeathBanCRUD(setLivesPlyrToSetLivesOf, this.plugin);
                if (amount == 0 && setLivesPlyrToSetLivesOf.isOnline()) {
                    setLivesCrudPlyrToSetLivesOf.setLives(1, true);
                    ((Player) setLivesPlyrToSetLivesOf).setHealth(0.0D);
                    cs.spigot().sendMessage(new ComponentBuilder(setLivesPlyrToSetLivesOf.getName() + " has been killed.").color(ChatColor.GOLD).create());
                } else if (amount > 0) {
                    //check if amount > max lives
                    if (amount > this.plugin.getLDBConfig().getMaxLives()) {
                        cs.spigot().sendMessage(new ComponentBuilder("Can't give that amount of lives to a player as the max amount of lives is " + this.plugin.getLDBConfig().getMaxLives() + ".").color(ChatColor.RED).create());
                        return true;
                    }

                    setLivesCrudPlyrToSetLivesOf.setLives(amount, true);
                    cs.spigot().sendMessage(new ComponentBuilder(setLivesPlyrToSetLivesOf.getName() + "'s lives has been set to " + amount + ".").color(ChatColor.GOLD).create());
                } else {
                    cs.spigot().sendMessage(new ComponentBuilder("You cannot kill an offline player.").color(ChatColor.RED).create());
                }
                return true;
            case "addlives":
                amount = this.parseStringToInt(args[1], cs);
                //check for permission
                if (!cmnd.testPermission(cs)) {
                    return true;
                }
                //check for player data
                OfflinePlayer addLivesPlyrToAddLivesTo = Bukkit.getOfflinePlayer(args[0]);
                if (!LiteDeathBanCRUD.doesPlayerDataExists(addLivesPlyrToAddLivesTo, this.plugin)) {
                    cs.spigot().sendMessage(new ComponentBuilder(addLivesPlyrToAddLivesTo.getName() + " has no data on this server.").color(ChatColor.RED).create());
                    return true;
                }
                //check if amount <= 0
                if (amount <= 0) {
                    cs.spigot().sendMessage(new ComponentBuilder("You can't add " + amount + " lives to a player.").color(ChatColor.RED).create());
                    return true;
                }
                //check if player lives + amount isn't more then max lives
                LiteDeathBanCRUD addLivesCrudPlyrToAddLivesTo = new LiteDeathBanCRUD(addLivesPlyrToAddLivesTo, this.plugin);
                if (amount + addLivesCrudPlyrToAddLivesTo.getLives() > this.plugin.getLDBConfig().getMaxLives()) {
                    cs.spigot().sendMessage(new ComponentBuilder("Can't add that amount of lives, as the max amount of lives is " + this.plugin.getLDBConfig().getMaxLives() + ". And " + addLivesPlyrToAddLivesTo.getName() + " already has " + addLivesCrudPlyrToAddLivesTo.getLives() + " lives.").color(ChatColor.RED).create());
                    return true;
                }

                addLivesCrudPlyrToAddLivesTo.setLives(amount + addLivesCrudPlyrToAddLivesTo.getLives(), true);
                cs.spigot().sendMessage(new ComponentBuilder(addLivesPlyrToAddLivesTo.getName() + "'s lives has been set to " + addLivesCrudPlyrToAddLivesTo.getLives() + ".").color(ChatColor.GOLD).create());
                return true;
            default:
                return false;
        }
    }

    private void revivePlayer(Player sender, OfflinePlayer playerBeingRevived) {
        LiteDeathBanCRUD crudSender = new LiteDeathBanCRUD(sender, this.plugin);
        //if reviver has 1 life left, kill on revive
        if (crudSender.getLives() == 1) {
            this.plugin.addToUsedRevive(sender.getUniqueId());
            sender.setHealth(0.0D);
            //remove life on revive
        } else {
            crudSender.setLives(crudSender.getLives() - 1, false);
        }
        crudSender.setLastRevive(LocalDateTime.now(), true);
        //if player who is being revived is banned, unban
        if (playerBeingRevived.isBanned()) {
            Bukkit.getBanList(BanList.Type.NAME).pardon(playerBeingRevived.getName());
            //give life to player
        } else {
            LiteDeathBanCRUD crudPlayerBeingRevived = new LiteDeathBanCRUD(playerBeingRevived, this.plugin);
            crudPlayerBeingRevived.setLives(crudPlayerBeingRevived.getLives() + 1, true);
            if (crudPlayerBeingRevived.getLives() == this.plugin.getLDBConfig().getMaxLives() && playerBeingRevived.isOnline()) {
                ((Player) playerBeingRevived).spigot().sendMessage(new ComponentBuilder("You have reached the maximum amount of lives!").color(ChatColor.GOLD).create());
            } else {
                ((Player) playerBeingRevived).spigot().sendMessage(new ComponentBuilder("You have received an extra live from " + sender.getName() + ". Your total live count is now " + crudPlayerBeingRevived.getLives()).color(ChatColor.GOLD).create());
            }
        }
    }

    private int parseStringToInt(String arg, CommandSender cs) {
        int parsedInt = 0;
        try {
            parsedInt = Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            cs.spigot().sendMessage(new ComponentBuilder(arg + " is not a number.").color(ChatColor.RED).create());
        }
        return parsedInt;
    }
}
