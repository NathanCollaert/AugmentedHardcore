package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class ListenerPlayerJoin extends AbstractEventListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(PlayerData::onJoin).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
