package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import javafx.util.Pair;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class CommandDeathBanReset extends AbstractCommand {
    public CommandDeathBanReset(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.DEATHBANRESET;

        if (!this.hasPermission(command)) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        if (!this.isPlayer()) {
            return;
        }

        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> {
            Pair<Integer, Ban> banPair = serverData.getBan(this.sender);
            if (banPair != null) {
                this.sender.sendMessage(String.format("§cYou cannot use this command for another %s.", MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), banPair.getValue().getExpirationDate()), TimeUnit.SECONDS), false, true)));
                return;
            }

            Bukkit.getScheduler().runTask(this.plugin, () -> this.sender.setGameMode(GameMode.SURVIVAL));
            this.plugin.getPlayerRepository().getByPlayer(this.sender).thenAcceptAsync(playerData -> {
                if (!playerData.isSpectatorBanned()) {
                    this.sender.sendMessage("§cYou are currently not spectator death banned.");
                    return;
                }

                playerData.setSpectatorBanned(false);
                playerData.onJoin();
                playerData.onRespawn();
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }
}
