package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class ListenerPlayerGameModeChange extends AbstractEventListener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Ban ban = this.plugin.getServerRepository().getServerDataSync().getBan(player).getValue();

        if (ban != null && event.getNewGameMode() != GameMode.SPECTATOR) {
            player.sendMessage(String.format("§cYou are still banned for another %s. Cannot change gamemode.", MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), ban.getExpirationDate()), TimeUnit.SECONDS), false, true)));
            event.setCancelled(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
