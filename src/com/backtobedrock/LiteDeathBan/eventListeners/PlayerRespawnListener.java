package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerRespawnListener implements Listener {
    private final LiteDeathBan plugin;

    public PlayerRespawnListener() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onRespawn(player);
        });
    }
}
