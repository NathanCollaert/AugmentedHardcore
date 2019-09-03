package com.backtobedrock.LiteDeathBan.helperClasses;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Nathanisme
 */
public class CombatLogChatWarning extends BukkitRunnable {

    private final LiteDeathBan plugin;
    private final Player plyr;

    public CombatLogChatWarning(LiteDeathBan plugin, Player plyr) {
        this.plugin = plugin;
        this.plyr = plyr;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        this.plugin.removeFromTagList(plyr.getUniqueId());
        super.cancel(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        this.plugin.removeFromTagList(plyr.getUniqueId());
        plyr.spigot().sendMessage(new ComponentBuilder("Your combat tag has run out...").create());
    }

}
