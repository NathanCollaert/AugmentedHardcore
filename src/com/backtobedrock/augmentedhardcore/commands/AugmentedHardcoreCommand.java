package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.utils.CommandUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AugmentedHardcoreCommand extends AbstractCommand {
    public AugmentedHardcoreCommand(CommandSender cs, String[] args) {
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

        final int amount;
        switch (command) {
            case ADDLIVES:
                if (!this.hasPlayedBefore(this.args[1])) {
                    return;
                }

                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }

                this.plugin.getPlayerRepository().getByPlayer(this.player).thenAcceptAsync(playerData -> {
                    playerData.increaseLives(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            String.format("§aYou've been given §6%s§a, you now have §6%s§a.", amount + (amount == 1 ? " life" : " lives"), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives")),
                            String.format("§aYou successfully gave §6%s§a, §6%s§a now has §6%s§a.", amount + (amount == 1 ? " life" : " lives"), this.player.getName(), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"))
                    );
                });
                break;
            case ADDLIFEPARTS:
                if (!this.hasPlayedBefore(this.args[1])) {
                    return;
                }
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }
                this.plugin.getPlayerRepository().getByPlayer(this.player).thenAcceptAsync(playerData -> {
                    playerData.increaseLifeParts(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            String.format("§aYou've been given §6%s§a, you now have §6%s§a and §6%s§a.", amount + (amount == 1 ? " life part" : " life parts"), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts")),
                            String.format("§aYou successfully gave §6%s§a, §6%s§a now has §6%s§a and §6%s§a.", amount + (amount == 1 ? " life part" : " life parts"), this.player.getName(), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"))
                    );
                });
                break;
            case SETLIVES:
                if (!this.hasPlayedBefore(this.args[1])) {
                    return;
                }
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }
                this.plugin.getPlayerRepository().getByPlayer(this.player).thenAcceptAsync(playerData -> {
                    playerData.setLives(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            String.format("§aYour §6lives§a have been set to §6%d§a.", playerData.getLives()),
                            String.format("§aYou successfully set the §6lives§a of §6%s§a to §6%d§a.", this.player.getName(), playerData.getLives())
                    );
                });
                break;
            case SETLIFEPARTS:
                if (!this.hasPlayedBefore(this.args[1])) {
                    return;
                }
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }
                this.plugin.getPlayerRepository().getByPlayer(this.player).thenAcceptAsync(playerData -> {
                    playerData.setLifeParts(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            String.format("§aYour §6life parts§a have been set to §6%d§a, giving you §6%s§a and §6%s§a.", amount, playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts")),
                            String.format("§aYou successfully set the §6life parts§a of §6%s§a to §6%d§a, §6%s§a now has §6%s§a and §6%s§a.", this.player.getName(), amount, this.player.getName(), playerData.getLives() + (playerData.getLives() == 1 ? " life" : " lives"), playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"))
                    );
                });
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
        cs.sendMessage(helpMessage.toArray(new String[0]));
    }

    private boolean canGiveLiveOrLifeParts(OfflinePlayer player, int amount) {
        if (player == null) {
            return false;
        }
        return amount != -1;
    }

    private void sendSuccessMessages(OfflinePlayer player, String receiverSuccessMessage, String senderSuccessMessage) {
        if (player.isOnline()) {
            ((Player) player).sendMessage(receiverSuccessMessage);
        }
        if (this.csPlayer == null || this.csPlayer.getUniqueId() != player.getUniqueId()) {
            this.cs.sendMessage(senderSuccessMessage);
        }
    }
}
