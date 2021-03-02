package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerQuitListener implements Listener {
    private final LiteDeathBan plugin;

    public PlayerQuitListener() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        PlayerData data = this.plugin.getPlayerRepository().removeFromPlayerCache(player);
        if (data != null)
            data.onLeave();
    }
}
