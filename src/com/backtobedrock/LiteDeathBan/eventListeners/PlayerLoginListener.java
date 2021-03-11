package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.domain.Ban;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerLoginListener extends AbstractEventListener {

    @EventHandler
    public void OnPlayerLogin(PlayerLoginEvent e) {
        this.plugin.getServerRepository().getServerData(data -> {
            //if player is banned from plugin, show custom ban message
            Ban ban = data.getBan(e.getPlayer());
            if (e.getResult() == PlayerLoginEvent.Result.KICK_BANNED && ban != null) {
                e.setKickMessage(ban.getBanMessage());
            }
            //check if death ban and actual ban are still in sync
            else if (ban != null) {
                data.removeBan(e.getPlayer());
            }
        });
    }
}
