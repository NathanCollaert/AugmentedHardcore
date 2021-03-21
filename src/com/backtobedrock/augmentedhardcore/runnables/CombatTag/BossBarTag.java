package com.backtobedrock.augmentedhardcore.runnables.CombatTag;

import com.backtobedrock.augmentedhardcore.domain.Killer;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.notifications.BossBarNotification;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BossBarTag extends AbstractCombatTag {
    private final BossBar bar;
    private final BossBarNotification bossBarNotification;

    public BossBarTag(Player player, PlayerData playerData, Killer tagger, BossBarNotification bossBarNotification) {
        super(player, playerData, tagger);
        this.bossBarNotification = bossBarNotification;
        this.bar = Bukkit.createBossBar(this.placeholderReplacements(this.bossBarNotification.getText()), bossBarNotification.getColor(), bossBarNotification.getStyle());
    }

    @Override
    protected void timerTask() {
        this.bar.setProgress((double) 1 / this.time * this.timer);
        this.bar.setTitle(this.placeholderReplacements(this.bossBarNotification.getText()));
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
