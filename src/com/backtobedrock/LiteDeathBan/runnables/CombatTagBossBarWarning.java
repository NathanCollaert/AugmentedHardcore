package com.backtobedrock.LiteDeathBan.runnables;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CombatTagBossBarWarning extends BukkitRunnable {

    private final LiteDeathBan plugin;

    private final int total;
    private int counter;
    private final Player plyr;
    private final BossBar bar;

    public CombatTagBossBarWarning(LiteDeathBan plugin, int counter, Player plyr, String taggedBy) {
        this.plugin = plugin;
        if (counter < 1) {
            throw new IllegalArgumentException("counter must be greater than 1");
        } else {
            this.total = counter;
            this.counter = counter;
            this.plyr = plyr;
            this.bar = Bukkit.createBossBar(this.plugin.getMessages().getOnCombatTaggedBossBar(plyr.getName(), taggedBy, total), BarColor.RED, BarStyle.SOLID);
            this.plugin.addBar(plyr.getUniqueId(), this.bar);
            bar.addPlayer(plyr);
            bar.setProgress(1);
            bar.setVisible(true);
        }
    }

    @Override
    public void run() {
        if (this.counter > 0) {
            this.bar.setProgress((double) 1 / this.total * this.counter);
            this.counter--;
        } else {
            this.plugin.removeFromTagList(plyr.getUniqueId());
            this.cancel();
        }
    }
}
