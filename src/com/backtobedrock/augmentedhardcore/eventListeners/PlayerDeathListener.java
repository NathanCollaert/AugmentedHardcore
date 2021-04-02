package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends AbstractEventListener {

    @EventHandler
    public void PlayerDeathListener(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(playerData -> playerData.onDeath(player, event)).handleAsync((v, t) -> {
            t.printStackTrace();
            return null;
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
