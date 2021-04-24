package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.utils.CommandUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandAugmentedHardcore extends AbstractCommand {
    public CommandAugmentedHardcore(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
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

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.increaseLives(amount);

                        this.sendSuccessMessages(target,
                                String.format("§aYou've been given §e%s§a, you now have §e%s§a.", amount + (amount == 1 ? " life" : " lives"), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives")),
                                String.format("§aYou successfully gave §e%s§a, §e%s§a now has §e%s§a.", amount + (amount == 1 ? " life" : " lives"), this.args[1], playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"))
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    }).exceptionally(ex -> {
                        ex.printStackTrace();
                        return null;
                    });
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
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

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.increaseLifeParts(amount);

                        this.sendSuccessMessages(target,
                                String.format("§aYou've been given §e%s§a, you now have §e%s§a and §e%s§a.", amount + (amount == 1 ? " life part" : " life parts"), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts")),
                                String.format("§aYou successfully gave §e%s§a, §e%s§a now has §e%s§a and §e%s§a.", amount + (amount == 1 ? " life part" : " life parts"), this.args[1], playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"))
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    });
                });
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

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.setLives(amount);

                        this.sendSuccessMessages(target,
                                String.format("§aYour §elives§a have been set to §e%d§a.", playerData.getLives()),
                                String.format("§aYou successfully set the §elives§a of §e%s§a to §e%d§a.", this.args[1], playerData.getLives())
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    });
                });
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

                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
                        playerData.setLifeParts(amount);

                        this.sendSuccessMessages(target,
                                String.format("§aYour §elife parts§a have been set to §e%d§a, giving you §e%s§a and §e%s§a.", amount, playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts")),
                                String.format("§aYou successfully set the §elife parts§a of §e%s§a to §e%d§a, §e%s§a now has §e%s§a and §e%s§a.", this.args[1], amount, this.args[1], playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"))
                        );

                        this.plugin.getPlayerRepository().updatePlayerData(playerData);
                    });
                });
                break;
            case RELOAD:
                this.plugin.initialize();
                this.cs.sendMessage("§aAugmentedHardcore has successfully been reloaded.");
                break;
            default:
                this.sendHelpMessage();
        }
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
