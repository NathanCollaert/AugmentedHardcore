package com.backtobedrock.LiteDeathBan.commands;

import com.backtobedrock.LiteDeathBan.domain.enums.Command;
import com.backtobedrock.LiteDeathBan.utils.BanUtils;
import com.backtobedrock.LiteDeathBan.utils.CommandUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiteDeathBanCommand extends ICommand {
    public LiteDeathBanCommand(CommandSender cs, String[] args) {
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

        final OfflinePlayer player;
        final int amount;
        switch (command) {
            case ADDLIVES:
                if (!this.hasPlayedBefore()) {
                    return;
                }
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }
                this.plugin.getPlayerRepository().getByPlayer(this.player, data -> {
                    data.increaseLives(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            //TODO: get from config
                            String.format("§aYou've been given §6%s§a, you now have §6%s§a.", amount + (amount == 1 ? " life" : " lives"), data.getLives() + (data.getLives() == 1 ? " life" : " lives")),
                            String.format("§aYou successfully gave §6%s§a, §6%s§a now has §6%s§a.", amount + (amount == 1 ? " life" : " lives"), this.player.getName(), data.getLives() + (data.getLives() == 1 ? " life" : " lives"))
                    );
                });
                break;
            case ADDLIFEPARTS:
                if (!this.hasPlayedBefore()) {
                    return;
                }
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }
                this.plugin.getPlayerRepository().getByPlayer(this.player, data -> {
                    data.increaseLifeParts(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            //TODO: get from config
                            String.format("§aYou've been given §6%s§a, you now have §6%s§a and §6%s§a.", amount + (amount == 1 ? " life part" : " life parts"), data.getLives() + (data.getLives() == 1 ? " life" : " lives"), data.getLifeParts() + (data.getLifeParts() == 1 ? " life part" : " life parts")),
                            String.format("§aYou successfully gave §6%s§a, §6%s§a now has §6%s§a and §6%s§a.", amount + (amount == 1 ? " life part" : " life parts"), this.player.getName(), data.getLives() + (data.getLives() == 1 ? " life" : " lives"), data.getLifeParts() + (data.getLifeParts() == 1 ? " life part" : " life parts"))
                    );
                });
                break;
            case SETLIVES:
                if (!this.hasPlayedBefore()) {
                    return;
                }
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }
                this.plugin.getPlayerRepository().getByPlayer(this.player, data -> {
                    data.setLives(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            //TODO: get from config
                            String.format("§aYour §6lives§a have been set to §6%d§a.", data.getLives()),
                            String.format("§aYou successfully set the §6lives§a of §6%s§a to §6%d§a.", this.player.getName(), data.getLives())
                    );
                });
                break;
            case SETLIFEPARTS:
                if (!this.hasPlayedBefore()) {
                    return;
                }
                amount = CommandUtils.getPositiveNumberFromString(this.cs, this.args[2]);
                if (!this.canGiveLiveOrLifeParts(this.player, amount)) {
                    return;
                }
                this.plugin.getPlayerRepository().getByPlayer(this.player, data -> {
                    data.setLifeParts(this.player, amount);
                    this.sendSuccessMessages(this.player,
                            //TODO: get from config
                            String.format("§aYour §6life parts§a have been set to §6%d§a, giving you §6%s§a and §6%s§a.", amount, data.getLives() + (data.getLives() == 1 ? " life" : " lives"), data.getLifeParts() + (data.getLifeParts() == 1 ? " life part" : " life parts")),
                            String.format("§aYou successfully set the §6life parts§a of §6%s§a to §6%d§a, §6%s§a now has §6%s§a and §6%s§a.", this.player.getName(), amount, this.player.getName(), data.getLives() + (data.getLives() == 1 ? " life" : " lives"), data.getLifeParts() + (data.getLifeParts() == 1 ? " life part" : " life parts"))
                    );
                });
                break;
            case UNBAN:
                if (!this.hasPlayedBefore()) {
                    return;
                }

                this.plugin.getPlayerRepository().getByPlayer(this.player, data -> {
                    if (!data.isBanned(this.player)) {
                        cs.sendMessage(String.format("§c%s is not banned.", this.player.getName()));
                        return;
                    }

                    this.plugin.getServerRepository().getServerData(data1 -> {
                        if (!data1.isDeathBanned(this.player)) {
                            this.cs.sendMessage(String.format("§c%s is not death banned and cannot be unbanned by LiteDeathBan.", this.player.getName()));
                            return;
                        }

                        BanUtils.unDeathBan(data, this.player);
                        this.cs.sendMessage(String.format("§a%s has successfully been unbanned from a death ban.", this.player.getName()));
                    });
                });
                break;
            case RELOAD:
                this.plugin.initialize();
                this.cs.sendMessage("§aLiteDeathBan has successfully been reloaded.");
                break;
            default:
                this.sendHelpMessage();
        }
    }

    private void sendHelpMessage() {
        List<String> helpMessage = new ArrayList<>();
        helpMessage.add("§8§m----------§6 LiteDeathBan §fHelp §8§m----------");
        Arrays.stream(Command.values()).forEach(e -> helpMessage.add(e.getFancyVersion()));
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
