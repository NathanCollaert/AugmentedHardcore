package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.guis.ReviveGui;
import com.backtobedrock.augmentedhardcore.utils.PlayerUtils;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        CompletableFuture<PlayerData> reviverFuture = this.plugin.getPlayerRepository().getByPlayer(this.csPlayer);
        CompletableFuture<PlayerData> revivingFuture = this.plugin.getPlayerRepository().getByPlayer(this.player);
        CompletableFuture<ServerData> serverFuture = this.plugin.getServerRepository().getServerData();

        List<?> combined = Stream.of(reviverFuture, revivingFuture, serverFuture).map(CompletableFuture::join).collect(Collectors.toList());

        if (!((PlayerData) combined.get(0)).checkRevivePermissions(this.csPlayer, this.player))
            return;

        PlayerUtils.openInventory(this.csPlayer, new ReviveGui(this.csPlayer, ((PlayerData) combined.get(0)), this.player, ((PlayerData) combined.get(1)), ((ServerData) combined.get(2))).getInventory());
    }
}
