package com.backtobedrock.augmentedhardcore.runnables.Playtime;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractPlaytime extends BukkitRunnable {

    protected final AugmentedHardcore plugin;
    protected final Player player;
    protected final PlayerData data;
    protected final int period;

    public AbstractPlaytime(Player player, PlayerData playerData) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.player = player;
        this.data = playerData;
        this.period = 20;
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
    }

    protected abstract void timerTask();
}
