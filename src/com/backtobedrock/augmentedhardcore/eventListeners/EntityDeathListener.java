package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityDeathListener extends AbstractEventListener {

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent))
            return;

        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
        if (!(entityDamageByEntityEvent.getDamager() instanceof Player))
            return;

        Player player = (Player) entityDamageByEntityEvent.getDamager();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAccept(playerData -> playerData.onEntityKill(player, event.getEntity().getType()));
    }

    @Override
    public boolean isEnabled() {
        return (this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isLifePartsOnKill() || this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth());
    }
}