package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.utils.UpdateUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener extends AbstractEventListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(playerData -> playerData.onJoin(player)).handleAsync((v, t) -> {
            t.printStackTrace();
            return null;
        });

        if (player.isOp()) {
            UpdateUtils.getVersion(71483, version -> {
                if (!this.plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                    player.sendMessage(String.format("§eA new version (§f%s§e) of §f%s§e is available on Spigot.org. Your current version is §f%s§e.", version, this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion()));
                }
            });
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
