package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandUnDeathBan extends AbstractCommand {
    public CommandUnDeathBan(CommandSender cs, String[] args) {
        super(cs, args);
    }

    public CommandUnDeathBan(Player player, OfflinePlayer target) {
        super(player, new String[]{});
        this.target = target;
    }

    @Override
    public void run() {
        Command command = Command.UNDEATHBAN;

        if (!this.hasPermission(command)) {
            return;
        }

        //Needed for unbanning through GUI.
        if (this.target != null) {
            this.unDeathBan();
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
            if (!bool) {
                return;
            }

            this.unDeathBan();
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void unDeathBan() {
        this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(playerData -> {
            if (BanUtils.unDeathBan(playerData)) {
                this.cs.sendMessage(String.format("§a%s has successfully been unbanned from a death ban.", this.target.getName()));
            } else {
                this.cs.sendMessage(String.format("§c%s is not death banned by %s.", this.target.getName(), this.plugin.getDescription().getName()));
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
