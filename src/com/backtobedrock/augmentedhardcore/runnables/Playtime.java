package com.backtobedrock.augmentedhardcore.runnables;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Playtime extends BukkitRunnable {

    private final AugmentedHardcore plugin;
    private final Player player;
    private final PlayerData data;

    private int counter;

    public Playtime(Player player, PlayerData playerData) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.player = player;
        this.data = playerData;
        this.counter = 0;
    }

    public void start() {
        this.runTaskTimer(this.plugin, 20, 20);
    }

    public void stop() {
        this.decreaseTime();
        this.cancel();
    }

    @Override
    public void run() {
        this.counter++;
        if (this.counter == 60) {
            this.decreaseTime();
        }
    }

    private void decreaseTime() {
        this.data.decreaseTimeTillNextLifePart(player, counter);
        this.counter = 0;
    }
}
