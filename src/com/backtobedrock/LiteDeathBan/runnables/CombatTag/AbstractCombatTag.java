package com.backtobedrock.LiteDeathBan.runnables.CombatTag;

import com.backtobedrock.LiteDeathBan.LiteDeathBan;
import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AbstractCombatTag extends BukkitRunnable {

    protected final LiteDeathBan plugin;
    protected final int time;
    protected final Player player;
    protected final PlayerData playerData;
    protected int timer;
    protected Killer tagger;

    public AbstractCombatTag(Player player, PlayerData playerData, Killer tagger) {
        this.plugin = JavaPlugin.getPlugin(LiteDeathBan.class);
        this.time = this.plugin.getConfigurations().getCombatTagConfiguration().getCombatTagTime() * 20;
        this.timer = this.time;
        this.tagger = tagger;
        this.playerData = playerData;
        this.player = player;
    }

    public Killer getTagger() {
        return tagger;
    }

    public void start() {
        this.runTaskTimerAsynchronously(this.plugin, 0, 1);
    }

    public void stop() {
        this.cancel();
        this.playerData.removeFromCombatTag(this);
    }

    public void restart(Killer tagger) {
        this.timer = this.time;
        this.tagger = tagger;
    }

    protected void timerTask() {
    }

    @Override
    public void run() {
        if (this.timer > 0) {
            if (this.timer % 20 == 0)
                this.timerTask();
            this.timer--;
        } else {
            this.stop();
        }
    }
}
