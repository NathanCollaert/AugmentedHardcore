package com.backtobedrock.LiteDeathBan.eventListeners;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class EntityDeathListener implements Listener {
    private final LiteDeathBan plugin;

    public EntityDeathListener() {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
    }

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;

        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
        if (!(entityDamageByEntityEvent.getDamager() instanceof Player))
            return;

        Player player = (Player) entityDamageByEntityEvent.getDamager();

        this.plugin.getPlayerRepository().getByPlayer(player, data -> {
            data.onEntityKill(player, event.getEntity().getType());
        });
    }
}
