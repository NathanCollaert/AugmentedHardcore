package com.backtobedrock.LiteDeathBan.commands;

import com.backtobedrock.LiteDeathBan.domain.enums.Command;
import com.backtobedrock.LiteDeathBan.guis.ReviveGui;
import org.bukkit.command.CommandSender;

public class ReviveCommand extends AbstractCommand {
    public ReviveCommand(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.REVIVE;

        if (!this.hasPermission(command))
            return;

        if (!this.isPlayer())
            return;

        if (!this.hasCorrectAmountOfArguments(command))
            return;

        if (!this.hasPlayedBefore(this.args[0])) {
            return;
        }

        this.plugin.getPlayerRepository().getByPlayer(this.csPlayer, data -> {
            if (!data.checkRevivePermissions(this.csPlayer, this.player))
                return;
            this.csPlayer.openInventory(new ReviveGui(this.csPlayer, this.player).getInventory());
        });
    }
}
