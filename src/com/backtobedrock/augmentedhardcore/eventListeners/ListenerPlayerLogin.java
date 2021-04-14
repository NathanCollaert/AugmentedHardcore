package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

public class ListenerPlayerLogin extends AbstractEventListener {

    @EventHandler
    public void OnPlayerLogin(PlayerLoginEvent e) {
        ServerData serverData = this.plugin.getServerRepository().getServerDataSync();
        Ban ban = serverData.getBan(e.getPlayer());

        if (e.getResult() == PlayerLoginEvent.Result.KICK_BANNED && ban != null) {
            e.setKickMessage(ban.getBanMessage());
        }
        //check if death ban and actual ban are still in sync
        else if (ban != null) {
            serverData.removeBan(e.getPlayer());
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
