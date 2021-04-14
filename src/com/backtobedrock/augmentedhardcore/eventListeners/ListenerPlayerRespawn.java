package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.concurrent.ExecutionException;

public class ListenerPlayerRespawn extends AbstractEventListener {

    @EventHandler
    public void OnPlayerRespawn(PlayerRespawnEvent event) throws ExecutionException, InterruptedException {
        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAcceptAsync(PlayerData::onRespawn).get();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
