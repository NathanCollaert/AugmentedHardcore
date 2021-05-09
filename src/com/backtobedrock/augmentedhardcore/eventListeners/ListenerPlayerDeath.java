package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ListenerPlayerDeath extends AbstractEventListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerDeathListener(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(playerData -> playerData.onDeath(event)).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
