package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class PlayerRegainHealthListener extends AbstractEventListener {

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) event.getEntity();
        EntityRegainHealthEvent.RegainReason reason = event.getRegainReason();
        if (this.plugin.getConfigurations().getMaxHealthConfiguration().isDisableArtificialRegeneration() && (reason == EntityRegainHealthEvent.RegainReason.EATING || reason == EntityRegainHealthEvent.RegainReason.MAGIC || reason == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN)) {
            if (player.hasPermission(Permission.BYPASS_ARTIFICIALREGENERATION.getPermissionString())) {
                return;
            }
            event.setCancelled(true);
        }
    }

    @Override
    public boolean isEnabled() {
        return (this.plugin.getConfigurations().getMaxHealthConfiguration().isDisableArtificialRegeneration());
    }
}
