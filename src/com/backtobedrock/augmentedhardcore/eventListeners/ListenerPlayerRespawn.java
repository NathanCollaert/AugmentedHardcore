package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ListenerPlayerRespawn extends AbstractEventListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(e -> e.onRespawn(player)).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
