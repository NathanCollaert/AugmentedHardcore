package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.guis.GuiMyStats;
import com.backtobedrock.augmentedhardcore.utils.PlayerUtils;
import org.bukkit.command.CommandSender;

import java.util.concurrent.ExecutionException;

public class CommandMyStats extends AbstractCommand {
    public CommandMyStats(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() throws ExecutionException, InterruptedException {
        Command command = Command.MYSTATS;

        if (this.args.length == 0 && !this.hasPermission(command)) {
            return;
        } else if (!this.hasPermission(Permission.MYSTATS_OTHER)) {
            return;
        }

        if (!this.isPlayer()) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        if (this.args.length == 0) {
            this.plugin.getPlayerRepository().getByPlayer(this.sender).thenAcceptAsync(playerData -> PlayerUtils.openInventory(this.sender, new GuiMyStats(this.sender, playerData).getInventory())).get();
        } else {
            this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
                if (!bool) {
                    return;
                }

                try {
                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> PlayerUtils.openInventory(this.sender, new GuiMyStats(this.sender, playerData).getInventory())).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).get();
        }
    }
}
