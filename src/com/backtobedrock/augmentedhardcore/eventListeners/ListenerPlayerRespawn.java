package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ListenerPlayerRespawn extends AbstractEventListener {

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent event) {
        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAcceptAsync(PlayerData::onRespawn);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
