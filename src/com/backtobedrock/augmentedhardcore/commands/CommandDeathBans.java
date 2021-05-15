package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.guis.GuiPlayerDeathBans;
import com.backtobedrock.augmentedhardcore.utilities.PlayerUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

public class CommandDeathBans extends AbstractCommand {
    public CommandDeathBans(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.DEATHBANS;

        if (this.args.length == 0 && !this.hasPermission(command)) {
            return;
        } else if (!this.hasPermission(Permission.DEATHBANS_OTHER)) {
            return;
        }

        if (!this.isPlayer()) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        if (this.args.length == 0) {
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
        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(playerData -> PlayerUtils.openInventory(this.sender, new GuiPlayerDeathBans(playerData))).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
