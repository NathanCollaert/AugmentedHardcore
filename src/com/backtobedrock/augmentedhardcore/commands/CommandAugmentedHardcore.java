package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.utils.CommandUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CommandAugmentedHardcore extends AbstractCommand {
    public CommandAugmentedHardcore(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() throws ExecutionException, InterruptedException {
        if (this.args.length < 1) {
            this.sendHelpMessage();
            return;
        }

        Command command = CommandUtils.getCommand(this.args[0]);
        if (command == null) {
            this.sendHelpMessage();
            return;
        }

        if (!this.hasPermission(command))
            return;

        if (!this.hasCorrectAmountOfArguments(command))
            return;

        switch (command) {
            case ADDLIVES:
                int amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    try {
                        this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                            playerData.increaseLives(amount);

                            this.sendSuccessMessages(target,
                                    String.format("§aYou've been given §6%s§a, you now have §6%s§a.", amount + (amount == 1 ? " life" : " lives"), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives")),
                                    String.format("§aYou successfully gave §6%s§a, §6%s§a now has §6%s§a.", amount + (amount == 1 ? " life" : " lives"), this.args[1], playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"))
                            );

                            this.plugin.getPlayerRepository().updatePlayerData(playerData);
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }).get();
                break;
            case ADDLIFEPARTS:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    try {
                        this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                            playerData.increaseLifeParts(amount);

                            this.sendSuccessMessages(target,
                                    String.format("§aYou've been given §6%s§a, you now have §6%s§a and §6%s§a.", amount + (amount == 1 ? " life part" : " life parts"), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts")),
                                    String.format("§aYou successfully gave §6%s§a, §6%s§a now has §6%s§a and §6%s§a.", amount + (amount == 1 ? " life part" : " life parts"), this.args[1], playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"))
                            );

                            this.plugin.getPlayerRepository().updatePlayerData(playerData);
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }).get();
                break;
            case SETLIVES:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    try {
                        this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                            playerData.setLives(amount);

                            this.sendSuccessMessages(target,
                                    String.format("§aYour §6lives§a have been set to §6%d§a.", playerData.getLives()),
                                    String.format("§aYou successfully set the §6lives§a of §6%s§a to §6%d§a.", this.args[1], playerData.getLives())
                            );

                            this.plugin.getPlayerRepository().updatePlayerData(playerData);
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }).get();
                break;
            case SETLIFEPARTS:
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (amount == -1) {
                    return;
                }

                this.hasPlayedBefore(this.args[1]).thenAcceptAsync(bool -> {
                    if (!bool) {
                        return;
                    }

                    try {
                        this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                            playerData.setLifeParts(amount);

                            this.sendSuccessMessages(target,
                                    String.format("§aYour §6life parts§a have been set to §6%d§a, giving you §6%s§a and §6%s§a.", amount, playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts")),
                                    String.format("§aYou successfully set the §6life parts§a of §6%s§a to §6%d§a, §6%s§a now has §6%s§a and §6%s§a.", this.args[1], amount, this.args[1], playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"))
                            );

                            this.plugin.getPlayerRepository().updatePlayerData(playerData);
                        }).get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }).get();
                break;
            case RELOAD:
                this.plugin.initialize();
                this.cs.sendMessage("§aAugmentedHardcore has successfully been reloaded.");
                break;
            default:
                this.sendHelpMessage();
        }
        //TODO: add set/add command for max health
    }

    private void sendHelpMessage() {
        List<String> helpMessage = new ArrayList<>();
        helpMessage.add("§8§m----------§6 Augmented Hardcore §fHelp §8§m----------");
        Arrays.stream(Command.values()).filter(e -> e.getPermission() != null).forEach(e -> helpMessage.add(e.getFancyVersion()));
        helpMessage.add("§8§m------------------------------------------");
        this.cs.sendMessage(helpMessage.toArray(new String[0]));
    }

    private void sendSuccessMessages(OfflinePlayer player, String receiverSuccessMessage, String senderSuccessMessage) {
        if (this.sender == null || (player != null && this.sender.getUniqueId() != player.getUniqueId())) {
            this.cs.sendMessage(senderSuccessMessage);
        }
        if (player != null) {
            ((Player) player).sendMessage(receiverSuccessMessage);
        }
    }
}
