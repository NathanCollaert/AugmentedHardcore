package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.enums.DamageCause;
import com.backtobedrock.augmentedhardcore.utils.BanUtils;
import com.backtobedrock.augmentedhardcore.utils.EventUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener extends AbstractEventListener {

    @EventHandler
    public void PlayerDeathListener(PlayerDeathEvent e) {
        Player player = e.getEntity();
        EntityDamageEvent damageEvent = player.getLastDamageCause();

        if (damageEvent == null) {
            return;
        }

        this.plugin.getPlayerRepository().getByPlayer(player).thenAccept(playerData -> {
            DamageCause cause = EventUtils.getDamageCauseFromDamageEvent(playerData, damageEvent);

            if (cause == null)
                return;

            Killer killer = playerData.isReviving() ? playerData.getReviving() : EventUtils.getDamageEventKiller(damageEvent), tagger = playerData.getCombatTagger();
            if (killer != null && killer.equals(tagger))
                tagger = null;
            playerData.onDeath(player, BanUtils.getBan(player, playerData, cause, killer, tagger, e.getDeathMessage(), EventUtils.getDamageCauseTypeFromEntityDamageEvent(damageEvent)));
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
