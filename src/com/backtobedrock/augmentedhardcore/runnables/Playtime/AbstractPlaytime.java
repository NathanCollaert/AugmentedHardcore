package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractPlaytime extends BukkitRunnable {

    protected final AugmentedHardcore plugin;
    protected final PlayerData data;
    protected final int period;
    protected int timer;

    public AbstractPlaytime(PlayerData playerData) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.data = playerData;
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
        this.timerTask();
        this.timer++;
        if (this.timer == 60) {
            this.plugin.getPlayerRepository().updatePlayerData(this.data);
            this.timer = 0;
        }
    }

    protected abstract void timerTask();
}
