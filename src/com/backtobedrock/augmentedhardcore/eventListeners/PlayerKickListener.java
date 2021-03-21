package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.event.player.PlayerKickEvent;

public class PlayerKickListener extends AbstractEventListener {

    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled())
            return;

        this.plugin.getPlayerRepository().getByPlayer(event.getPlayer()).thenAccept(PlayerData::onKick);
    }

    @Override
    public boolean isEnabled() {
        return this.plugin.getConfigurations().getCombatTagConfiguration().isCombatTagPlayerKickDeath();
    }
}
