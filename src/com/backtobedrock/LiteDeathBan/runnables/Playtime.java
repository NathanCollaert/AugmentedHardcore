package com.backtobedrock.LiteDeathBan.runnables;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Playtime extends BukkitRunnable {

    private final LiteDeathBan plugin;
    private final Player player;

    private int counter;

    public Playtime(Player player) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.player = player;
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
        this.plugin.getPlayerRepository().getByPlayer(this.player, data -> {
            data.decreaseTimeTillNextLifePart(player, counter);
            this.counter = 0;
        });
    }
}
