package com.backtobedrock.LiteDeathBan.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener extends AbstractEventListener {

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();

        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onRespawn(player);
        });
    }
}
