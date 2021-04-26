package com.backtobedrock.augmentedhardcore.runnables;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class BanExpiration extends BukkitRunnable {
    private final AugmentedHardcore plugin;
    private final PlayerData playerData;
    private final Ban ban;

    public BanExpiration(PlayerData playerData, Ban ban) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.playerData = playerData;
        this.ban = ban;
    }


    public void start() {
        this.playerData.setBanExpiration(this);
        this.runTaskLaterAsynchronously(this.plugin, Math.max(5, MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), this.ban.getExpirationDate()), TimeUnit.SECONDS)));
    }

    public void finish() {
        this.stop();
        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> {
            serverData.removeBan(this.playerData.getPlayer());

            if (this.playerData.getPlayer().getPlayer() != null) {
                this.playerData.getPlayer().getPlayer().sendMessage("§aYou are now able to use the §n/deathbanreset§r §acommand to start playing again.");
            }
        });
    }

    public void stop() {
        this.cancel();
    }

    @Override
    public void run() {
        this.finish();
    }
}
