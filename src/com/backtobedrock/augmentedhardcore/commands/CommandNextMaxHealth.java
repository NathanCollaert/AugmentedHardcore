package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.ExecutionException;

public class CommandNextMaxHealth extends AbstractCommand {
    public CommandNextMaxHealth(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() throws ExecutionException, InterruptedException {
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

            this.plugin.getPlayerRepository().getByPlayer(this.sender).thenAcceptAsync(this::sendSuccessMessage).get();
        } else {
            this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
                if (!bool) {
                    return;
                }

                try {
                    this.plugin.getPlayerRepository().getByPlayer(this.target).thenAcceptAsync(this::sendSuccessMessage).get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }).get();
        }
    }

    private void sendSuccessMessage(PlayerData playerData) {
        long nextMaxHealth = playerData.getTimeTillNextMaxHealth();
        this.cs.sendMessage(String.format("§a%s will receive extra §emax health %s§a", this.cs instanceof Player && ((Player) this.cs).getUniqueId() == playerData.getPlayer().getUniqueId()
                        ? "You"
                        : playerData.getPlayer().getName()
                , nextMaxHealth == 0 ? "§6now" : "§ain §6" + MessageUtils.getTimeFromTicks(nextMaxHealth, false, true)));
    }
}
