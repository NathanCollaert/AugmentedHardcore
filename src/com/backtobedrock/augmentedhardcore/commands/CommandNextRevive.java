package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNextRevive extends AbstractCommand {
    public CommandNextRevive(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.NEXTREVIVE;

        if (this.args.length == 0 && !this.hasPermission(command)) {
            return;
        } else if (!this.hasPermission(Permission.NEXTREVIVE_OTHER)) {
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
        long reviveCooldown = playerData.getTimeTillNextRevive();
        this.cs.sendMessage(String.format("§a%s will be able to §erevive %s§a.", this.cs instanceof Player && ((Player) this.cs).getUniqueId().toString().equals(playerData.getPlayer().getUniqueId().toString())
                        ? "You"
                        : playerData.getPlayer().getName()
                , reviveCooldown == 0 ? "§6now" : "§ain §6" + MessageUtils.getTimeFromTicks(reviveCooldown, TimePattern.LONG)));
    }
}
