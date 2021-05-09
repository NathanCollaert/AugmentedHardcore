package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.javatuples.Pair;

public class ListenerPlayerLogin extends AbstractEventListener {

    @EventHandler
    public void OnPlayerLogin(PlayerLoginEvent event) {
        ServerData serverData = this.plugin.getServerRepository().getServerDataSync();
        Player player = event.getPlayer();
        Pair<Integer, Ban> ban = serverData.getBan(player);

        if (ban == null) {
            return;
        }

        boolean hasPermission = player.hasPermission(Permission.BYPASS_BAN_SPECTATOR.getPermissionString());

        if (event.getResult() != PlayerLoginEvent.Result.KICK_BANNED) {
            if (!hasPermission) {
                serverData.removeBan(player);
            }
            return;
        }

        if (hasPermission) {
            event.allow();
            return;
        }

        event.setKickMessage(ban.getValue1().getBanMessage());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
