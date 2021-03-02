package com.backtobedrock.LiteDeathBan.runnables;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearCache extends BukkitRunnable {

    private final LiteDeathBan plugin;
    private final OfflinePlayer player;

    public ClearCache(OfflinePlayer player) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.player = player;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            this.plugin.getPlayerRepository().removeFromPlayerCache(player);
        }
    }
}
