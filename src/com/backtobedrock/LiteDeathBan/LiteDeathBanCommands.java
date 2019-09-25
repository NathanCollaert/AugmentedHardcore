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
    private final int maxLives;

    public LiteDeathBanCommands(LiteDeathBan plugin) {
        this.plugin = plugin;
        this.maxLives = this.plugin.getLDBConfig().getMaxLives();
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
                if (sender != null) {
                    if (cmnd.testPermission(cs)) {
                        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(sender, this.plugin);
                        sender.spigot().sendMessage(new ComponentBuilder(crud.getLives() > 1 ? "You have " + crud.getLives() + " lives left." : "You have one life left.").color(ChatColor.GOLD).create());
                    }
                } else {
                    cs.spigot().sendMessage(new ComponentBuilder("This command requires you to be in game.").color(ChatColor.RED).create());
                }
                return true;
            case "lifeparts":
                if (sender != null) {
                    if (cmnd.testPermission(cs)) {
                        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(sender, this.plugin);
                        sender.spigot().sendMessage(new ComponentBuilder(crud.getLifeParts() == 1 ? "You have one life part left." : "You have " + crud.getLifeParts() + " life parts left.").color(ChatColor.GOLD).create());
                    }
                } else {
                    cs.spigot().sendMessage(new ComponentBuilder("This command requires you to be in game.").color(ChatColor.RED).create());
                }
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
                if (sender != null) {
                    if (cmnd.testPermission(cs)) {
                        if (this.plugin.getLDBConfig().isRevive()) {
                            List<String> disableLosingLivesInWorlds = this.plugin.getLDBConfig().getDisableLosingLivesInWorlds();
                            List<String> disableBanInWorlds = this.plugin.getLDBConfig().getDisableBanInWorlds();
                            List<String> disableReviveInWorlds = this.plugin.getLDBConfig().getDisableReviveInWorlds();
                            if (!disableLosingLivesInWorlds.contains(sender.getWorld().getName().toLowerCase()) || !disableBanInWorlds.contains(sender.getWorld().getName().toLowerCase()) || !disableReviveInWorlds.contains(sender.getWorld().getName().toLowerCase())) {
                                if (!arg.equalsIgnoreCase(sender.getName())) {
                                    OfflinePlayer playerToRevive = Bukkit.getOfflinePlayer(arg);
                                    if (LiteDeathBanCRUD.doesPlayerDataExists(playerToRevive.getUniqueId().toString(), this.plugin)) {
                                        LiteDeathBanCRUD crudPlayerToRevive = new LiteDeathBanCRUD(playerToRevive, this.plugin);
                                        if (crudPlayerToRevive.getLives() != this.maxLives) {
                                            LiteDeathBanCRUD crud = new LiteDeathBanCRUD(playerToRevive, this.plugin);
                                            int timeLeft = this.plugin.getLDBConfig().getTimeBetweenRevives() - (int) Duration.between(crud.getLastRevive(), LocalDateTime.now()).toMinutes();
                                            if (timeLeft <= 0) {
                                                boolean containsConfirmation = this.plugin.doesConfirmationContain(sender.getUniqueId());
                                                if (!containsConfirmation) {
                                                    BukkitTask chatTask = new ReviveChatWarning(this.plugin, sender).runTaskLater(this.plugin, 7 * 20);
                                                    this.plugin.addToConfirmation(sender.getUniqueId(), arg, chatTask.getTaskId());
                                                    sender.spigot().sendMessage(new ComponentBuilder("Would you really like to give a life to " + arg + "?\n").color(ChatColor.GOLD).append("[Confirm]").color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revive " + arg + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to confirm revive").create())).append(" / or execute the following command ").color(ChatColor.GOLD).append("/revive " + arg + " confirm").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/revive " + arg + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to use command").create())).create());
                                                } else {
                                                    if (this.plugin.getFromConfirmation(sender.getUniqueId()).equalsIgnoreCase(arg)) {
                                                        sender.spigot().sendMessage(new ComponentBuilder("already reviving this player").create());
                                                    } else {
                                                        Bukkit.getScheduler().cancelTask(this.plugin.getFromTagList(sender.getUniqueId()));
                                                        this.plugin.removeFromConfirmation(sender.getUniqueId());
                                                        BukkitTask chatTask = new ReviveChatWarning(this.plugin, sender).runTaskLater(this.plugin, 7 * 20);
                                                        this.plugin.addToConfirmation(sender.getUniqueId(), arg, chatTask.getTaskId());
                                                        sender.spigot().sendMessage(new ComponentBuilder("Would you really like to give a life to " + arg + "?\n").color(ChatColor.GOLD).append("[Confirm]").color(ChatColor.GREEN).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/revive " + arg + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to confirm revive").create())).append(" / or execute the following command ").color(ChatColor.GOLD).append("/revive " + arg + " confirm").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/revive " + arg + " confirm")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to use command").create())).create());
                                                    }
                                                }
                                            } else {
                                                sender.spigot().sendMessage(new ComponentBuilder("Your revive ability is on cooldown for another " + timeLeft + " minutes.")
                                                        .color(ChatColor.RED).create());
                                            }
                                        } else {
                                            sender.spigot().sendMessage(new ComponentBuilder(playerToRevive.getName() + " already has the maximum amount of lives.").color(ChatColor.RED).create());
                                        }
                                    } else {
                                        sender.spigot().sendMessage(new ComponentBuilder(playerToRevive.getName() + " has no data on this server.").color(ChatColor.RED).create());
                                    }
                                } else {
                                    sender.spigot().sendMessage(new ComponentBuilder("You cannot revive yourself.").color(ChatColor.RED).create());
                                }
                            } else {
                                sender.spigot().sendMessage(new ComponentBuilder("Reviving is not available in this world.").color(ChatColor.RED).create());
                            }
                        } else {
                            sender.spigot().sendMessage(new ComponentBuilder("Reviving is not available.").color(ChatColor.RED).create());
                        }
                    }
                } else {
                    cs.spigot().sendMessage(new ComponentBuilder("You'll need to log in to use this command.").color(ChatColor.RED).create());
                }
                return true;
            case "lives":
                if (cs.hasPermission("litedeathban.lives.other")) {
                    OfflinePlayer plyr = Bukkit.getOfflinePlayer(arg);
                    if (LiteDeathBanCRUD.doesPlayerDataExists(plyr.getUniqueId().toString(), this.plugin)) {
                        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(plyr, this.plugin);
                        cs.spigot().sendMessage(new ComponentBuilder(crud.getLives() > 1 ? plyr.getName() + " has " + crud.getLives() + " lives left." : plyr.getName() + " has one life left.").color(ChatColor.GOLD).create());
                    } else {
                        cs.spigot().sendMessage(new ComponentBuilder(arg + " has no data on this server.").color(ChatColor.RED).create());
                    }
                }
                return true;
            case "lifeparts":
                if (cs.hasPermission("litedeathban.lifeparts.other")) {
                    OfflinePlayer plyr = Bukkit.getOfflinePlayer(arg);
                    if (LiteDeathBanCRUD.doesPlayerDataExists(plyr.getUniqueId().toString(), this.plugin)) {
                        LiteDeathBanCRUD crud = new LiteDeathBanCRUD(plyr, this.plugin);
                        cs.spigot().sendMessage(new ComponentBuilder(crud.getLifeParts() == 1 ? plyr.getName() + " has one life part left." : plyr.getName() + " has " + crud.getLifeParts() + " life parts left.").color(ChatColor.GOLD).create());
                    } else {
                        cs.spigot().sendMessage(new ComponentBuilder(arg + " has no data on this server.").color(ChatColor.RED).create());
                    }
                }
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
                if (sender != null) {
                    if (cmnd.testPermission(cs)) {
                        if (this.plugin.getLDBConfig().isRevive()) {
                            List<String> disableLosingLivesInWorlds = this.plugin.getLDBConfig().getDisableLosingLivesInWorlds();
                            List<String> disableBanInWorlds = this.plugin.getLDBConfig().getDisableBanInWorlds();
                            List<String> disableReviveInWorlds = this.plugin.getLDBConfig().getDisableReviveInWorlds();
                            if (!disableLosingLivesInWorlds.contains(sender.getWorld().getName().toLowerCase()) || !disableBanInWorlds.contains(sender.getWorld().getName().toLowerCase()) || !disableReviveInWorlds.contains(sender.getWorld().getName().toLowerCase())) {
                                if (args[1].equalsIgnoreCase("confirm") && this.plugin.doesConfirmationContain(sender.getUniqueId())) {
                                    String personInConfirmationList = this.plugin.getFromConfirmation(sender.getUniqueId());
                                    if (personInConfirmationList.equalsIgnoreCase(args[0])) {
                                        this.plugin.removeFromConfirmation(sender.getUniqueId());
                                        this.revivePlayer(sender, Bukkit.getOfflinePlayer(personInConfirmationList));
                                        sender.spigot().sendMessage(new ComponentBuilder("You've given a live to  " + args[0] + ".").color(ChatColor.GOLD).create());
                                    } else {
                                        sender.spigot().sendMessage(new ComponentBuilder("The player you wanted to revive was not " + args[0] + ".").color(ChatColor.RED).create());
                                    }
                                }
                            } else {
                                sender.spigot().sendMessage(new ComponentBuilder("Reviving is not available in this world.").color(ChatColor.RED).create());
                            }
                        } else {
                            sender.spigot().sendMessage(new ComponentBuilder("Reviving is not available.").color(ChatColor.RED).create());
                        }
                    }
                } else {
                    cs.spigot().sendMessage(new ComponentBuilder("You'll need to log in to use this command.").color(ChatColor.RED).create());
                }
                return true;
            case "setlives":
                OfflinePlayer plyr = Bukkit.getOfflinePlayer(args[0]);
                int amount = this.parseStringToInt(args[1], cs);
                if (cmnd.testPermission(cs)) {
                    if (LiteDeathBanCRUD.doesPlayerDataExists(plyr.getUniqueId().toString(), this.plugin)) {
                        if (amount >= 0) {
                            LiteDeathBanCRUD crud = new LiteDeathBanCRUD(plyr, this.plugin);
                            if (amount == 0 && plyr.isOnline()) {
                                crud.setLives(1, true);
                                ((Player) plyr).setHealth(0.0D);
                                cs.spigot().sendMessage(new ComponentBuilder(args[0] + " has been killed.").color(ChatColor.GOLD).create());
                            } else if (amount > 0) {
                                if (amount <= this.maxLives) {
                                    crud.setLives(amount, true);
                                    cs.spigot().sendMessage(new ComponentBuilder(args[0] + "'s lives has been set to " + amount + ".").color(ChatColor.GOLD).create());
                                } else {
                                    cs.spigot().sendMessage(new ComponentBuilder("Can't give that amount of lives to a player as the max amount of lives is " + this.maxLives + ".").color(ChatColor.RED).create());
                                }
                            } else {
                                cs.spigot().sendMessage(new ComponentBuilder("You cannot kill an offline player.").color(ChatColor.RED).create());
                            }
                        } else {
                            cs.spigot().sendMessage(new ComponentBuilder("You can't set a player's lives to " + amount + ".").color(ChatColor.RED).create());
                        }
                    } else {
                        cs.spigot().sendMessage(new ComponentBuilder(args[0] + " has no data on this server.").color(ChatColor.RED).create());
                    }
                }
                return true;
            case "addlives":
                plyr = Bukkit.getOfflinePlayer(args[0]);
                amount = this.parseStringToInt(args[1], cs);
                if (cmnd.testPermission(cs)) {
                    if (LiteDeathBanCRUD.doesPlayerDataExists(plyr.getUniqueId().toString(), this.plugin)) {
                        if (amount > 0) {
                            LiteDeathBanCRUD crud = new LiteDeathBanCRUD(plyr, this.plugin);
                            if (amount + crud.getLives() <= this.maxLives) {
                                crud.setLives(amount + crud.getLives(), true);
                                cs.spigot().sendMessage(new ComponentBuilder(args[0] + "'s lives has been set to " + crud.getLives() + ".").color(ChatColor.GOLD).create());
                            } else {
                                cs.spigot().sendMessage(new ComponentBuilder("Can't add that amount of lives, as the max amount of lives is " + this.maxLives + ". And " + args[0] + " already has " + crud.getLives() + " lives.").color(ChatColor.RED).create());
                            }
                        } else {
                            cs.spigot().sendMessage(new ComponentBuilder("You can't add " + amount + " lives to a player.").color(ChatColor.RED).create());
                        }
                    } else {
                        cs.spigot().sendMessage(new ComponentBuilder(args[0] + " has data on this server.").color(ChatColor.RED).create());
                    }
                }
                return true;
            default:
                return false;
        }
    }

    private void revivePlayer(Player sender, OfflinePlayer playerBeingRevived) {
        LiteDeathBanCRUD crudSender = new LiteDeathBanCRUD(sender, this.plugin);
        if (crudSender.getLives() == 1) {
            this.plugin.addToUsedRevive(sender.getUniqueId());
            sender.setHealth(0.0D);
        } else {
            crudSender.setLives(crudSender.getLives() - 1, false);
        }
        crudSender.setLastRevive(LocalDateTime.now(), true);
        if (playerBeingRevived.isBanned()) {
            Bukkit.getBanList(BanList.Type.NAME).pardon(playerBeingRevived.getName());
        } else {
            LiteDeathBanCRUD crudPlayerBeingRevived = new LiteDeathBanCRUD(playerBeingRevived, this.plugin);
            crudPlayerBeingRevived.setLives(crudPlayerBeingRevived.getLives() + 1, true);
            if (crudPlayerBeingRevived.getLives() == this.maxLives && playerBeingRevived.isOnline()) {
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
