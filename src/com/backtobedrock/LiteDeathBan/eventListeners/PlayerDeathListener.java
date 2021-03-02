package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.enums.DamageCause;
import com.backtobedrock.LiteDeathBan.utils.BanUtils;
import com.backtobedrock.LiteDeathBan.utils.EventUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class PlayerDeathListener implements Listener {
    private final LiteDeathBan plugin;

    public PlayerDeathListener() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    @EventHandler
    public void PlayerDeathListener(PlayerDeathEvent e) {
        Player player = e.getEntity();
        EntityDamageEvent damageEvent = player.getLastDamageCause();
        if (damageEvent == null) {
            return;
        }

        //get death cause
        final String lastDamageCauseName = damageEvent.getCause().toString().toUpperCase();
        final String lastDamageCauseId = EventUtils.isEntityDamageEventFromPlayer(damageEvent) ? "PLAYER_" + lastDamageCauseName : lastDamageCauseName;
        try {
            //see if death cause exists
            final DamageCause damageCause = DamageCause.valueOf(lastDamageCauseId);
            this.plugin.getPlayerRepository().getByPlayer(player, data -> {
                //TODO: set inCombatWith when combat logged
                data.onDeath(player, BanUtils.getBan(player, damageCause, EventUtils.getDamageEventKiller(damageEvent), null, e.getDeathMessage(), EventUtils.getDamageCauseTypeFromEntityDamageEvent(damageEvent)));
            });
        } catch (IllegalArgumentException exception) {
            this.plugin.getLogger().log(Level.SEVERE, String.format("%s was not a known damage type, please report this to the plugin author for an update.", lastDamageCauseId));
        }
    }
}
