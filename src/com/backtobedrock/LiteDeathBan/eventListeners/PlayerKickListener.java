package com.backtobedrock.LiteDeathBan.eventListeners;

import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickListener extends AbstractEventListener {

    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled())
            return;

        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer(), data -> {
            data.onKick();
        });
    }
}
