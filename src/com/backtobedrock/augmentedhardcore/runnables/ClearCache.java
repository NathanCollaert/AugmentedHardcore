package com.backtobedrock.augmentedhardcore.runnables;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearCache extends BukkitRunnable {

    private final AugmentedHardcore plugin;
    private final OfflinePlayer player;

    public ClearCache(OfflinePlayer player) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.player = player;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            this.plugin.getPlayerRepository().removeFromPlayerCache(this.player);
        }
    }
}
