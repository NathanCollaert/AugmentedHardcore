package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import javafx.util.Pair;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;

public class ListenerPlayerLogin extends AbstractEventListener {

    @EventHandler
    public void OnPlayerLogin(PlayerLoginEvent event) {
        ServerData serverData = this.plugin.getServerRepository().getServerDataSync();
        Player player = event.getPlayer();
        Pair<Integer, Ban> ban = serverData.getBan(player);

        if (event.getResult() == PlayerLoginEvent.Result.KICK_BANNED && ban != null) {
            if (player.hasPermission(Permission.BYPASS_BAN_SPECTATOR.getPermissionString())) {
                if (player.getGameMode() != GameMode.SPECTATOR) {
                    player.setGameMode(GameMode.SPECTATOR);
                }
                event.allow();
            } else {
                event.setKickMessage(ban.getValue().getBanMessage());
            }
        }
        //check if death ban and actual ban are still in sync
        else if (ban != null) {
            serverData.removeBan(player);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
