package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNextLifePart extends AbstractCommand {
    public CommandNextLifePart(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.NEXTLIFEPART;

        if (this.args.length == 0 && !this.hasPermission(command)) {
            return;
        } else if (!this.hasPermission(Permission.NEXTLIFEPART_OTHER)) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        if (this.args.length == 0) {
            if (!this.isPlayer()) {
                return;
            }

            this.plugin.getPlayerRepository().getByPlayer(this.sender).thenAcceptAsync(this::sendSuccessMessage);
        } else {
            this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
                if (!bool) {
                    return;
                }

                this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(this::sendSuccessMessage);
            });
        }
    }

    private void sendSuccessMessage(PlayerData playerData) {
        long nextLifePart = playerData.getTimeTillNextMaxHealth();
        this.cs.sendMessage(String.format("§a%s will receive a new §elife part %s§a.", this.cs instanceof Player && ((Player) this.cs).getUniqueId() == playerData.getPlayer().getUniqueId()
                        ? "You"
                        : playerData.getPlayer().getName()
                , nextLifePart == 0 ? "§6now" : "§ain §6" + MessageUtils.getTimeFromTicks(nextLifePart, false, true)));
    }
}
