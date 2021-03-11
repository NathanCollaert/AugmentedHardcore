package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.utils.EventUtils;
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

        if (player.getHealth() <= event.getFinalDamage()) {
            return;
        }

        if (!this.plugin.getConfigurations().getCombatTagConfiguration().isPlayerCombatTag() && EventUtils.isEntityDamageByEntityEventFromPlayer(event)) {
            return;
        } else if (!this.plugin.getConfigurations().getCombatTagConfiguration().isMonsterCombatTag() && EventUtils.isEntityDamageByEntityEventFromMonster(event)) {
            return;
        }

        Killer tagger = EventUtils.getDamageEventKiller(event);

        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onCombatTag(player, tagger);
        });
    }
}
