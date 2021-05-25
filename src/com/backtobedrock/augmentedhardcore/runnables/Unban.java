package com.backtobedrock.augmentedhardcore.runnables;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.javatuples.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class Unban extends BukkitRunnable {
    private final AugmentedHardcore plugin;
    private final OfflinePlayer player;
    private final Pair<Integer, Ban> ban;

    public Unban(OfflinePlayer player, Pair<Integer, Ban> ban) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.player = player;
        this.ban = ban;
    }

    public void start() {
        this.runTaskLaterAsynchronously(this.plugin, MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), this.ban.getValue1().getExpirationDate()), TimeUnit.SECONDS));
    }

    private void stop() {
        try {
            this.cancel();
        } catch (IllegalStateException e) {
            //ignore not scheduled yet
        }
    }

    public void finish() {
        this.stop();
        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> serverData.removeBan(this.player));
    }

    @Override
    public void run() {
        this.finish();
    }

    public Pair<Integer, Ban> getBan() {
        return ban;
    }
}
