package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandNextMaxHealth extends AbstractCommand {
    public CommandNextMaxHealth(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.NEXTMAXHEALTH;

        if (this.args.length == 0 && !this.hasPermission(command)) {
            return;
        } else if (!this.hasPermission(Permission.NEXTMAXHEALTH_OTHER)) {
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
        long nextMaxHealth = playerData.getTimeTillNextMaxHealth();
        this.cs.sendMessage(String.format("§a%s will receive extra §emax health %s§a", this.cs instanceof Player && ((Player) this.cs).getUniqueId().toString().equals(playerData.getPlayer().getUniqueId().toString())
                        ? "You"
                        : playerData.getPlayer().getName()
                , nextMaxHealth == 0 ? "§6now" : "§ain §6" + MessageUtils.getTimeFromTicks(nextMaxHealth, TimePattern.LONG)));
    }
}
