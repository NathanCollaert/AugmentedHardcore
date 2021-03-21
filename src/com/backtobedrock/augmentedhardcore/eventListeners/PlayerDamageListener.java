package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.utils.EventUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class PlayerDamageListener extends AbstractEventListener {
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntityType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getEntity();

        //check if not dead
        if (player.getHealth() <= event.getFinalDamage()) {
            return;
        }

        if (!this.plugin.getConfigurations().getCombatTagConfiguration().isPlayerCombatTag() && EventUtils.isEntityDamageByEntityEventFromPlayer(event)) {
            return;
        } else if (!this.plugin.getConfigurations().getCombatTagConfiguration().isMonsterCombatTag() && EventUtils.isEntityDamageByEntityEventFromMonster(event)) {
            return;
        }

        Killer tagger = EventUtils.getDamageEventKiller(event);

        this.plugin.getPlayerRepository().getByPlayer(player).thenAccept(playerData -> playerData.onCombatTag(player, tagger));
    }

    @Override
    public boolean isEnabled() {
        return (this.plugin.getConfigurations().getCombatTagConfiguration().isPlayerCombatTag() || this.plugin.getConfigurations().getCombatTagConfiguration().isMonsterCombatTag());
    }
}
