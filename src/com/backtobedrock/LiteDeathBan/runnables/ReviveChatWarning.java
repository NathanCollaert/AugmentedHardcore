package com.backtobedrock.LiteDeathBan.runnables;

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
    public void run() {
        this.plugin.removeFromConfirmation(plyr.getUniqueId());
    }
}
