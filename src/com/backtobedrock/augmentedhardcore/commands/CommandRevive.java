package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.guis.GuiRevive;
import com.backtobedrock.augmentedhardcore.utils.PlayerUtils;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ExecutionException;

public class CommandRevive extends AbstractCommand {
    public CommandRevive(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() throws ExecutionException, InterruptedException {
        Command command = Command.REVIVE;

        if (!this.hasPermission(command)) {
            return;
        }

        if (!this.isPlayer()) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
            if (!bool) {
                return;
            }

            try {
                this.plugin.getPlayerRepository().getByPlayer(this.sender).thenAcceptAsync(playerData -> {
                    if (!playerData.checkRevivePermissionsReviver(this.target)) {
                        return;
                    }

                    try {
                        PlayerUtils.openInventory(this.sender, new GuiRevive(playerData, this.target).getInventory());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }).get();
    }
}
