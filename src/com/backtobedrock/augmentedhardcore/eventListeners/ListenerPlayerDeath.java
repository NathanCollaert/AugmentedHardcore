package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.concurrent.ExecutionException;

public class ListenerPlayerDeath extends AbstractEventListener {

    @EventHandler
    public void PlayerDeathListener(PlayerDeathEvent event) throws ExecutionException, InterruptedException {
        Player player = event.getEntity();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(playerData -> {
            try {
                playerData.onDeath(event);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }).get();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
