package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractPlaytime extends BukkitRunnable {

    protected final AugmentedHardcore plugin;
    protected final PlayerData playerData;
    protected final int period;
    protected int timer;
    protected Player player;

    public AbstractPlaytime(PlayerData playerData, Player player) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.playerData = playerData;
        this.player = player;
        this.period = 20;
        this.timer = 0;
    }

    public void start() {
        this.runTaskTimerAsynchronously(this.plugin, this.period, this.period);
    }

    public void stop() {
        this.cancel();
    }

    @Override
    public void run() {
        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> {
            if (serverData.isDeathBanned(this.player.getUniqueId())) {
                return;
            }

            this.timerTask();
            this.timer++;
            if (this.timer == 300) {
                this.plugin.getPlayerRepository().updatePlayerData(this.playerData);
                this.timer = 0;
            }
        });
    }

    protected abstract void timerTask();
}
