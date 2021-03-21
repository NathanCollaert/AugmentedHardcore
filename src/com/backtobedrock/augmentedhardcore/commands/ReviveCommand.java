package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.guis.ReviveGui;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.concurrent.CompletableFuture;

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

        reviverFuture.thenCompose(reviverData -> revivingFuture.thenCompose(revivingData -> serverFuture.thenAccept(serverData -> {
            if (!reviverData.checkRevivePermissions(this.csPlayer, this.player))
                return;

            Bukkit.getScheduler().runTask(plugin, () -> this.csPlayer.openInventory(new ReviveGui(this.csPlayer, reviverData, this.player, revivingData, serverData).getInventory()));
        })));
    }
}
