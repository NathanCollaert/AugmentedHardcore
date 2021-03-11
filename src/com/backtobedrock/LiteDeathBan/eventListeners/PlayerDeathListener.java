package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCause;
import com.backtobedrock.LiteDeathBan.utils.BanUtils;
import com.backtobedrock.LiteDeathBan.utils.EventUtils;
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

        DamageCause cause = EventUtils.getDamageCauseFromDamageEvent(player, damageEvent);

        if (cause == null)
            return;

        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            Killer killer = data.isReviving() ? data.getReviving() : EventUtils.getDamageEventKiller(damageEvent), tagger = data.getCombatTagger();
            if (killer != null && killer.equals(tagger))
                tagger = null;
            data.onDeath(player, BanUtils.getBan(player, cause, killer, tagger, e.getDeathMessage(), EventUtils.getDamageCauseTypeFromEntityDamageEvent(damageEvent)));
        });
    }
}
