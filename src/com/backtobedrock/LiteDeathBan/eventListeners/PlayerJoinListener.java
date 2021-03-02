package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {
    private final LiteDeathBan plugin;

    public PlayerJoinListener() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onJoin(player);
        });
    }
}
