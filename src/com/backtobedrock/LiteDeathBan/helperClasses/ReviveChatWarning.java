package com.backtobedrock.LiteDeathBan.helperClasses;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReviveChatWarning extends BukkitRunnable {

    private final LiteDeathBan plugin;
    private final Player plyr;

    public ReviveChatWarning(LiteDeathBan plugin, Player plyr) {
        this.plugin = plugin;
        this.plyr = plyr;
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        this.plugin.removeFromConfirmation(plyr.getUniqueId());
    }
}
