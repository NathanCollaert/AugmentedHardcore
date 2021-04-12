package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickListener extends AbstractEventListener {

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAcceptAsync(PlayerData::onKick);
    }

    @Override
    public boolean isEnabled() {
        return (this.plugin.getConfigurations().getCombatTagConfiguration().isUseCombatTag() && !this.plugin.getConfigurations().getCombatTagConfiguration().isCombatTagPlayerKickDeath());
    }
}
