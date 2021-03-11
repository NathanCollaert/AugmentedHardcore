package com.backtobedrock.LiteDeathBan.commands;

import com.backtobedrock.LiteDeathBan.domain.enums.Command;
import org.bukkit.command.CommandSender;

public class UnDeathBanCommand extends AbstractCommand {
    public UnDeathBanCommand(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.UNDEATHBAN;

        if (!this.hasPermission(command))
            return;

        if (!this.hasCorrectAmountOfArguments(command))
            return;

        if (!this.hasPlayedBefore(this.args[0])) {
            return;
        }

        this.plugin.getPlayerRepository().getByPlayer(this.player, data -> {
            if (data.unDeathBan(this.cs, this.player, false)) {
                this.cs.sendMessage(String.format("§a%s has successfully been unbanned from a death ban.", this.player.getName()));
            }
        });
    }
}
