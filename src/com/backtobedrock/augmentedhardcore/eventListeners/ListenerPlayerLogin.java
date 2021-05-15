package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.runnables.Unban;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;

public class ListenerPlayerLogin extends AbstractEventListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnPlayerLogin(PlayerLoginEvent event) {
        ServerData serverData = this.plugin.getServerRepository().getServerDataSync();
        Player player = event.getPlayer();
        Unban unban = serverData.getBan(player.getUniqueId());
        Ban ban;

        if (unban == null) {
            ban = serverData.getOngoingIPBans().get(event.getAddress().getHostAddress());
            if (ban == null) {
                return;
            }
        } else {
            ban = unban.getBan().getValue1();
        }

        if (player.hasPermission(Permission.BYPASS_BAN_SPECTATOR.getPermissionString())) {
            return;
        }

        String message = unban == null ? MessageUtils.replacePlaceholders(this.plugin.getMessages().getIPBanMessage(), ban.getPlaceholdersReplacements()) : ban.getBanMessage();
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, message);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
