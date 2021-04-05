package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        CompletableFuture<PlayerData> playerFuture = this.plugin.getPlayerRepository().getByPlayer(this.player);
        CompletableFuture<ServerData> serverFuture = this.plugin.getServerRepository().getServerData();

        List<?> combined = Stream.of(playerFuture, serverFuture).map(CompletableFuture::join).collect(Collectors.toList());

        if (((PlayerData) combined.get(0)).unDeathBan(this.cs, (ServerData) combined.get(1), this.player, false)) {
            this.cs.sendMessage(String.format("§a%s has successfully been unbanned from a death ban.", this.player.getName()));
        }
    }
}
