package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class CommandLifeParts extends AbstractCommand {
    public CommandLifeParts(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.LIFEPARTS;

        if (this.args.length == 0 && !this.hasPermission(command)) {
            return;
        } else if (!this.hasPermission(Permission.LIFEPARTS_OTHER)) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        if (this.args.length == 0) {
            if (!this.isPlayer()) {
                return;
            }

            this.runCommand(this.sender);
        } else {
            this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
                if (!bool) {
                    return;
                }

                this.runCommand(this.target);
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
    }

    private void runCommand(OfflinePlayer player) {
        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(this::sendSuccessMessage).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void sendSuccessMessage(PlayerData playerData) {
        this.cs.sendMessage(
                this.plugin.getMessages().getCommandLifePartsLeft(playerData.getPlayer().getName(),
                        playerData.getLifeParts() + (playerData.getLifeParts() == 1 ? " life part" : " life parts"),
                        Integer.toString(playerData.getLifeParts()))
        );
    }
}
