package com.backtobedrock.LiteDeathBan.eventHandlers;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author PC_Nathan
 */
public class LiteDeathBanEventHandlers implements Listener {

    private final LiteDeathBan plugin;

    public LiteDeathBanEventHandlers(LiteDeathBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPlayedBefore()) {
            Player plyr = e.getPlayer();
        }
    }
    
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent e) {
        Player plyr = e.getEntity();
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player plyr = e.getPlayer();
    }
}
