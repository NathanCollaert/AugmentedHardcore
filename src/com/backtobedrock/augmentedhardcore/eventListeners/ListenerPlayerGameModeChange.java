package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utils.MessageUtils;
import javafx.util.Pair;
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
        Pair<Integer, Ban> ban = this.plugin.getServerRepository().getServerDataSync().getBan(player);

        if (ban != null && event.getNewGameMode() != GameMode.SPECTATOR) {
            player.sendMessage(String.format("§cCannot change game mode, you are still banned for another %s.", MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(LocalDateTime.now(), ban.getValue().getExpirationDate()), TimeUnit.SECONDS), TimePattern.LONG)));
            event.setCancelled(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
