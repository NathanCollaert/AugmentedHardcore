package com.backtobedrock.LiteDeathBan.runnables.CombatTag;

import com.backtobedrock.LiteDeathBan.domain.Killer;
import com.backtobedrock.LiteDeathBan.domain.data.PlayerData;
import com.backtobedrock.LiteDeathBan.domain.notifications.BossBarNotification;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarTag extends AbstractCombatTag {
    private final BossBar bar;

    public BossBarTag(Player player, PlayerData playerData, Killer tagger, BossBarNotification bossBarNotification) {
        super(player, playerData, tagger);
        this.bar = Bukkit.createBossBar(bossBarNotification.getText(), bossBarNotification.getColor(), bossBarNotification.getStyle());
    }

    @Override
    protected void timerTask() {
        this.bar.setProgress((double) 1 / this.time * this.timer);
    }

    @Override
    public void start() {
        super.start();
        this.bar.addPlayer(this.player);
        this.bar.setVisible(true);
    }

    @Override
    public void stop() {
        super.stop();
        this.bar.removePlayer(this.player);
        this.bar.setVisible(false);
    }

    @Override
    public void restart(Killer tagger) {
        super.restart(tagger);
        this.bar.setProgress(1);
    }
}
