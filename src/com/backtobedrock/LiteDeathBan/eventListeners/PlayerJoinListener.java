package com.backtobedrock.LiteDeathBan.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener extends AbstractEventListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onJoin(player);
        });
    }
}