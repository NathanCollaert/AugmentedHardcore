package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.javatuples.Pair;

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

                if (this.target.getPlayer() == null) {
                    this.cs.sendMessage(String.format("%s will have to be online in order to death ban reset.", this.target.getName()));
                    return;
                }

                this.runCommand(this.target.getPlayer());
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });
        }
    }

    private void runCommand(Player player) {
        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> {
            Pair<Integer, Ban> banPair = serverData.getBan(player);
            if (banPair != null) {
                this.cs.sendMessage(String.format("§c%s cannot be unbanned for another %s.", this.cs instanceof Player && ((Player) this.cs).getUniqueId().toString().equals(player.getUniqueId().toString()) ? "You" : player.getName(), MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), banPair.getValue1().getExpirationDate()), TimeUnit.SECONDS), TimePattern.LONG)));
                return;
            }

            Bukkit.getScheduler().runTask(this.plugin, () -> player.setGameMode(GameMode.SURVIVAL));
            this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(playerData -> {
                if (!playerData.isSpectatorBanned()) {
                    this.cs.sendMessage(String.format("§c%s not currently spectator death banned.", this.cs instanceof Player && ((Player) this.cs).getUniqueId().toString().equals(player.getUniqueId().toString()) ? "You are" : player.getName() + " is"));
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
