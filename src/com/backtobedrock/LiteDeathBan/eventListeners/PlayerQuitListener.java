package com.backtobedrock.LiteDeathBan.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener extends AbstractEventListener {

    @EventHandler
    public void OnPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onLeave(player);
        });
    }
}
