package com.backtobedrock.augmentedhardcore.eventListeners;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.utils.UpdateUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener extends AbstractEventListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(PlayerData::onJoin);

        if (player.isOp()) {
            Bukkit.getScheduler().runTaskAsynchronously(JavaPlugin.getPlugin(AugmentedHardcore.class), () -> UpdateUtils.getVersion(71483, version -> {
                if (!this.plugin.getDescription().getVersion().equalsIgnoreCase(version)) {
                    player.sendMessage(String.format("§eA new version (§f%s§e) of §f%s§e is available on Spigot.org. Your current version is §f%s§e.", version, this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion()));
                }
            }));
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
